package com.amier.Activities.api

import com.amier.Activities.Constants
import com.amier.Activities.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {

    @Multipart
    @PATCH("user/{id}")
    fun userUpdate(
        @Path("id") id:String,
        @PartMap data : LinkedHashMap<String, RequestBody>,
        @Part profilePicture: MultipartBody.Part?
    ) : Call<User>



    @POST("user/forgotPassword")
    fun forgotpassword(
        @Body email: User
    ):Call<User>

    @POST("user/resetPassword/{email}/{token}")
    fun resetpassword(
        @Path("email") email:String,
        @Path("token") token:String,
        @Body password: User,
    ):Call<User>

    @Multipart
    @POST("user")
    fun userSignUp(
        @PartMap data: LinkedHashMap<String, RequestBody>,
        @Part profilePicture: MultipartBody.Part?
    ) : Call<User>

    @POST("user/login")
    fun userLogin(
        @Body user: User
    ):Call<User>

    @POST("user/updateTokenFb/{id}")
    fun updateToken(
        @Path("id") id: String?,
        @Body user: User
    ):Call<User>

    @GET("user/findUser/{id}")
    fun getUser(
        @Path("id") id: String
    ):Call<User>


    @GET("user/oyoy/{id}")
    fun checkVerified(
        @Path("id") id: String,

    ):Call<User>

    @POST("user/Auth")
    fun userLoginSocial(
        @Body user: User
    ):Call<User>


    @POST("https://api-C2B86342-5275-4183-9F0C-28EF1E4B3014.sendbird.com/v3/users")
    @Headers("Api-Token: 9838c32272965383009ace17937bb8565e108d38")
    fun sendBirdCreate(
        @Body user: SendBirdUser
    ):Call<SendBirdUser>

    companion object {
        fun create() : Api {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()
            return retrofit.create(Api::class.java)

        }
    }
}