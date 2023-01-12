package com.amier.Activities.api

import com.amier.Activities.Constants
import com.amier.Activities.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiNotification {
    @POST("user/notif")
    fun pushNotif(
        @Body user: User
    ):Call<User>




    companion object {
        fun create() : ApiNotification {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()
            return retrofit.create(ApiNotification::class.java)

        }
    }
}