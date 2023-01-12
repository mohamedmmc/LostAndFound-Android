package com.amier.Activities.activities.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amier.Activities.api.ApiReport
import com.amier.Activities.models.Articles
import com.amier.Activities.models.Report
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.article_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ArticleViewAdapter    (private val listArticle : MutableList<Articles>, private val listener: OnItemClickListener, val context: Context):
    RecyclerView.Adapter<ArticleViewAdapter.MyViewHolder>(){
    val DayInMilliSec = 86400000
    lateinit var mSharedPref: SharedPreferences

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {



        @SuppressLint("ResourceAsColor")
        fun bind(property: Articles){
            mSharedPref = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE,)

            itemView.ArticleName.text = property.nom
            itemView.ArticleDescription.text = property.description
            itemView.Creation.text = getDateTime(property.dateCreation!!)
            itemView.textView9.text = property.type
            if(mSharedPref.getString("_id","") == property.user!!._id){
                itemView.report.visibility = View.INVISIBLE
            }
            if(property.question == null){
                itemView.question.visibility = View.INVISIBLE
            }
            else if (property.question!!.reponse!!.isEmpty() && mSharedPref.getString("_id","") == property.user!!._id){
                itemView.question.setColorFilter(Color.argb(255, 255, 0, 0))

            }else{
                if(mSharedPref.getString("_id","") == property.user!!._id)
                itemView.question.setColorFilter(Color.argb(255, 0, 255, 0))

            }
            Glide.with(itemView).load(property.photo).into(itemView.ArticleImage)
            itemView.report.setOnClickListener {
                val builder = AlertDialog.Builder(it.context)
                builder.setMessage("Etes vous sur de vouloir reporter cet article ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui") { dialog, id ->
                        val apiInterface = ApiReport.create()
                        val position: Int = adapterPosition
                        var report = Report()
                        report.article = property._id
                        report.user= mSharedPref.getString("_id", "")!!.toString()
                        println("wa ya jon : "+report.user)
                        apiInterface.postReport(report).enqueue(object :
                            Callback<Report> {
                            override fun onResponse(
                                call: Call<Report>,
                                response: Response<Report>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        itemView.context,
                                        "Article reporté",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.i("onResponse goooood", response.body().toString())

                                } else if (response.code()==400) {
                                    Toast.makeText(
                                        itemView.context,
                                        "Article dejà reporté !",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else if (response.code()==300) {
                                    Toast.makeText(
                                        itemView.context,
                                        "L'article à va être supprimer !",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<Report>, t: Throwable) {
                            }
                        })


                    }
                    .setNegativeButton("Non") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }
        init {
            itemView.setOnClickListener (this)
        }
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position,listArticle)
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int,property: List<Articles>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false))
    }
    override fun getItemCount(): Int {
        println(listArticle.size)
        return listArticle.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(listArticle.get(position))
    }

     fun deleteItem(i: Int){
        listArticle.removeAt(i)
         notifyDataSetChanged()

    }
     fun addItem(i : Int, cars : Articles) {

        //listArticle.add(i,cars)
        //notifyDataSetChanged()

    }
    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/YYYY")
            val netDate = Date(s.toLong()  ).addDays(1)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun Date.addDays(numberOfDaysToAdd: Int): Date{
        return Date(this.time + numberOfDaysToAdd * DayInMilliSec)
    }
}