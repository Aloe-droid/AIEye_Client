package com.example.client.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {
    @Headers("Content-Type: application/json")
    @POST("api/Account/SignIn")
    suspend fun sendLogin(@Body login: Login): Response<LoginResponse>
}