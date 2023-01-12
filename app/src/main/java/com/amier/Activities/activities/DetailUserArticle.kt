package com.amier.Activities.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.amier.Activities.activities.adapters.UserArticleListAdapter
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.models.Articles
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import com.sendbird.android.SendBird
import kotlinx.android.synthetic.main.activity_detail_user_article.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserArticle : AppCompatActivity(), UserArticleListAdapter.OnItemClickListener {
    lateinit var mSharedPref: SharedPreferences
    lateinit var userArticleid: String
    lateinit var articleUser: MutableList<Articles>
    lateinit var animationView: LottieAnimationView
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    var usersSB: MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user_article)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        animationView = findViewById(R.id.animationNoArticle)
         userArticleid = intent.getStringExtra("id")
        val userArticleNom = intent.getStringExtra("nom")
        val userArticlePrenom = intent.getStringExtra("prenom")
        val userArticlePhoto = intent.getStringExtra("photo")
        val userArticleEmail = intent.getStringExtra("email")


        val photo = findViewById<ImageView>(R.id.imageUser)
        val nom = findViewById<TextView>(R.id.userArticleNom)
        val prenom = findViewById<TextView>(R.id.prenomUser)
        nom.text = userArticleNom
        prenom.text = userArticlePrenom
        val repondreButton = findViewById<Button>(R.id.voirProfilB)
        Glide.with(applicationContext).load(Uri.parse(userArticlePhoto)).into(photo)


        repondreButton.setOnClickListener {
            println(userArticleid)
            usersSB.add(userArticleid)
            createChannel(usersSB)
        }
        recycler_viewArticleList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recycler_viewArticleList.setHasFixedSize(true)

        getAllData{ articless : MutableList<Articles> ->
            if(articless.isEmpty()){
                Articles.text = "Pas d'articles à afficher "
                recycler_viewArticleList.visibility = View.GONE
                animationView.visibility = View.VISIBLE
                animationView.playAnimation()
                animationView.loop(true)
            }
            recycler_viewArticleList.adapter = UserArticleListAdapter(articless,this)

        }



    }
    private fun createChannel(users: MutableList<String>) {
        val params = GroupChannelParams()

        val operatorId = ArrayList<String>()
        params.setDistinct(true)
        operatorId.add(SendBird.getCurrentUser().userId)

        params.addUserIds(users)
        params.setOperatorUserIds(operatorId)

        GroupChannel.createChannel(params) { groupChannel, e ->
            if (e != null) {
                Log.e("TAG", e.message)
            } else {
                val intent = Intent(this, ChannelActivity::class.java)
                intent.putExtra(EXTRA_CHANNEL_URL, groupChannel.url)
                startActivity(intent)
            }
        }
    }
    private fun getAllData(callback: (MutableList<Articles>) -> Unit){
        val apiInterface = ApiArticle.create()
        val id = userArticleid
        var articleRetour : MutableList<Articles>? =null
        apiInterface.GetArticlesByUser(id).enqueue(object:
            Callback<Articles> {
            override fun onResponse(
                call: Call<Articles>,
                response: Response<Articles>
            ) {
                if(response.isSuccessful){
                    Log.i("onResponse goooood", response.body().toString())
//                    response.body()!!.articles!!.forEach{
//                        if (it.question != null){
//                            articleRetour?.add(it)
//                        }
//                    }
                    return callback(response.body()!!.articles!!)
                } else {
                    Log.i("OnResponse not good", response.body().toString())
                }
            }
            override fun onFailure(call: Call<Articles>, t: Throwable) {
            }
        })
    }

    override fun onItemClick(position: Int, articles: List<Articles>) {
        val intent = Intent(this@DetailUserArticle, DetailArticle::class.java)
        intent.putExtra("nom",articles[position].nom)
        if(articles[position].addresse!!.isNotEmpty()){
            intent.putExtra("longi", articles[position].addresse?.get(0))
            intent.putExtra("lalti",articles[position].addresse?.get(1))
        }
        intent.putExtra("_id",articles[position]._id)
        intent.putExtra("description",articles[position].description)
        intent.putExtra("type",articles[position].type)
        intent.putExtra("photo",articles[position].photo)
        intent.putExtra("userArticleNom", articles[position].user?.nom)
        intent.putExtra("userArticlePrenom", articles[position].user?.prenom)
        intent.putExtra("userArticlePhoto", articles[position].user?.photoProfil)
        intent.putExtra("userArticleEmail", articles[position].user?.email)
        intent.putExtra("userDetail", articles[position].user?._id)
        intent.putExtra("question", articles[position].question?._id)
        intent.putExtra("questionTitle", articles[position].question?.titre)
        startActivity(intent)
        this.finish()
    }
}