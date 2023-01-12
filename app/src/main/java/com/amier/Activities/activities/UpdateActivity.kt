package com.amier.Activities.activities

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.amier.Activities.api.Api
import com.amier.Activities.models.User

import com.amier.Activities.storage.SharedPrefManager
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.view.*
import kotlinx.android.synthetic.main.activity_update.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class UpdateActivity : AppCompatActivity() {
    lateinit var id: String
    lateinit var Name: EditText
    lateinit var Name1: EditText
    lateinit var Email: EditText
    lateinit var Num: EditText
    lateinit var layout: Layout

    lateinit var profileimage1: ImageView
    lateinit var mSharedPref: SharedPreferences
    private var selectedImageUri: Uri? = null
    var imagePicker: ImageView?=null
    private lateinit var fb: FloatingActionButton
    private lateinit var buttonUpdate: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        profileimage1 = findViewById<ImageView?>(R.id.profileimage1)
        val picStr: String = mSharedPref.getString("photoProfil", "").toString()
        Glide.with(this).load(Uri.parse(picStr)).into(profileimage1)

        val emailStr: String = mSharedPref.getString("nom", "user.email").toString()
        Name = findViewById<EditText?>(R.id.Name)as EditText
        Name.setText(emailStr)

        val prenomStr: String = mSharedPref.getString("prenom", "user.email").toString()
        Name1 = findViewById<EditText?>(R.id.Name1)as EditText
        Name1.setText(prenomStr)

        val nameStr: String = mSharedPref.getString("email", "user.email").toString()
        Email = findViewById<EditText?>(R.id.Email)as EditText
        Email.setText(nameStr)

        val phoneStr: String = mSharedPref.getString("numt", "user.email").toString()
        Num = findViewById<EditText?>(R.id.Num)as EditText
        Num.setText(phoneStr)


        buttonUpdate = findViewById(R.id.buttonUpdate)
        imagePicker = findViewById(R.id.profileimage1)
        fb = findViewById(R.id.fb)
        fb.setOnClickListener(View.OnClickListener {
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        })
        buttonUpdate.setOnClickListener {

            val name = Name.text.toString().trim()
            val name1 = Name1.text.toString().trim()
            val email = Email.text.toString().trim()
            val num = Num.text.toString().trim()


            if(email.isEmpty()){
                Email.error = "Email required"
                Email.requestFocus()
                return@setOnClickListener
            }

            if(name.isEmpty()){
                Name.error = "Name required"
                editTextName.requestFocus()
                return@setOnClickListener
            }
            if(name1.isEmpty()){
               Name1.error = "Last name required"
                Name1.requestFocus()
                return@setOnClickListener
            }
            if(num.isEmpty()){
                Num.error = "Num required"
                Num.requestFocus()
                return@setOnClickListener
            }
            if (selectedImageUri != null) {
                //Snackbar.make(layout_root, "Select an Image First", Snackbar.LENGTH_SHORT).show()
                //return@setOnClickListener
                val parcelFileDescriptor =
                    contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return@setOnClickListener
            }



            id = mSharedPref.getString("_id", "user.id").toString()


            update(
                name,name1,
                email,
                num,
                id
            )

        }
    }

    private fun update(firstName: String, lastName: String, email: String, number: String,id: String){

         var profilePicture:
                MultipartBody.Part?=null

        //id1 = mSharedPref.getString("id", "user.id").toString();
        if (selectedImageUri != null) {
            val stream = contentResolver.openInputStream(selectedImageUri!!)
            val request =
                stream?.let {
                    RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        it.readBytes()
                    )
                } // read all bytes using kotlin extension
             profilePicture = request?.let {
                MultipartBody.Part.createFormData(
                    "photoProfil",
                    "image.jpeg",
                    it
                )
            }
        }



        val apiInterface = Api.create()
        val data: LinkedHashMap<String, RequestBody> = LinkedHashMap()

        data["nom"] = RequestBody.create(MultipartBody.FORM, firstName)
        data["prenom"] = RequestBody.create(MultipartBody.FORM, lastName)
        data["email"] = RequestBody.create(MultipartBody.FORM, email)
        data["numt"] = RequestBody.create(MultipartBody.FORM, number)

            apiInterface.userUpdate(id,data,profilePicture).enqueue(object:
                Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    println(response.message())
                    if(response.isSuccessful){
                        Log.i("onResponse goooood", response.body().toString())

                        MotionToast.darkColorToast(
                            this@UpdateActivity,
                            "Good ",
                            "Success Update",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_TOP,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this@UpdateActivity,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )

                        mSharedPref.edit().apply{

                            putString("photoProfil", response.body()?.user?.photoProfil.toString())
                            putString("_id", response.body()?.user?._id.toString())
                            putString("nom", response.body()?.user?.nom.toString())
                            putString("email", response.body()?.user?.email)
                            putString("password", response.body()?.user?.password)
                            putString("numt", response.body()?.user?.numt.toString())
                            putString("prenom", response.body()?.user?.prenom.toString())

                            println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                            println(response.body()?.user?.password.toString())
                            //putBoolean("session", true)
                        }.apply()

                        finish()
                        val intent = Intent(applicationContext, HomeActivity::class.java)

                        startActivity(intent)

                    } else {
                        Log.i("OnResponse not good", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    progress_bar1.progress = 0
                    println("noooooooooooooooooo")
                }

            })

    }
    override fun onStart() {
        super.onStart()
        if(SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(applicationContext, LastFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE){
            selectedImageUri = data?.data
            imagePicker?.setImageURI(selectedImageUri)

        }
    }

}


private fun Unit.enqueue(callback: Callback<User>) {

}
