package com.amier.Activities.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.amier.Activities.activities.adapters.ArticleViewAdapter
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.models.Articles
import com.amier.Activities.models.User
import com.amier.modernloginregister.R
import kotlinx.android.synthetic.main.activity_contribution.*
import kotlinx.android.synthetic.main.activity_voir_article.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class Contribution : AppCompatActivity(), ArticleViewAdapter.OnItemClickListener {
    lateinit var mSharedPref: SharedPreferences
    lateinit var idUser: String
    lateinit var idAss: String
    lateinit var animationView: LottieAnimationView
    lateinit var tesxt: TextView
    lateinit var articles: MutableList<Articles>
    lateinit var adap: ArticleViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribution)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        idUser = mSharedPref.getString("_id","")!!
        idAss = intent.getStringExtra("idAss")!!


        animationView = findViewById(R.id.animationNoreponseE)
        tesxt = findViewById(R.id.messageReponseE)
        myArticleRVV.layoutManager = LinearLayoutManager(this)
        myArticleRVV.setHasFixedSize(true)


        getAllData(idUser, idAss){ articless : MutableList<Articles> ->
            if(articless.isEmpty()){
                println(articless)
                articles = articless
                adap = ArticleViewAdapter(articles,this,this)
                myArticleRVV.visibility = View.GONE
                animationView.visibility = View.VISIBLE
                tesxt.visibility = View.VISIBLE
                animationView.playAnimation()
                animationView.loop(true)
            }

            myArticleRVV.adapter = ArticleViewAdapter(articless,this,this)

        }
    }
    private fun getAllData(idUser:String,idAss:String,callback: (MutableList<Articles>) -> Unit){
        val apiInterface = ApiArticle.create()

        var user = User()
        user._id = idUser

        apiInterface.voirContribution(idAss,user).enqueue(object:
            Callback<Articles> {
            override fun onResponse(
                call: Call<Articles>,
                response: Response<Articles>
            ) {
                println("reponse : "+response.code())
                if(response.isSuccessful){
                    Log.i("onResponse goooood", response.body().toString())
                    return callback(response.body()!!.articles!!)

                } else {
                    Log.i("OnResponse not good", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Articles>, t: Throwable) {
                println("reponse : "+t)
            }

        })
    }

    override fun onItemClick(position: Int, property: List<Articles>) {
    }
}