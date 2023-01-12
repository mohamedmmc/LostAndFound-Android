package com.amier.Activities.api

import com.amier.Activities.Constants
import com.amier.Activities.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiReponse {

    @GET("reponse/{id}")
    fun getReponses(
        @Path("id") id: String?
    ):Call<Reponse>

    @DELETE("reponse/{id}")
    fun deleteReponse(
        @Path("id") id: String?
    ):Call<Reponse>


    companion object {
        fun create() : ApiReponse {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()
            return retrofit.create(ApiReponse::class.java)

        }
    }
}