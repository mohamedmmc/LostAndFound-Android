package com.amier.Activities.api

import com.amier.Activities.Constants
import com.amier.Activities.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiArticle {
    @Multipart
    @POST("article")
    fun ajoutArticle(
        @PartMap data : LinkedHashMap<String, RequestBody>,
        @Part profilePicture: MultipartBody.Part
    ) : Call<Articles>


    @Multipart
    @PATCH("article/{id}")
    fun modifierArticle(
        @Path("id") idArticle: String?,
        @PartMap data : LinkedHashMap<String, RequestBody>,
        @Part profilePicture: MultipartBody.Part?
    ) : Call<Articles>

    @GET("article")
    fun GetAllArticles():Call<Articles>

    @DELETE("article/{id}")
    fun deleteArticle(@Path("id") idArticle: String?):Call<Articles>

    @GET("article/myArticles/{id}")
    fun GetMyArticles(@Path("id") id: String?):Call<Articles>

    @GET("article/myArticles/{id}")
    fun GetArticlesByUser(@Path("id") id: String?): Call<Articles>

    @GET("association")
    fun getAss(): Call<Association>

    @GET("association/participation/{id}")
    fun getParticipation(
        @Path("id") id: String?,
        @Body user: User
    ): Call<Association>


    @POST("association/{id}")
    fun posterContribution(
        @Path("id") id: String?,
        @Body user: Association
    ): Call<Association>

    @PATCH("association/participation/{id}")
    fun voirContribution(
        @Path("id") id: String?,
        @Body user: User
    ): Call<Articles>

    companion object {
        fun create() : ApiArticle {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()
            return retrofit.create(ApiArticle::class.java)

        }
    }
}