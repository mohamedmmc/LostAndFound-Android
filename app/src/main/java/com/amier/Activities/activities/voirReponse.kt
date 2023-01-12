package com.amier.Activities.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amier.Activities.SwipeGesture
import com.amier.Activities.activities.adapters.ReponseViewAdapter
import com.amier.Activities.api.ApiNotification
import com.amier.Activities.api.ApiReponse
import com.amier.Activities.models.Reponse
import com.amier.Activities.models.User
import com.amier.modernloginregister.R
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_voir_article.*
import kotlinx.android.synthetic.main.activity_voir_reponse.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class voirReponse : AppCompatActivity(), ReponseViewAdapter.OnItemClickListener {
    lateinit var mSharedPref: SharedPreferences
    lateinit var idUser: String
    lateinit var userArticleid: String

    lateinit var adap: ReponseViewAdapter
    lateinit var reponsesss: MutableList<Reponse>
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    var usersSB: MutableList<String> = arrayListOf()

    //lateinit var test: ArticleViewAdapter.OnItemClickListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voir_reponse)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        idUser = mSharedPref.getString("_id","")!!

        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView2.setHasFixedSize(true)



        maquestion.text = intent.getStringExtra("question")!!
        getAllData(intent.getStringExtra("idArticle")!!){ reponses : MutableList<Reponse> ->
            adap = ReponseViewAdapter(reponses,this)
            reponsesss = reponses
            recyclerView2.adapter = ReponseViewAdapter(reponses,this)

        }
        val swipegesture = object : SwipeGesture(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val archiveItem = reponsesss[viewHolder.adapterPosition]
                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder = AlertDialog.Builder(this@voirReponse)
                        builder.setMessage("Etes vous sur de vouloir supprimer la réponse de :  "+archiveItem.user!!.nom!!)
                            .setCancelable(false)
                            .setPositiveButton("Oui") { dialog, id ->
                                val apiInterface = ApiReponse.create()

                                apiInterface.deleteReponse(archiveItem._id).enqueue(object:
                                    Callback<Reponse> {
                                    override fun onResponse(
                                        call: Call<Reponse>,
                                        response: Response<Reponse>
                                    ) {
                                        if(response.isSuccessful){
                                            Log.i("onResponse goooood", response.body().toString())

                                        } else {
                                            Log.i("OnResponse not good", response.body().toString())
                                        }
                                    }
                                    override fun onFailure(call: Call<Reponse>, t: Throwable) {
                                    }
                                })
                                adap.deleteItem(viewHolder.adapterPosition)

                            }
                            .setNegativeButton("No") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()

                    }
                    ItemTouchHelper.RIGHT -> {
                        val notif= User()
                        notif.tokenfb = intent.getStringExtra("tokenfbUser")
                        notif.nom = mSharedPref.getString("nom","")

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

                                } else if (response.code() == 400){
                                    Toast.makeText(applicationContext, "Vous avez déjà repondu a la question !", Toast.LENGTH_LONG).show()
                                }
                            }
                            override fun onFailure(call: Call<User>, t: Throwable) {
                            }
                        })
                        usersSB.add(archiveItem.user!!._id!!)
                        createChannel(usersSB)

                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(recyclerView2)

    }
    private fun createChannel(users: MutableList<String>) {
        val params = GroupChannelParams()

        val operatorId = ArrayList<String>()
        params.setDistinct(true)
        operatorId.add(SendBird.getCurrentUser().userId)

        params.addUserIds(users)
        params.setOperatorUserIds(operatorId)

        GroupChannel.createChannel(params) { groupChannel, e ->
            if (e != null) {
                Log.e("TAG", e.message)
            } else {
                val intent = Intent(this, ChannelActivity::class.java)
                intent.putExtra(EXTRA_CHANNEL_URL, groupChannel.url)
                startActivity(intent)
            }
        }
    }
    private fun getAllData(idUser:String,callback: (MutableList<Reponse>) -> Unit){
        val apiInterface = ApiReponse.create()

        apiInterface.getReponses(idUser).enqueue(object:
            Callback<Reponse> {
            override fun onResponse(
                call: Call<Reponse>,
                response: Response<Reponse>
            ) {
                if(response.isSuccessful){
                    Log.i("onResponse goooood", response.body().toString())
                    return callback(response.body()!!.reponses!!)

                } else {
                    Log.i("OnResponse not good", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Reponse>, t: Throwable) {
            }

        })
    }

    override fun onItemClick(position: Int, property: List<Reponse>) {
        val intent = Intent(applicationContext, DetailUserArticle::class.java)

        intent.putExtra("photo",property[position].user!!.photoProfil!!)
        intent.putExtra("nom",property[position].user!!.nom!!.uppercase() )
        intent.putExtra("prenom",property[position].user!!.prenom!!)
        intent.putExtra("id",property[position].user!!._id)
        intent.putExtra("email",property[position].user!!.email)
        intent.putExtra("photoUser",property[position].user!!.photoProfil)
        intent.putExtra("nomUser",property[position].user!!.nom)
        //intent.putExtra("addresse","")



        startActivity(intent)
    }


}