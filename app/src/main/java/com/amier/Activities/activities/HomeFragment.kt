package com.amier.Activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amier.Activities.activities.adapters.AssociationViewAdapter
import com.amier.Activities.api.ApiArticle
import com.amier.Activities.models.Association
import com.amier.modernloginregister.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment()  , AssociationViewAdapter.OnItemClickListener{

    var associationDispo: MutableList<Association> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        view.recycler_viewAssociation.layoutManager = LinearLayoutManager(activity)
        view.recycler_viewAssociation.setHasFixedSize(true)
        getNewsData { newss: List<Association> ->
            associationDispo = newss as MutableList<Association>

            view.recycler_viewAssociation.adapter = AssociationViewAdapter(newss,this)
        }



        return view
    }

    private fun getNewsData(callback: (List<Association>) -> Unit) {
        val apiInterface = ApiArticle.create()

        apiInterface.getAss().enqueue(object: Callback<Association> {
            override fun onResponse(call: Call<Association>, response: Response<Association>) {
                Log.i("reponse code ",response.code().toString())
                if(response.code() == 200) {
                    return callback(response.body()!!.association!!)
                    Log.i("yessss", response.body().toString())
                }else {
                    Log.i("nooooo", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Association>, t: Throwable) {
                t.printStackTrace()
                print("hello hello"+t.message)
            }

        })
    }

    override fun onItemClick(position: Int, property: List<Association>) {
        val intent = Intent(activity, AssociationDonation::class.java)
        intent.putExtra("nomAss",property[position].nom)
        intent.putExtra("idAss",property[position]._id)
        intent.putExtra("categorie",property[position].categorie)
        intent.putExtra("numTel",property[position].numTel)
        intent.putExtra("photo",property[position].photo)
        startActivity(intent)
    }

}