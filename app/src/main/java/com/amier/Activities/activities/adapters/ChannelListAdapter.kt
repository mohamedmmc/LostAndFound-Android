package com.amier.Activities.activities.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import com.sendbird.android.AdminMessage
import com.sendbird.android.FileMessage
import com.sendbird.android.GroupChannel
import com.sendbird.android.UserMessage
import kotlinx.android.synthetic.main.article_item.view.*
import kotlinx.android.synthetic.main.item_channel_chooser.view.*

class ChannelListAdapter(listener: OnChannelClickedListener, val context: Context) : RecyclerView.Adapter<ChannelListAdapter.ChannelHolder>() {
    inner class ChannelHolder(v: View) : RecyclerView.ViewHolder(v) {
        lateinit var mSharedPref: SharedPreferences

        val channelName = v.text_channel_name
        val imageuser = v.imageuser
        val channelRecentMessage = v.text_channel_recent


        fun bindViews(groupChannel: GroupChannel, listener: OnChannelClickedListener) {
            mSharedPref = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE)

            val lastMessage = groupChannel.lastMessage


            if (lastMessage != null) {

                when (lastMessage) {
                    is UserMessage -> channelRecentMessage.setText(lastMessage.message)
                    is AdminMessage -> channelRecentMessage.setText(lastMessage.message)
                    else -> {
                        val fileMessage = lastMessage as FileMessage
                        val sender = fileMessage.sender.nickname

                        channelRecentMessage.text = sender
                    }
                }
            }

            itemView.setOnClickListener {
                listener.onItemClicked(groupChannel)
            }

            println(groupChannel.members.toString())
            println("le con " +mSharedPref.getString("_id",""))
            if(mSharedPref.getString("_id","") == groupChannel.members[0].userId){
                Glide.with(itemView).load(groupChannel.members[1].profileUrl).into(imageuser)
                channelName.text = groupChannel.members[1].nickname
            }else{
                Glide.with(itemView).load(groupChannel.members[0].profileUrl).into(imageuser)
                channelName.text = groupChannel.members[0].nickname
            }


        }

    }
    interface OnChannelClickedListener {
        fun onItemClicked(channel: GroupChannel)
    }

    private val listener: OnChannelClickedListener
    private var channels: MutableList<GroupChannel>


    init {
        channels = ArrayList()
        this.listener = listener
    }

    fun addChannels(channels: MutableList<GroupChannel>) {
        this.channels = channels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChannelHolder(layoutInflater.inflate(R.layout.item_channel_chooser, parent, false))    }

    override fun getItemCount() = channels.size


    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        holder.bindViews(channels[position], listener)
    }

}