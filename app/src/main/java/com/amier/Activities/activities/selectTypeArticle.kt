package com.amier.Activities.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.amier.modernloginregister.R

class selectTypeArticle : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_type_article)
        val perdu = findViewById<Button>(R.id.perdu)
        val trouve = findViewById<Button>(R.id.trouve)
        val intent = Intent(applicationContext, AjouterArticle::class.java)
        perdu.setOnClickListener{
            intent.putExtra("type","Lost")
            startActivity(intent)
            this.finish()
        }
        trouve.setOnClickListener{
            intent.putExtra("type","Found")
            startActivity(intent)
            this.finish()
        }

    }

}