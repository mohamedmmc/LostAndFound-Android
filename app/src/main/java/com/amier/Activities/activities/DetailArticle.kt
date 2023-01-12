package com.amier.Activities.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.amier.Activities.api.ApiNotification
import com.amier.Activities.api.ApiQuestion
import com.amier.Activities.models.Reponse
import com.amier.Activities.models.User
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.MapsInitializer

import com.sendbird.android.constant.StringSet.core
import java.lang.Exception
import com.google.android.gms.maps.CameraUpdateFactory
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode

import com.sendbird.android.constant.StringSet.core
import com.sendbird.android.constant.StringSet.core
import okhttp3.internal.wait


class DetailArticle : AppCompatActivity()  {
    lateinit var mSharedPref: SharedPreferences
    lateinit var animationView: LottieAnimationView
    private lateinit var mapView : MapView
    private lateinit var map : MapboxMap
    private lateinit var point : LatLng
    private var locationEngine : LocationEngine?=null
    private var locationLayerPlugin : LocationLayerPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)




        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val name = intent.getStringExtra("nom")
        val addresse = intent.getStringExtra("addresse")
        val longi = intent.getDoubleExtra("longi",2000.00)
        val lalti = intent.getDoubleExtra("lalti",2000.00)
        val idd = intent.getStringExtra("_id")
        val userArticleid = intent.getStringExtra("userDetail")
        val questionID = intent.getStringExtra("question")
        val questionTitle = intent.getStringExtra("questionTitle")
        val userArticleNom = intent.getStringExtra("userArticleNom")
        val userArticlePrenom = intent.getStringExtra("userArticlePrenom")
        val userArticlePhoto = intent.getStringExtra("userArticlePhoto")
        val userArticleEmail = intent.getStringExtra("userArticleEmail")
        val question = intent.getStringExtra("question")
        val namee = findViewById<TextView>(R.id.textView4)
        mapView = findViewById(R.id.map_view)
        namee.text = name
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        val photo = findViewById<ImageView>(R.id.imageView)
        val warning = findViewById<TextView>(R.id.textView6)
        val messageReponse = findViewById<TextView>(R.id.messageReponse)
        Glide.with(applicationContext).load(Uri.parse(intent.getStringExtra("photo"))).into(photo)
        animationView = findViewById(R.id.animationNoGps)
        val description = findViewById<TextView>(R.id.editTextTextMultiLine3)
        description.text = intent.getStringExtra("description")

        val type = findViewById<TextView>(R.id.textView5)
        type.text = intent.getStringExtra("type")

        val repondreButton = findViewById<Button>(R.id.repondre)
        val voirProfilButton = findViewById<Button>(R.id.profil)
        if(question == null){

            repondreButton.visibility = View.GONE
        }
        if(userArticleid==mSharedPref.getString("_id",null)){
            voirProfilButton.visibility = View.GONE
            repondreButton.visibility = View.GONE
            warning.text = "C'est votre article"
        }

        Log.i("la lalti est ",lalti.toString())
        Log.i("la longi est ",longi.toString())
        if(longi == 2000.00){
            mapView.visibility = View.GONE
            animationView.visibility = View.VISIBLE
            messageReponse.visibility = View.VISIBLE
            animationView.playAnimation()
            animationView.loop(true)
        }else{

            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync{

                map = it

                map.animateCamera(
                    com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(
                        LatLng(lalti, longi),  13.0))
                point.longitude = longi
                point.latitude = lalti
                map.addMarker(MarkerOptions().position(point))
            }


        }


        voirProfilButton.setOnClickListener {

            val intent = Intent(applicationContext, DetailUserArticle::class.java)
            intent.putExtra("id",userArticleid)
            intent.putExtra("nom",userArticleNom)
            intent.putExtra("prenom",userArticlePrenom)
            intent.putExtra("email",userArticleEmail)
            intent.putExtra("photo",userArticlePhoto)
            startActivity(intent)
        }


        repondreButton.setOnClickListener {
            showdialog(mSharedPref.getString("_id","")!!,questionTitle!!,questionID!!)
        }
        

    }

    fun showdialog(idd:String,question:String,questionID:String){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Repondez à la question ! ")

        builder.setMessage(question)
        val input = EditText(this)
        input.setHint("Votre réponse...")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            val reponse= Reponse()
            reponse.description = input.text.toString()
            reponse.userr = idd
            Log.i("la reponse",reponse.toString())
            val apiInterface = ApiQuestion.create()
            apiInterface.postReponse(questionID,reponse).enqueue(object:
                Callback<Reponse> {
                override fun onResponse(
                    call: Call<Reponse>,
                    response: Response<Reponse>
                ) {
                    if(response.isSuccessful){
                        val notif= User()
                        notif.tokenfb = intent.getStringExtra("tokenfbUser")
                        notif.nom = mSharedPref.getString("nom","")
                        notif.type = "reponse"
                        Log.i("notif qu'on va envoyer est : ",notif.toString())
                        val apiInterface = ApiNotification.create()
                        apiInterface.pushNotif(notif).enqueue(object:
                            Callback<User> {
                            override fun onResponse(
                                call: Call<User>,
                                response: Response<User>
                            ) {
                                if(response.isSuccessful){

                                    Toast.makeText(applicationContext, "Notification envoyé avec succés", Toast.LENGTH_LONG).show()

                                }
                            }
                            override fun onFailure(call: Call<User>, t: Throwable) {
                            }
                        })
                        Toast.makeText(applicationContext, "Message envoyé avec succés", Toast.LENGTH_LONG).show()

                    } else if (response.code() == 400){
                        Toast.makeText(applicationContext, "Vous avez déjà repondu a la question !", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Reponse>, t: Throwable) {
                }
            })
        })
        builder.setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }


    override fun onStart() {
        super.onStart()
        if (PermissionsManager.areLocationPermissionsGranted (  this)) {
            locationEngine?.requestLocationUpdates ()
            locationLayerPlugin?.onStart()
        }
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationEngine?.deactivate()
        mapView.onDestroy()
    }
    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation

    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView,map,locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL

    }
}