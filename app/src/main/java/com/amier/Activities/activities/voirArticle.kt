package com.amier.Activities.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.amier.Activities.SwipeGesture
import com.amier.Activities.activities.adapters.ArticleViewAdapter
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.models.Articles
import com.amier.modernloginregister.R
import kotlinx.android.synthetic.main.activity_detail_user_article.*
import kotlinx.android.synthetic.main.activity_voir_article.*
import kotlinx.android.synthetic.main.article_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class voirArticle : AppCompatActivity(), ArticleViewAdapter.OnItemClickListener {
    lateinit var mSharedPref: SharedPreferences
    lateinit var idUser: String
    lateinit var articles: MutableList<Articles>
    lateinit var test: ArticleViewAdapter.OnItemClickListener
    lateinit var adap: ArticleViewAdapter
    lateinit var tesxt: TextView
    lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        idUser = mSharedPref.getString("_id","")!!
        setContentView(R.layout.activity_voir_article)
        animationView = findViewById(R.id.animationNoreponse)
        tesxt = findViewById(R.id.messageReponse)
        myArticleRV.layoutManager = LinearLayoutManager(this)
        myArticleRV.setHasFixedSize(true)


        getAllData(idUser){ articless : MutableList<Articles> ->
            if(articless.isEmpty()){
                println(articless)
                articles = articless
                adap = ArticleViewAdapter(articles,this,this)
                myArticleRV.visibility = View.GONE
                animationView.visibility = View.VISIBLE
                tesxt.visibility = View.VISIBLE
                animationView.playAnimation()
                animationView.loop(true)
            }

            myArticleRV.adapter = ArticleViewAdapter(articless,this,this)

        }

        val swipegesture = object : SwipeGesture(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val archiveItem = articles[viewHolder.adapterPosition]
                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder = AlertDialog.Builder(this@voirArticle)
                        builder.setMessage("Confirmer la suppression de l'article "+archiveItem.nom)
                            .setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->
                                val apiInterface = ApiArticle.create()

                                apiInterface.deleteArticle(archiveItem._id).enqueue(object:
                                    Callback<Articles> {
                                    override fun onResponse(
                                        call: Call<Articles>,
                                        response: Response<Articles>
                                    ) {
                                        if(response.isSuccessful){
                                            Log.i("onResponse goooood", response.body().toString())

                                        } else {
                                            Log.i("OnResponse not good", response.body().toString())
                                        }
                                    }
                                    override fun onFailure(call: Call<Articles>, t: Throwable) {
                                    }
                                })
                                adap.deleteItem(viewHolder.adapterPosition)

                            }
                            .setNegativeButton("No") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()

                    }
                    ItemTouchHelper.RIGHT -> {


                        //adap.deleteItem(viewHolder.adapterPosition)
                        print("id a envoye de l'article : "+archiveItem._id!!)
                        if(archiveItem.question == null){
                            Toast.makeText(applicationContext, "Article sans questions", Toast.LENGTH_SHORT).show()


                        }else if (archiveItem.question!!.reponse!!.isEmpty()){
                            Toast.makeText(applicationContext, "Pas de réponses ...", Toast.LENGTH_SHORT).show()

                        }else{
                            val intent = Intent(applicationContext, voirReponse::class.java)
                            intent.putExtra("idArticle",articles[viewHolder.adapterPosition]._id)
                            intent.putExtra("tokenfbUser",articles[viewHolder.adapterPosition].user!!.tokenfb)
                            intent.putExtra("question",articles[viewHolder.adapterPosition].question!!.titre!!)
                            startActivity(intent)
                        }

                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView(myArticleRV)

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
                    return callback(response.body()!!.articles!!)

                } else {
                    Log.i("OnResponse not good", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Articles>, t: Throwable) {
            }

        })
    }

    override fun onItemClick(position: Int, property: List<Articles>) {
        val intent = Intent(applicationContext, AjouterArticle::class.java)
        intent.putExtra("photo",property[position].photo)
        intent.putExtra("nom",property[position].nom)
        intent.putExtra("description",property[position].description)
        intent.putExtra("type",property[position].type)
        intent.putExtra("idArticle",property[position]._id)
        intent.putExtra("photoUser",property[position].user!!.photoProfil)
        intent.putExtra("nomUser",property[position].user!!.nom)
        //intent.putExtra("addresse","")



        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        myArticleRV.layoutManager = LinearLayoutManager(this)
        myArticleRV.setHasFixedSize(true)


        getAllData(idUser){ articless : MutableList<Articles> ->
            println(articless)
            articles = articless
            adap = ArticleViewAdapter(articles,this,this)
            myArticleRV.adapter = ArticleViewAdapter(articless,this,this)

        }
    }
}