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
    @POST("/v1/register")
    fun registerUser(
        @Field("name") name: String? = null,
        @Field("email") email: String? = null,
        @Field("password") password: String? = null
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun loginUser(
        @Field("email") email: String? = null,
        @Field("password") password: String? = null
    ): Call<LoginResponse>

    @Multipart
    @POST("/v1/stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<StoryResponse>

    @GET("/v1/stories")
    fun getAllStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>
}