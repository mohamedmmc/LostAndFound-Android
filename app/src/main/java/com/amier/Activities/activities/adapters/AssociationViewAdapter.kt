package com.amier.Activities.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amier.Activities.models.Association
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.association_item.view.*


class AssociationViewAdapter(private val associationList: List<Association>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<AssociationViewAdapter.ViewHolder>() {


    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view), View.OnClickListener {

        fun bind(property: Association){

            Glide.with(itemView).load(property.photo).into(itemView.associationImage)
            itemView.associationNom.text = property.nom
            itemView.associationNumTel.text = property.numTel
            itemView.associationCategorie.text = property.categorie
        }
        init {
            itemView.setOnClickListener (this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position,associationList)
            }
        }


    }

    interface OnItemClickListener {
        fun onItemClick(position: Int,property: List<Association>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.association_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(associationList.get(position))
    }

    override fun getItemCount(): Int {
        return associationList.size
    }

}