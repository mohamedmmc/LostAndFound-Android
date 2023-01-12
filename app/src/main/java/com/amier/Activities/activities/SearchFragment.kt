package com.amier.Activities.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amier.Activities.api.Api
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.models.Articles
import com.amier.modernloginregister.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_search.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.text.Editable

import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.amier.Activities.activities.adapters.ArticleViewAdapter
import com.amier.Activities.models.User


class SearchFragment : Fragment() , ArticleViewAdapter.OnItemClickListener{
    lateinit var mSharedPref: SharedPreferences


    var filtredArticle: MutableList<Articles> = arrayListOf()
    var articlesDispo: MutableList<Articles> = arrayListOf()
    lateinit var test: ArticleViewAdapter.OnItemClickListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var searcha = ""
        test = this
        var happycheck = false
        var sadcheck = false
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        view.recyclerView.layoutManager = LinearLayoutManager(activity)
        view.recyclerView.setHasFixedSize(true)
        getNewsData { newss: List<Articles> ->
            articlesDispo = newss as MutableList<Articles>

            view.recyclerView.adapter = ArticleViewAdapter(newss, this,context!!)
        }
        mSharedPref = view.context!!.getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        if (!mSharedPref.getBoolean("isVerified", false)) {
            val apiInterfacee = Api.create()

            apiInterfacee.checkVerified(mSharedPref.getString("_id", "")!!)
                .enqueue(object : Callback<User> {

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        Log.i("reponse code user check",response.code().toString())
                        if (response.isSuccessful) {
                            if (response.body()!!.isVerified!!) {
                                mSharedPref.edit()
                                    .putBoolean("isVerified", response.body()!!.isVerified!!)
                                    .apply()
                            }
                            //}
                        } else {
                            Log.i("nooooo", response.body().toString())
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        t.printStackTrace()
                        println("OnFailure")
                    }

                })

        }


        val ajouterArticleButton = view.findViewById<Button>(R.id.ajouterArticle)

        val lost = view.findViewById<TextView>(R.id.Lost)
        val found = view.findViewById<TextView>(R.id.Found)
        val linearlayoutLost = view.findViewById<LinearLayout>(R.id.linearlayoutLost)
        val linearlayoutFound = view.findViewById<LinearLayout>(R.id.linearlayoutFound)
        val happy = view.findViewById<ImageView>(R.id.happy)
        val report = view.findViewById<ImageView>(R.id.report)
        val sad = view.findViewById<ImageView>(R.id.sad)
        val searchBar = view.findViewById<TextInputLayout>(R.id.searchBar)
        val keyword = view.findViewById<TextInputEditText>(R.id.keyword)

        linearlayoutLost.setOnClickListener {
            if (!sadcheck) {
                sad.setImageResource(R.drawable.sadcolored)
                happy.setImageResource(R.drawable.happy)
                lost.setTextColor(Color.BLUE)
                found.setTextColor(Color.BLACK)
                sadcheck = true
                happycheck = false
                if (articlesDispo.size > 0) {
                filtredArticle.clear()

                    articlesDispo.forEach {
                        if (it.type!!.contains("Lost")) {
                            filtredArticle.add(it)
                        }
                    }
                    view.recyclerView.adapter = ArticleViewAdapter(filtredArticle, test,context!!)
                }
            }
            else {
                sad.setImageResource(R.drawable.sad)
                lost.setTextColor(Color.BLACK)
                sadcheck = false
                view.recyclerView.adapter = ArticleViewAdapter(articlesDispo, test,context!!)
            }

        }
        linearlayoutFound.setOnClickListener {
            if (!happycheck) {
                sad.setImageResource(R.drawable.sad)
                happy.setImageResource(R.drawable.happycolored)
                found.setTextColor(Color.BLUE)
                lost.setTextColor(Color.BLACK)
                happycheck = true
                sadcheck = false
                if (articlesDispo.size > 0) {
                    filtredArticle.clear()

                    articlesDispo.forEach {
                        if (it.type!!.contains("Found")) {
                            filtredArticle.add(it)
                        }
                    }
                    view.recyclerView.adapter = ArticleViewAdapter(filtredArticle, test,context!!)
                }

            }
            else {
                happy.setImageResource(R.drawable.happy)
                found.setTextColor(Color.BLACK)
                happycheck = false
                view.recyclerView.adapter = ArticleViewAdapter(articlesDispo, test,context!!)
            }
        }


        ajouterArticleButton.setOnClickListener {

            if(!mSharedPref.getBoolean("isVerified",false)){
                showAlertDialog()

            }else{
                val intent = Intent(activity, selectTypeArticle::class.java)
                startActivity(intent)
            }

        }
        keyword.setOnFocusChangeListener(View.OnFocusChangeListener { view, focused ->
            val keyboard =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (focused) keyboard.showSoftInput(keyword, 0) else keyboard.hideSoftInputFromWindow(
                keyword.getWindowToken(),
                0
            )
        })
        keyword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    filtredArticle.clear()
                    articlesDispo.forEach {
                        if (it.nom!!.contains(s)) {
                            filtredArticle.add(it)
                        }
                    }
                    view.recyclerView.adapter = ArticleViewAdapter(filtredArticle, test,context!!)
                }
                if(s.isEmpty()){
                    view.recyclerView.adapter = ArticleViewAdapter(articlesDispo, test,context!!)
                }
                searcha = s.toString()
            }
        })


        return view
    }
    private fun getNewsData(callback: (List<Articles>) -> Unit) {
        val apiInterface = ApiArticle.create()

        apiInterface.GetAllArticles().enqueue(object: Callback<Articles> {
            override fun onResponse(call: Call<Articles>, response: Response<Articles>) {
                Log.i("reponse code ",response.code().toString())
                if(response.code() == 200){
                    return callback(response.body()!!.articles!!)
                    Log.i("yessss", response.body().toString())
                    //}
                }else if(response.code() == 201){

                }else {
                    Log.i("nooooo", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Articles>, t: Throwable) {
                t.printStackTrace()
                println("OnFailure")
            }

        })
    }
    private fun showAlertDialog(){

        val builder = AlertDialog.Builder(view!!.context)
        builder.setTitle("Alert")
        builder.setMessage("Verifier d'abord votre compte avec l'email envoyé!")

        builder.show()
    }
    override fun onItemClick(position: Int, articles: List<Articles>) {



            val intent = Intent(activity, DetailArticle::class.java)
            intent.putExtra("nom",articles[position].nom)

            if(articles[position].addresse!!.isNotEmpty()){
                intent.putExtra("lalti", articles[position].addresse?.get(0))
                intent.putExtra("longi",articles[position].addresse?.get(1))
            }
            //intent.putExtra("addresse",articles[position].addresse)
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
            intent.putExtra("tokenfbUser", articles[position].user?.tokenfb)
            intent.putExtra("questionTitle", articles[position].question?.titre)
            startActivity(intent)


    }
    override fun onResume() {
        mSharedPref.edit().apply{
            remove("lat")
            remove("long")
        }.apply()
        super.onResume()
    }


}