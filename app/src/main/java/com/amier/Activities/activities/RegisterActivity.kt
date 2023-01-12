    package com.amier.Activities.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.amier.Activities.api.Api
import com.amier.Activities.models.SendBirdUser
import com.amier.Activities.models.User
import com.amier.modernloginregister.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

    class RegisterActivity : AppCompatActivity(){
    private var selectedImageUri: Uri? = null
 var imagePicker: ImageView?=null
private lateinit var fab: FloatingActionButton
        lateinit var mSharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        SendBird.init("C2B86342-5275-4183-9F0C-28EF1E4B3014", this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        imagePicker = findViewById(R.id.profileimage)
         fab = findViewById(R.id.floatingActionButton)
       fab.setOnClickListener(View.OnClickListener {
           ImagePicker.with(this)
               .crop()	    			//Crop image(Optional), Check Customization for more option
               .compress(1024)			//Final image size will be less than 1 MB(Optional)
               .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
               .start()
       })
        buttonSignUp.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val name1 = editTextName1.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val num = editTextNum.text.toString().trim()



            if(email.isEmpty()){
                editTextEmail.error = "Email required"
                editTextEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                editTextPassword.error = "Password required"
                editTextPassword.requestFocus()
                return@setOnClickListener
            }
            if(name.isEmpty()){
                editTextName.error = "Name required"
                editTextName.requestFocus()
                return@setOnClickListener
            }
            if(name1.isEmpty()){
                editTextName.error = "Last name required"
                editTextName.requestFocus()
                return@setOnClickListener
            }
            if(num.isEmpty()){
                editTextName.error = "Num required"
                editTextName.requestFocus()
                return@setOnClickListener
            }
            if (selectedImageUri == null) {

                return@setOnClickListener
            }

            if(checkEmail(editTextEmail.text.toString())){
                try {
                    createAccount(
                        name,name1,
                        email,
                        password,
                        num
                    )
                }catch (e:Exception){
                    Log.d("erreur creating user : ",e.toString())
                }


            }else{
                editTextEmail.error = "Invalid Email"
                editTextEmail.requestFocus()
                return@setOnClickListener
            }


        }
    }
     fun createAccount(firstName: String, lastName: String, email: String, password: String, number: String){
        if(selectedImageUri == null){
            println("image null")

            return
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

        val apiInterface = Api.create()
        val data: LinkedHashMap<String, RequestBody> = LinkedHashMap()

        data["nom"] = RequestBody.create(MultipartBody.FORM, firstName)
        data["prenom"] = RequestBody.create(MultipartBody.FORM, lastName)
        data["email"] = RequestBody.create(MultipartBody.FORM, email)
        data["password"] = RequestBody.create(MultipartBody.FORM, password)
        data["numt"] = RequestBody.create(MultipartBody.FORM, number)
        data["tokenfb"] = RequestBody.create(MultipartBody.FORM, mSharedPref.getString("tokenfb", "")!!)


        if (profilePicture != null) {
            apiInterface.userSignUp(data,profilePicture).enqueue(object: Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if(response.isSuccessful){
                        mSharedPref.edit().apply{
                            putString("photoProfil", response.body()?.user?.photoProfil.toString())
                            putString("_id", response.body()?.user?._id.toString())
                            putString("nom", response.body()?.user?.nom.toString())
                            putString("email", response.body()?.user?.email.toString())
                            putString("numt", response.body()?.user?.numt.toString())
                            putString("tokenfb", response.body()?.user?.tokenfb.toString())
                            putString("prenom", response.body()?.user?.prenom.toString())
                            //putBoolean("session", true)
                        }.apply()
                        Log.i("signup good", response.body().toString())
                        var userSendbird = SendBirdUser()
                        userSendbird.nickname = response.body()?.user?.prenom.toString()

                        userSendbird.profile_url = response.body()?.user?.photoProfil.toString()
                        userSendbird.user_id = response.body()?.user?._id.toString()
                        Log.i("photo de profil sendbird : ", userSendbird.profile_url!!)
                        apiInterface.sendBirdCreate(userSendbird).enqueue(object:
                            Callback<SendBirdUser>{
                            override fun onResponse(
                                call: Call<SendBirdUser>,
                                response: Response<SendBirdUser>
                            ) {

                                val intent = Intent(applicationContext, HomeActivity::class.java)
                                if(connectToSendBird(mSharedPref.getString("_id","")!!)){
                                    Log.i("sendbird connecté :","")
                                }else{
                                    Log.i("sendbird moch connecté :","")
                                }
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                Log.i("server reponse sendbird good: ",response.body().toString())
                            }
                            override fun onFailure(call: Call<SendBirdUser>, t: Throwable) {
                                Log.i("server reponse sendbird error: ",t.toString())
                            }
                        })
//                        showAlertDialog()
                    } else {
                        Log.i("signup error", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    progress_bar.progress = 0
                    println(t.toString())
                }

            })

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE){
            selectedImageUri = data?.data
             imagePicker?.setImageURI(selectedImageUri)

        }
    }
    private fun showAlertDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle("Alert")
            .setMessage("Merci pour avoir choisis Lost&Found ! \n Vous avez reçu un email contenant un lien de verification!")
            .setPositiveButton("Ok") {dialog, which ->
                showSnackbar("Bienvenue")
            }
            .show()
    }
    private fun showSnackbar(msg: String) {
        Snackbar.make(layout_root, msg, Snackbar.LENGTH_SHORT).show()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

        fun createAccountToSendBird(userID: String, nickname: String, photo: String) :Boolean{
            val jsonObject = JSONObject()
            jsonObject.put("user_id", userID)
            jsonObject.put("nickname", nickname)
            jsonObject.put("profile_url", photo)
            var a = false

            return  a
        }

        private fun checkEmail(email: String): Boolean {
            return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
        }
        val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        fun connectToSendBird(userID: String) :Boolean{
            var a = false
            SendBird.connect(userID) { user, e ->

                if (e != null) {

                    Log.i("erreur connecting sendbird : ",e.message)
                }else{

                    a = true
                }
            }
            return  a
        }

}
