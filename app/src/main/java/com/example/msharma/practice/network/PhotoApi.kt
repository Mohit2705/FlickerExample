package com.example.msharma.practice.network

import com.example.msharma.practice.data.Response
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


private const val NUMBER_OF_IMAGES = 50
interface PhotoApi {
    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    fun getPhotos(@Query("page") pageNumber: Int,
                  @Query("per_page") perPageImage: Int = NUMBER_OF_IMAGES,
                  @Query("text") queryText: String,
                  @Query("extras") extras: String,
                  @Query("api_key") apiKey: String): Single<Response>

}