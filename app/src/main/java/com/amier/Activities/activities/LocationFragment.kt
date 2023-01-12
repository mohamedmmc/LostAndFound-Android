package com.amier.Activities.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.amier.Activities.activities.adapters.ChannelListAdapter
import com.amier.modernloginregister.R
import com.sendbird.android.GroupChannel
import kotlinx.android.synthetic.main.activity_channel.*
import kotlinx.android.synthetic.main.fragment_location.*
import kotlinx.android.synthetic.main.fragment_location.view.*


class LocationFragment : Fragment() , ChannelListAdapter.OnChannelClickedListener{
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    lateinit var recyclerView: RecyclerView
    lateinit var messageAnimation: LottieAnimationView

    lateinit var adapter: ChannelListAdapter
    lateinit var rcx: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        rcx = view.findViewById(R.id.rcx)
        messageAnimation = view.findViewById(R.id.pasdemsg)
        adapter = ChannelListAdapter(this,view.context)
        recyclerView = rcx
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)



        addChannels()

        return view
    }
    private fun addChannels() {
        val channelList = GroupChannel.createMyGroupChannelListQuery()
        channelList.limit = 100
        channelList.next { list, e ->
            if (e != null) {
                Log.e("TAG", e.message)
            }
            if(list.size != 0){
                adapter.addChannels(list)
                messageAnimation.visibility = View.GONE

            }else{
                messageAnimation.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                blabla.visibility = View.VISIBLE
                messageAnimation.playAnimation()
                messageAnimation.loop(true)
            }
        }
    }

    override fun onItemClicked(channel: GroupChannel) {
        val intent = Intent(activity, ChannelActivity::class.java)
        intent.putExtra(EXTRA_CHANNEL_URL, channel.url)
        startActivity(intent)
    }
}