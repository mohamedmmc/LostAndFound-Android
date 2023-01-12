package com.amier.Activities.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.amier.Activities.activities.adapters.ArticleViewAdapter
import com.amier.Activities.activities.adapters.AssociationViewAdapter
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.api.ApiReport
import com.amier.Activities.models.Articles
import com.amier.Activities.models.Association
import com.amier.Activities.models.Report
import com.amier.Activities.models.User
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_association_donation.*
import kotlinx.android.synthetic.main.activity_voir_article.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssociationDonation : AppCompatActivity(), ArticleViewAdapter.OnItemClickListener {
    lateinit var mSharedPref: SharedPreferences
    lateinit var animationView: LottieAnimationView
    lateinit var tesxt: TextView
    lateinit var articles: MutableList<Articles>
    val articlesFound : MutableList<Articles> = arrayListOf()
    lateinit var adap: ArticleViewAdapter
    lateinit var idAss: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_association_donation)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        associationNom.text = intent.getStringExtra("nomAss")
        associationNumTel.text = intent.getStringExtra("numTel")
        idAss = intent.getStringExtra("idAss")!!
        ArticleAssociation.setOnClickListener {
            val intent = Intent(this, Contribution::class.java)

            intent.putExtra("idAss",idAss)
            startActivity(intent)
        }
        associationCategorie.text = intent.getStringExtra("categorie")
        Glide.with(this).load(Uri.parse(intent.getStringExtra("photo"))).into(AssociationImage)
        animationView = findViewById(R.id.animationNoreponse)
        tesxt = findViewById(R.id.messageReponse)
        animationView.playAnimation()
        animationView.loop(true)
        rvAss.layoutManager = LinearLayoutManager(this)
        rvAss.setHasFixedSize(true)
        getAllData(mSharedPref.getString("_id","")!!){ articless : MutableList<Articles> ->
            if(articless.isEmpty()){
                articles = articless
                adap = ArticleViewAdapter(articles,this,this)
                rvAss.visibility = View.GONE
                animationView.visibility = View.VISIBLE
                tesxt.visibility = View.VISIBLE
                animationView.playAnimation()
                animationView.loop(true)
            }else{

                rvAss.visibility = View.VISIBLE
                animationView.visibility = View.GONE
                tesxt.visibility = View.GONE
                rvAss.adapter = ArticleViewAdapter(articless,this,this)
            }



        }
    }
    private fun getAllData(idUser:String,callback: (MutableList<Articles>) -> Unit){
        val apiInterface = ApiArticle.create()

        apiInterface.GetMyArticles(idUser).enqueue(object:
            Callback<Articles> {
            override fun onResponse(
                call: Call<Articles>,
                response: Response<Articles>
            ) {

                if(response.isSuccessful){
                    Log.i("onResponse goooood", response.body().toString())
                    if(response.body()?.articles!!.isNotEmpty()){
                        response.body()?.articles!!.forEach {
                            if (it.type =="Found"){
                                articlesFound.add(it)
                            }
                        }
                    }
                    println("le tableau contient " +articlesFound.count() +" cases")
                    return callback(articlesFound)

                } else {
                    Log.i("OnResponse not good", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Articles>, t: Throwable) {

            }

        })
    }


    override fun onItemClick(position: Int, property: List<Articles>) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Vous êtes sur le point de contacter "+intent.getStringExtra("nomAss")+" pour donner "+property[position].nom)
            .setCancelable(false)
            .setPositiveButton("Oui") { dialog, id ->
                val apiInterface = ApiArticle.create()
                var association = Association()
                association.article = property[position]._id
                apiInterface.posterContribution(idAss,association).enqueue(object :
                    Callback<Association> {
                    override fun onResponse(
                        call: Call<Association>,
                        response: Response<Association>
                    ) {
                        if (response.isSuccessful) {

                        }
                    }

                    override fun onFailure(call: Call<Association>, t: Throwable) {
                    }
                })


            }
            .setNegativeButton("Non") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

//    override fun onResume() {
//        super.onResume()
//        rvAss.layoutManager = LinearLayoutManager(this)
//        rvAss.setHasFixedSize(true)
//        getAllData(mSharedPref.getString("_id","")!!){ articless : MutableList<Articles> ->
//            if(articless.isEmpty()){
//                println(articless)
//                articles = articless
//                adap = ArticleViewAdapter(articles,this,this)
//                rvAss.visibility = View.GONE
//                animationView.visibility = View.VISIBLE
//                tesxt.visibility = View.VISIBLE
//                animationView.playAnimation()
//                animationView.loop(true)
//            }
//
//            else{
//                rvAss.visibility = View.VISIBLE
//                animationView.visibility = View.GONE
//                tesxt.visibility = View.GONE
//                rvAss.adapter = ArticleViewAdapter(articless,this,this)
//            }
//        }
//    }
}