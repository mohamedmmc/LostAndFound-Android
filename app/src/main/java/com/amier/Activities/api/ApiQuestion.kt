package com.amier.Activities.api

import com.amier.Activities.Constants
import com.amier.Activities.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiQuestion {
    @POST("question")
    fun postQuestion(
        @Body user: Question
    ):Call<Question>

    @POST("reponse/{id}")
    fun postReponse(@Path("id") idArticle: String?,
        @Body reponse: Reponse
    ):Call<Reponse>


    companion object {
        fun create() : ApiQuestion {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()
            return retrofit.create(ApiQuestion::class.java)

        }
    }
}