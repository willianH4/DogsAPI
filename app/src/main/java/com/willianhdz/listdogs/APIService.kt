package com.willianhdz.listdogs

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getDog(@Url url:String): Response<DogResponse>
}