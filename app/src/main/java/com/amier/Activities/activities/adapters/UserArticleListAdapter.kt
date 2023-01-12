package com.amier.Activities.activities.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amier.Activities.activities.DetailUserArticle
import com.amier.Activities.models.Articles
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_user_articles.view.*
import java.text.SimpleDateFormat
import java.util.*


class UserArticleListAdapter(private val userArticleList: List<Articles>, private val listener: DetailUserArticle) : RecyclerView.Adapter<UserArticleListAdapter.MyViewHolder>() {
    val DayInMilliSec = 86400000

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_user_articles, parent, false)
        return MyViewHolder(itemView)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        fun bind(property: Articles) {
            itemView.articleTitlee.text = property.nom
            itemView.articleTypee.text = property.type
            itemView.articleCreationDate.text = "Date d'ajout: " + getDateTime(property.dateCreation!!)
            Glide.with(itemView).load(property.photo).into(itemView.imageArticlee)

        }
        init {
            itemView.setOnClickListener (this)
        }
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position,userArticleList)
            }
        }



    }
    interface OnItemClickListener {
        fun onItemClick(position: Int,property: List<Articles>)
    }



    override fun getItemCount(): Int {
        return userArticleList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(userArticleList.get(position))

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

    fun Date.addDays(numberOfDaysToAdd: Int): Date {
        return Date(this.time + numberOfDaysToAdd * DayInMilliSec)
    }



}