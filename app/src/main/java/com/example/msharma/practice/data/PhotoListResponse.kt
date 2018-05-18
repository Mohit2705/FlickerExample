package com.example.msharma.practice.data

import com.google.gson.annotations.SerializedName

data class Response(val photos: PhotoListResponse)
data class PhotoListResponse(@SerializedName("page") val currentPage: Int,
                             @SerializedName("pages") val totalPage: Int,
                             @SerializedName("photo") val photoList: List<Photo>,
                             @SerializedName("perPage") val perPage: Int,
                             @SerializedName("total") val totalImages: Int)

data class Photo(@SerializedName("id") val photoId: String,
                 val title: String,
                 @SerializedName("url_t") val thumbnailUrl: String,
                 @SerializedName("farm") val farmId: String,
                 @SerializedName("server") val serverId: String,
                 @SerializedName("secret") val secretId: String)
