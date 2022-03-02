package com.example.films.retrofit

import com.example.films.data.Response
import retrofit2.Call
import retrofit2.http.*


interface RetrofitServices {
    @GET("/films")
    fun getMovieList(): Call<MutableList<Response>>

    @PUT("/films/{id}")
    fun like(@Path("id") id: String): Call<Unit>
}