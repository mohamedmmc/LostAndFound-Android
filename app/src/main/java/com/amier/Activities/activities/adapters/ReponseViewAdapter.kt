package com.amier.Activities.activities.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amier.Activities.models.Articles
import com.amier.Activities.models.Reponse
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_reponse.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReponseViewAdapter    (private val listReponse : MutableList<Reponse>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<ReponseViewAdapter.MyViewHolder>(){
    val DayInMilliSec = 86400000

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {



        @SuppressLint("ResourceAsColor")
        fun bind(property: Reponse){
            itemView.userName.text = property.user!!.nom
            itemView.reponse.text = "Reponse : "+property.description


//            if(property.type.equals("Lost")){
//                itemView.article.setBackgroundColor(Color.RED)
//            }else{
//                itemView.article.setBackgroundColor(Color.GREEN)
//            }
            Glide.with(itemView).load(property.user!!.photoProfil).into(itemView.userimage)
        }
        init {
            itemView.setOnClickListener (this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position,listReponse)
            }
        }

    }
    fun deleteItem(i: Int){
        listReponse.removeAt(i)
        notifyDataSetChanged()

    }
    fun addItem(i : Int, cars : Articles) {

        //listArticle.add(i,cars)
        //notifyDataSetChanged()

    }
    interface OnItemClickListener {
        fun onItemClick(position: Int,property: List<Reponse>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_reponse, parent, false))
    }
    override fun getItemCount(): Int {
        println(listReponse.size)
        return listReponse.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(listReponse.get(position))
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