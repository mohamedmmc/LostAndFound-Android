package com.amier.Activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.amier.Activities.api.Api
import com.amier.Activities.models.*
import com.amier.modernloginregister.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class resetActivity : AppCompatActivity() {
    lateinit var verif_token: Button
    lateinit var email: String
    lateinit var forgottoken: EditText
    //lateinit var forgotemail: EditText
    lateinit var newpass: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        verif_token = findViewById(R.id.verif_token)
        forgottoken = findViewById(R.id.forgottoken)
        newpass = findViewById(R.id.newpass)
        email = intent.getStringExtra("email")!!


        verif_token.setOnClickListener {

            if(newpass.text.toString().isNotEmpty()){
                val user = User()
                user.password = newpass.text.toString()
                val apiuser = Api.create().resetpassword(email,forgottoken.text.toString(),user)
                apiuser.enqueue(object: Callback<User> {
                    override fun onResponse(
                        call: Call<User>,
                        response: Response<User>
                    ) {
                        if(response.isSuccessful){


                            MotionToast.darkColorToast(
                                this@resetActivity,
                                "Good ",
                                "Success reset Password",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_TOP,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                    this@resetActivity,
                                    www.sanju.motiontoast.R.font.helvetica_regular
                                )
                            )
                            val intent = Intent(applicationContext, LoginActivity::class.java)

                            startActivity(intent)


                        } else {

                            Toast.makeText(applicationContext, "Code incorrect", Toast.LENGTH_LONG).show()

                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(applicationContext, "erreur server", Toast.LENGTH_LONG).show()
                    }

                })
            }
            else{
                Toast.makeText(applicationContext, "Mot de passe obligatoire", Toast.LENGTH_LONG).show()
            }




        }
    }
}