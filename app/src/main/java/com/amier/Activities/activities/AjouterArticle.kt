package com.amier.Activities.activities

import android.app.Activity
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
import com.amier.Activities.api.Api
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.api.ApiQuestion
import com.amier.Activities.mapbos
import com.amier.Activities.models.Articles
import com.amier.Activities.models.Question
import com.amier.Activities.models.SendBirdUser
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_ajouter_article.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.article_item.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AjouterArticle : AppCompatActivity() {
    lateinit var mSharedPref: SharedPreferences
    private var selectedImageUri: Uri? = null
    var imagePicker: ImageView?=null
    var questionnn :String?=null
    var ajoutQuestion :Button?=null

    var descriptionLayout :TextInputLayout?=null
    var photoLayout :TextInputLayout?=null
    var GpsArticle :Button?=null
    var whatDidYou : TextView?=null
    var ou : TextView?=null
    var changed  = false
    var valider :Button?=null
    lateinit var type:String
    lateinit var txtLogin: TextInputEditText
     var titreLayout: TextInputLayout? =null

    private lateinit var fab: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_ajouter_article)

        txtLogin = findViewById(R.id.titreText)
        GpsArticle = findViewById(R.id.GpsArticle)
        titreLayout = findViewById(R.id.titreLayout)
        descriptionLayout = findViewById(R.id.descriptionLayout)
        fab = findViewById(R.id.cameraArticle)
        imagePicker = findViewById(R.id.imageArticle)

        whatDidYou = findViewById(R.id.whatDidYou)
        photoLayout = findViewById(R.id.photoLayout)

        ou = findViewById(R.id.ou)


        mSharedPref.edit().remove("lat").apply()
        mSharedPref.edit().remove("long").apply()

        ajoutQuestion = findViewById(R.id.ajoutQuestion)
        valider = findViewById(R.id.AjouterArticleButton)
        if(intent.getStringExtra("nom") != null){
            AjouterArticleButton.text = "Modifier"
            titreText.setText(intent.getStringExtra("nom"))
        }
        if(intent.getStringExtra("description") != null){
            descriptionText.setText(intent.getStringExtra("description"))
        }
        if(intent.getStringExtra("photo") != null){
            selectedImageUri = Uri.parse(intent.getStringExtra("photo")!!)
            Glide.with(this).load(intent.getStringExtra("photo")).into(imageArticle)
        }
        type = intent.getStringExtra("type")!!
        if(type == "Lost"){
            whatDidYou!!.text = "Qu'avez vous perdu ?"
            ou!!.text = "Ou l'avez vous perdu ??"
            ajoutQuestion!!.visibility = View.INVISIBLE
        }else{
            whatDidYou!!.text = "Qu'avez vous trouvé ?"
            ou!!.text = "Ou l'avez vous trouvé ??"
        }

       GpsArticle!!.setOnClickListener{
           val intent = Intent(applicationContext, mapbos::class.java)
           intent.putExtra("type",type)
           startActivity(intent)

       }
        fab.setOnClickListener(View.OnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        })
        ajoutQuestion!!.setOnClickListener {
            showdialog()
        }


        valider!!.setOnClickListener {
            val titre = txtLogin.text.toString().trim()
            val description = descriptionText.text.toString().trim()


            var questionEnvoye = ""
            if (questionnn != null) {
                questionEnvoye = questionnn.toString()
            }
            if (intent.getStringExtra("nom") != null) {
                val resultat = modifierArticle(
                    titre,
                    questionEnvoye,
                    description,
                    type,
                    mSharedPref.getString("_id", "")!!
                )
                Log.d("le resultat est : ", resultat)
            } else {
                if (validate()) {
                    val resultat = AjoutArticle(
                        titre,
                        questionEnvoye,
                        description,
                        type,
                        mSharedPref.getString("_id", "")!!
                    )
                    Log.d("le resultat est : ", resultat)
                    this.finish()
                    if (resultat == "good") {
                        this.finish()
                        Toast.makeText(
                            applicationContext,
                            "Email ou Mot de passe incorrect",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }
    }

    private fun validate(): Boolean {
        titreLayout!!.error = null
        descriptionLayout!!.error = null
        photoLayout!!.error = null

        if (txtLogin.text!!.isEmpty()){
            titreLayout!!.error = "Champs obligatoire !"
            return false
        }

        if (descriptionText.text!!.isEmpty()){
            descriptionLayout!!.error = "Champs obligatoire"
            return false
        }
        if(selectedImageUri == null){
            photoLayout!!.error = "Photo obligatoire"
            return false
        }

        return true
    }



    private fun AjoutArticle(nom: String, question: String, description: String, type: String, user: String):String{
        if(selectedImageUri == null){

            return "imageNull"
        }


        val stream = contentResolver.openInputStream(selectedImageUri!!)
        val request =
            stream?.let { RequestBody.create("image/*".toMediaTypeOrNull(), it.readBytes()) } // read all bytes using kotlin extension
        val profilePicture = request?.let {
            MultipartBody.Part.createFormData(
                "photoProfil",
                "image.jpeg",
                it
            )
        }

        var a = ""
        val apiInterface = ApiArticle.create()
        val data: LinkedHashMap<String, RequestBody> = LinkedHashMap()

        data["nom"] = RequestBody.create(MultipartBody.FORM, nom)
        if(question.isNotEmpty()){
            data["question"] = RequestBody.create(MultipartBody.FORM, question)
        }
        if(mSharedPref.getString("lat","")!!.isNotEmpty()){
            data["lat"] = RequestBody.create(MultipartBody.FORM,mSharedPref.getString("lat","")!!)
            data["long"] = RequestBody.create(MultipartBody.FORM,mSharedPref.getString("long","")!!)
        }
        data["description"] = RequestBody.create(MultipartBody.FORM, description)
        data["type"] = RequestBody.create(MultipartBody.FORM, type)
        data["user"] = RequestBody.create(MultipartBody.FORM, user)

        if (profilePicture != null) {
            apiInterface.ajoutArticle(data,profilePicture).enqueue(object:
                Callback<Articles> {
                override fun onResponse(
                    call: Call<Articles>,
                    response: Response<Articles>
                ) {
                    if(response.isSuccessful){
                        Log.i("onResponse goooood", response.body().toString())
                        //on va ajouter la question ici
                        if(!questionnn.isNullOrEmpty()){

                            val apiQ = ApiQuestion.create()
                            val questionObject = Question()
                            questionObject.article = response.body()?._id.toString()
                            questionObject.titre = questionnn

                            apiQ.postQuestion(questionObject).enqueue(object:
                                Callback<Question>{
                                override fun onResponse(
                                    call: Call<Question>,
                                    response: Response<Question>
                                ) {
                                    a="good"
                                    Log.i("server reponse question good: ",response.body().toString())
                                }
                                override fun onFailure(call: Call<Question>, t: Throwable) {
                                    a="echec question"

                                }
                            })
                        }
                        //showAlertDialog()
                    } else {

                        Log.i("OnResponse not good", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<Articles>, t: Throwable) {
                    progress_bar.progress = 0
                    a="echec article"
                }

            })
        }
        return a
    }
    private fun modifierArticle(nom: String, question: String, description: String, type: String, user: String):String{

        var photo : MultipartBody.Part? = null
        if (changed) {
            val stream = contentResolver.openInputStream(selectedImageUri!!)
            val request =
                stream?.let { RequestBody.create("image/*".toMediaTypeOrNull(), it.readBytes()) } // read all bytes using kotlin extension
            photo = request?.let {
                MultipartBody.Part.createFormData(
                    "photoProfil",
                    "image.jpeg",
                    it

                )

            }
        }



        var a = ""
        val apiInterface = ApiArticle.create()
        val data: LinkedHashMap<String, RequestBody> = LinkedHashMap()

        data["nom"] = RequestBody.create(MultipartBody.FORM, nom)
        if(question.isNotEmpty()){
            data["question"] = RequestBody.create(MultipartBody.FORM, question)
        }

        if(mSharedPref.getString("lat","") !=null){

            println("heee hooo"+mSharedPref.getString("lat","")!!)
            var addresse: MutableList<Double>? = ArrayList()
            addresse!!.add(mSharedPref.getString("lat","")!!.toDouble())
            addresse.add(mSharedPref.getString("long","")!!.toDouble())
            data["addresse"] = RequestBody.create(MultipartBody.FORM,addresse.toString())
        }
        data["description"] = RequestBody.create(MultipartBody.FORM, description)
        data["type"] = RequestBody.create(MultipartBody.FORM, type)
        data["user"] = RequestBody.create(MultipartBody.FORM, user)


            apiInterface.modifierArticle(intent.getStringExtra("idArticle")!!,data,photo).enqueue(object:
                Callback<Articles> {
                override fun onResponse(
                    call: Call<Articles>,
                    response: Response<Articles>
                ) {
                    if(response.isSuccessful){
                        Log.i("onResponse goooood", response.body().toString())
                        //on va ajouter la question ici
                        if(!questionnn.isNullOrEmpty()){

                            val apiQ = ApiQuestion.create()
                            val questionObject = Question()
                            questionObject.article = response.body()?._id.toString()
                            questionObject.titre = questionnn

                            apiQ.postQuestion(questionObject).enqueue(object:
                                Callback<Question>{
                                override fun onResponse(
                                    call: Call<Question>,
                                    response: Response<Question>
                                ) {
                                    finish()
                                    a="good"
                                    Log.i("server reponse question good: ",response.body().toString())
                                }
                                override fun onFailure(call: Call<Question>, t: Throwable) {
                                    a="echec question"

                                }
                            })
                        }
                        //showAlertDialog()
                    } else {

                        Log.i("OnResponse not good", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<Articles>, t: Throwable) {
                    progress_bar.progress = 0
                    a="echec article"
                }

            })

        return a
    }

    private fun showAlertDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Alert")
            .setMessage("L'article a été ajouté avec succes")
            .setPositiveButton("Ok") {dialog, which ->
                showSnackbar("welcome")
            }
            .show()
    }
    private fun showSnackbar(msg: String) {
        Snackbar.make(layout_root, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun showdialog(){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Ajouter une question a votre article ! ")

        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Votre question...")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            questionnn = input.text.toString()

        })
        builder.setNegativeButton("Annuler", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE){
            selectedImageUri = data?.data
            imagePicker?.setImageURI(selectedImageUri)
            changed = true

        }
    }
}