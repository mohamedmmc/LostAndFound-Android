package com.amier.Activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.amier.modernloginregister.R

class SplashScreen : AppCompatActivity() {
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        //hide action bar on current view
        supportActionBar?.hide()
        //splash screen 3s
        handler= Handler()
        handler.postDelayed({
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}