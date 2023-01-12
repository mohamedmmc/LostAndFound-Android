package com.amier.Activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.amier.Activities.api.Api
import com.amier.Activities.models.User

import com.amier.modernloginregister.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var verif_email: Button


    lateinit var forgotemail: EditText


    var loadingDialog = LoadingDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgotemail = findViewById(R.id.forgotemail)

        verif_email = findViewById(R.id.verif_email)


        verif_email.setOnClickListener {
            var emaill = User()
            var emailreset = forgotemail.text.toString()
            emaill.email = emailreset
            val apiuser = Api.create().forgotpassword(emaill)
            loadingDialog.LoadingDialog(this)
            apiuser.enqueue(object: Callback<User>{
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if(response.isSuccessful){
                        println(response.body()?.token)
                        MotionToast.darkColorToast(
                            this@ForgotPasswordActivity,
                            "Email correct",
                            "Prochaine étape",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_TOP,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this@ForgotPasswordActivity,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )

                        val intent = Intent(applicationContext, resetActivity::class.java)
                        intent.apply {
                            putExtra("email", emailreset)
                        }
                        startActivity(intent)


                    } else {

                        Toast.makeText(applicationContext, "Email incorrect", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(applicationContext, "erreur server", Toast.LENGTH_LONG).show()
                }

            })


        }


    }
}