package com.example.msharma.practice.network

import com.example.msharma.practice.data.Response
import io.reactivex.Single

class PhotoService(private val photoApi: PhotoApi, private val apiKey: String, private val extraString: String) {
    fun getPhotosByKeyword(pageNumber: Int, textToSearch: String): Single<Response> {
        return photoApi.getPhotos(pageNumber, queryText = textToSearch, extras = extraString, apiKey = apiKey)
    }

}