package com.revcoding.mystoryapp.api

import com.revcoding.mystoryapp.data.model.LoginResponse
import com.revcoding.mystoryapp.data.model.RegisterResponse
import com.revcoding.mystoryapp.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<StoryResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>
}