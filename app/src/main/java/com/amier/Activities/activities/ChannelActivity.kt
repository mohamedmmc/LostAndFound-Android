package com.amier.Activities.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.amier.Activities.activities.adapters.MessageAdapter
import com.amier.Activities.api.Api
import com.amier.Activities.api.ApiNotification
import com.amier.Activities.models.User
import com.amier.modernloginregister.R
import com.bumptech.glide.Glide
import com.sendbird.android.*
import kotlinx.android.synthetic.main.activity_ajouter_article.*
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChannelActivity : AppCompatActivity() {

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    private val CHANNEL_HANDLER_ID = "sendbird_group_channel_4734534_460254d0effd0f3824c67265e1d57a27b1f749b2"
    lateinit var mSharedPref: SharedPreferences
    lateinit var discussionUserId: String
    lateinit var tokenFB: String


    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupChannel: GroupChannel
    private lateinit var channelUrl: String
    private lateinit var imageuser: ImageView
    private lateinit var nameuser: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        imageuser = findViewById(R.id.imageuser)
        nameuser = findViewById(R.id.Name)
        setUpRecyclerView()
        setButtonListeners()

    }

    override fun onResume() {
        super.onResume()
        channelUrl = getChannelURl()


        GroupChannel.getChannel(channelUrl,
            GroupChannel.GroupChannelGetHandler { groupChannel, e ->
                if (e != null) {
                    // Error!
                    e.printStackTrace()
                    return@GroupChannelGetHandler
                }
                this.groupChannel = groupChannel
                if(mSharedPref.getString("_id","") == groupChannel.members[0].userId){
                    Glide.with(imageuser).load(groupChannel.members[1].profileUrl).into(imageuser)
                    nameuser.text = groupChannel.members[1].nickname
                    discussionUserId = groupChannel.members[1].userId
                }else{
                    Glide.with(imageuser).load(groupChannel.members[0].profileUrl).into(imageuser)
                    nameuser.text = groupChannel.members[0].nickname
                    discussionUserId = groupChannel.members[0].userId
                }


                getMessages()
                setUpTokenfb()
            })

        SendBird.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : SendBird.ChannelHandler() {
                override fun onMessageReceived(
                    baseChannel: BaseChannel,
                    baseMessage: BaseMessage
                ){
                    if (baseChannel.url == channelUrl) {
                        // Add new message to view
                        adapter.addFirst(baseMessage)
                        groupChannel.markAsRead()
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
    }

    /**
     * Function handles setting handlers for back/send button
     */
    private fun setButtonListeners() {
        val back = button_gchat_back
        back.setOnClickListener {
            finish()
        }

        val send = button_gchat_send
        send.setOnClickListener {

            sendMessage()
        }
    }

    /**
     * Sends the message from the edit text, and clears text field.
     */
    private fun sendMessage()
    {
        val params = UserMessageParams()
            .setMessage(edit_gchat_message.text.toString())
        groupChannel.sendUserMessage(params,
            BaseChannel.SendUserMessageHandler { userMessage, e ->
                if (e != null) {    // Error.
                    return@SendUserMessageHandler
                }

                adapter.addFirst(userMessage)
                edit_gchat_message.text.clear()
                val notif= User()
                notif.tokenfb = tokenFB
                notif.nom = mSharedPref.getString("nom","")
                println("on va envoyer une notif a :"+nameuser.text.toString())
                println("on une notif a :"+discussionUserId)
                val apiInterface = ApiNotification.create()
                apiInterface.pushNotif(notif).enqueue(object:
                    Callback<User> {
                    override fun onResponse(
                        call: Call<User>,
                        response: Response<User>
                    ) {
                        if(response.isSuccessful){

                            Toast.makeText(applicationContext, "Notification envoyé avec succés", Toast.LENGTH_LONG).show()

                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                    }
                })
            })
    }


    /**
     * Function to get previous messages in channel
     */
    private fun getMessages() {

        val previousMessageListQuery = groupChannel.createPreviousMessageListQuery()

        previousMessageListQuery.load(
            100,
            true,
            object : PreviousMessageListQuery.MessageListQueryResult {
                override fun onResult(
                    messages: MutableList<BaseMessage>?,
                    e: SendBirdException?
                ) {
                    if (e != null) {
                        Log.e("Error", e.message)
                    }
                    adapter.loadMessages(messages!!)
                }
            })

    }

    /**
     * Set up the  recyclerview and set the adapter
     */
    private fun setUpRecyclerView() {
        adapter = MessageAdapter(this)
        recyclerView = recycler_gchat
        recyclerView.adapter = adapter

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.scrollToPosition(0)

    }

    private fun setUpTokenfb() {

        val apiInterface = Api.create()
        apiInterface.getUser(discussionUserId).enqueue(object:
            Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if(response.isSuccessful){
                    tokenFB = response.body()?.tokenfb.toString()
                    println("token : "+tokenFB)

                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
            }
        })

    }

    /**
     * Get the Channel URL from the passed intent
     */
    private fun getChannelURl(): String {
        val intent = this.intent
        return intent.getStringExtra(EXTRA_CHANNEL_URL)
    }


}