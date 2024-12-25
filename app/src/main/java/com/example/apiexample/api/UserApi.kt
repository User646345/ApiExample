package com.example.apiexample.api

import com.example.apiexample.UserCreation
import com.example.apiexample.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @Headers(
        "Accept: application/json"
    )
    @GET("users/{id}")
    fun getUserById(@Path("id") id: String): Call<UserModel?>?

    @POST("user/")
    fun createUser(@Body user: UserCreation): Call<Void>

    @DELETE("user/{id}")
    fun deleteUserById(@Path("id") userId: String): Call<Void>
}