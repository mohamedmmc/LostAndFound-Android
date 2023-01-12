package com.amier.Activities.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.amier.Activities.JavaHelper
import com.amier.Activities.api.Api
import com.amier.Activities.models.SendBirdUser
import com.amier.Activities.models.User
import com.amier.Activities.storage.SharedPrefManager
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_login.editTextEmail
import kotlinx.android.synthetic.main.activity_login.editTextPassword
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.item_email.*
import kotlinx.android.synthetic.main.item_image.*
import kotlinx.android.synthetic.main.item_image.nameUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import android.widget.TextView
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

import com.sendbird.android.constant.StringSet.core
import java.util.*
import kotlin.collections.LinkedHashMap


@Suppress("DEPRECATION")
const val RC_SIGN_IN = 123

class LoginActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var googleSign: SignInButton
    lateinit var remember_me: CheckBox
    lateinit var mSharedPref: SharedPreferences
    lateinit var gotoRegister: Button
    lateinit var forgotPassword: Button
    lateinit var mainBinding: LastFragment
    private val EMAIL = "email"
    lateinit var Pref: SharedPreferences
    lateinit var callbackManager : CallbackManager
    var loadingDialog = LoadingDialog()
    lateinit var FacebookButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        SendBird.init("C2B86342-5275-4183-9F0C-28EF1E4B3014", this)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        FacebookButton = findViewById(R.id.btnFacebook)
        FacebookButton.setReadPermissions(
            Arrays.asList(
            "public_profile", "email"));


        //////////////////////////////////
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        remember_me = findViewById(R.id.remember);
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        googleSign = findViewById(R.id.sign_in_button)
        password = findViewById(R.id.editTextPassword)
        gotoRegister = findViewById(R.id.gotoRegister)
        forgotPassword = findViewById(R.id.forgotPassword)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        setGooglePlusButtonText(googleSign,"Connexion Google")
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSign.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

        gotoRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }


        ///fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        callbackManager = CallbackManager.Factory.create()
        FacebookButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {

            override fun onCancel() {
                Toast.makeText(applicationContext, "batlanaaaa", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(applicationContext, "5leeeet", Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {


                val graphRequest = GraphRequest.newMeRequest(result?.accessToken){ `object`, response ->

                    println(response);
                    println("666666666666666666666666666");
                    // println(`object`);
                    getFacebookData1(`object`)



                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)

                }

                val param = Bundle()
                param.putString("fields","name,email,id")
                graphRequest.parameters = param
                graphRequest.executeAsync()

                Toast.makeText(applicationContext, "mrigel ya star", Toast.LENGTH_LONG).show()

            }


        })


        buttonLogin.setOnClickListener {


            var userr = User()
            userr.email = editTextEmail.text.toString()
            userr.password = editTextPassword.text.toString()
            userr.tokenfb = mSharedPref.getString("tokenfb", "")!!
            val apiuser = Api.create().userLogin(userr)
            loadingDialog.LoadingDialog(this)
            apiuser.enqueue(object: Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.isSuccessful){

                        MotionToast.darkColorToast(
                            this@LoginActivity,
                            "Good ",
                            "Success Login",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_TOP,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this@LoginActivity,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )
                        Log.i("login User:", response.body().toString())
                        //Store company data in sharedpref
                        //mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                        mSharedPref.edit().apply{
                            if(remember_me.isChecked){
                                putBoolean("remember", true)
                                putString("email", userr.email)
                                putString("password", userr.password)
                            }
                            putString("photoProfil", response.body()?.user?.photoProfil.toString())
                            putString("_id", response.body()?.user?._id.toString())
                            putString("nom", response.body()?.user?.nom.toString())
                            putString("email", userr.email)
                            putString("password", userr.password)
                            putString("numt", response.body()?.user?.numt.toString())
                            putString("prenom", response.body()?.user?.prenom.toString())
                            putString("tokenfb", response.body()?.user?.tokenfb.toString())
                            println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                            println(response.body()?.user?.password.toString())
                            //putBoolean("session", true)
                        }.apply()
                        if(connectToSendBird(mSharedPref.getString("_id","")!!)){
                            Log.i("sendbird connecté :","")
                        }else{
                            Log.i("sendbird moch connecté :","")
                        }
                        loadingDialog.dismissDialog()
                        finish()
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        loadingDialog.dismissDialog()
                        Toast.makeText(applicationContext, "uncorrect email or password", Toast.LENGTH_LONG).show()
                        Log.i("RETROFIT_API_RESPONSE", response.toString())
                        Log.i("login response", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    loadingDialog.dismissDialog()
                    Toast.makeText(applicationContext, "erreur server", Toast.LENGTH_LONG).show()
                }

            })
            loadingDialog.startLoadingDialog()
            println("/////////////////////")
            println(mSharedPref.getString("photoProfil", "user.email").toString())

            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
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

        }


    }
/////facebooklogin
//    private fun getFacebookData(obj: JSONObject?) {
//        val profilePic = "https://graph.facebook.com/${obj?.getString("id")}/picture?width=200&height200"
//        Glide.with(this).load(profilePic).into(mainBinding.imageUser)
//        val name = obj?.getString("name")
//        val email = obj?.getString("email")
//        mainBinding.nameUser.text = "NAME:${name}"
//        mainBinding.emailUser.text = "EMAIL:${email}"
//
//    }

    fun connectToSendBird(userID: String) :Boolean{
        var a = false
        SendBird.connect(userID) { user, e ->

            if (e != null) {

                Log.i("erreur connecting sendbird : ",e.toString())
            }else{

                a = true
            }
        }
        return  a
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //callbackManager.onActivityResult(requestCode,resultCode,data)
        //GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            var userr = User()
            userr.email = account.email.toString()
            userr.nom = account.familyName.toString()
            userr.prenom = account.givenName.toString()
            userr.tokenfb = mSharedPref.getString("tokenfb", "")!!
            Log.i("user google est : ",userr.toString()+account.photoUrl)
           // loadingDialog.LoadingDialog(this)
//            loadingDialog.startLoadingDialog()
            val apiuser = Api.create().userLoginSocial(userr)
            apiuser.enqueue(object: Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.isSuccessful){
                        Log.i("login User:", response.body().toString())
                        //Store company data in sharedpref
                        //mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                        mSharedPref.edit().apply{
                            putString("photoProfil", response.body()?.user?.photoProfil.toString())
                            putString("_id", response.body()?.user?._id.toString())
                            putString("nom", response.body()?.user?.nom.toString())
                            putString("email", response.body()?.user?.email.toString())
                            putString("numt", response.body()?.user?.numt.toString())
                            putString("prenom", response.body()?.user?.prenom.toString())
                            putString("tokenfb", response.body()?.user?.tokenfb.toString())
                            putBoolean("remember",true)
                            //putBoolean("session", true)
                        }.apply()
                        Log.i("shared pref id user : ",mSharedPref.getString("nom","")!!)
                        if(connectToSendBird(mSharedPref.getString("_id","")!!)){
                            Log.i("sendbird connecté :","")
                        }else{
                            Log.i("sendbird moch connecté :","")
                        }
//                        loadingDialog.dismissDialog()
                        finish()
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else if (response.code() == 404) {


                        createAccount(account.givenName.toString(),account.familyName.toString(),account.email.toString(),null,null,account.photoUrl.toString())

                    }else{
//                        loadingDialog.dismissDialog()
                        Toast.makeText(applicationContext, "Email ou Mot de passe incorrect", Toast.LENGTH_LONG).show()
                        Log.i("RETROFIT_API_RESPONSE", response.toString())
                        Log.i("login response", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    //loadingDialog.dismissDialog()
                    Toast.makeText(applicationContext, "Erreur server", Toast.LENGTH_LONG).show()
                }

            })
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Log.i("user google est : ",e.toString())
        }
    }

    fun createAccount(firstName: String, lastName: String, email: String, password: String?, number: String?,photo:String?){




        val apiInterface = Api.create()
        val data: LinkedHashMap<String, RequestBody> = LinkedHashMap()

        data["nom"] = RequestBody.create(MultipartBody.FORM, firstName)
        data["prenom"] = RequestBody.create(MultipartBody.FORM, lastName)
        data["email"] = RequestBody.create(MultipartBody.FORM, email)
        data["tokenfb"] = RequestBody.create(MultipartBody.FORM, mSharedPref.getString("tokenfb", "")!!)
        if(photo != null){
            data["photo"] = RequestBody.create(MultipartBody.FORM, photo)
        }
        if(password != null){
            data["password"] = RequestBody.create(MultipartBody.FORM, password)
            data["numt"] = RequestBody.create(MultipartBody.FORM, number!!)
        }



        apiInterface.userSignUp(data,null).enqueue(object: Callback<User> {
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
                    //userSendbird.access_token = response.body()?.user?.tokenfb.toString()
                    userSendbird.nickname = response.body()?.user?.prenom.toString()
                    userSendbird.session_tokens?.add(response.body()?.user?.tokenfb.toString())
                    userSendbird.profile_url = response.body()?.user?.photoProfil.toString()
                    userSendbird.user_id = response.body()?.user?._id.toString()
                    Log.i("photo de profil sendbird : ", userSendbird.profile_url!!)
                    apiInterface.sendBirdCreate(userSendbird).enqueue(object:
                        Callback<SendBirdUser>{
                        override fun onResponse(
                            call: Call<SendBirdUser>,
                            response: Response<SendBirdUser>
                        ) {
                            mSharedPref.edit().putBoolean("remember",true)
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
    ////////////////////////
    override fun onStart() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            else {

                val token = task.result

                mSharedPref.edit().apply {
                    putString("tokenfb", token)

                }.apply()
                Log.i("tokenfb",token)
            }

        })
        if(mSharedPref.getBoolean("remember",false)){
            val user = User()
            user.tokenfb = mSharedPref.getString("tokenfb","")!!
            val apiuser = Api.create().updateToken(mSharedPref.getString("_id","")!!,user)
            apiuser.enqueue(object: Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.isSuccessful) {

                        Log.i("shared pref id user : ", mSharedPref.getString("nom", "")!!)
                        if (connectToSendBird(mSharedPref.getString("_id", "")!!)) {
                            Log.i("sendbird connecté :", "")
                        } else {
                            Log.i("sendbird moch connecté :", "")
                        }
//                        loadingDialog.dismissDialog()
                        finish()
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    //loadingDialog.dismissDialog()
                    Toast.makeText(applicationContext, "Erreur server", Toast.LENGTH_LONG).show()
                }

            })
            finish()
            if(connectToSendBird(mSharedPref.getString("_id","")!!)){
                Log.i("sendbird connecté :","")
            }else{
                Log.i("sendbird moch connecté :","")
            }
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
        super.onStart()
//        if(SharedPrefManager.getInstance(this).isLoggedIn){
//            val intent = Intent(applicationContext, HomeActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }
    }

    private fun getFacebookData1(obj: JSONObject?) {
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        //val profilePic = "https://graph.facebook.com/${obj?.getString("id")}/picture?width=200&height200"
        //Glide.with(this).load(profilePic).into(mainBinding.imageUser)
        val name = obj?.getString("name")
        val emaill = obj?.getString("email")
        val id = obj?.getString("id")
        val imagefacebook = "https://graph.facebook.com/" + id + "/picture?type=large"

        println("000000000000000000000000000000000");
        println(name);

        println(emaill);
        println(id);
        println(imagefacebook);
        mSharedPref.edit().apply{
            putString("nom", name);
            putString("email", emaill);
            putString("id", id);
            putString("photoProfil", imagefacebook);

        }.apply()

        println(obj?.getString("email"));
//        val profilePic = obj!!.getJSONObject("picture").getJSONObject("data").getString("url");
        // println(profilePic.toString())

    }

    private fun navigate(){
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
    }
    protected fun setGooglePlusButtonText(signInButton: SignInButton, buttonText: String?) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (i in 0 until signInButton.childCount) {
            val v: View = signInButton.getChildAt(i)
            if (v is TextView) {
                val tv = v as TextView
                tv.text = buttonText
                return
            }
        }
    }


    fun saveFacebookUserInfo(
        first_name: String,
        last_name: String,
        email: String,
        gender: String,
        profileURL: String
    ) {
        Pref = getSharedPreferences("facebook", Context.MODE_PRIVATE)
        val editor = Pref.edit()
        editor.putString("fb_first_name", first_name)
        editor.putString("fb_last_name", last_name)
        editor.putString("fb_email", email)
        editor.putString("fb_gender", gender)
        editor.putString("fb_profileURL", profileURL)
        editor.apply() // This line is IMPORTANT !!!
        Log.d(
            "MyApp",
            "Shared Name : $first_name\nLast Name : $last_name\nEmail : $email\nGender : $gender\nProfile Pic : $profileURL"
        )
    }

}
