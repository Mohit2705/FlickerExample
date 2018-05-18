package com.example.msharma.practice.di.module

import com.example.msharma.practice.BuildConfig
import com.example.msharma.practice.network.PhotoApi
import com.example.msharma.practice.network.PhotoService
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


private const val END_POINT = "api.flickr.com"

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideBaseURL(): HttpUrl {
        return HttpUrl.Builder()
                .scheme("https")
                .host(END_POINT)
                .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): Interceptor {
        val logLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val loggingInterceptor = HttpLoggingInterceptor()
        return loggingInterceptor.setLevel(logLevel)

    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetroFit(baseURL: HttpUrl, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun providePhotoAPI(retrofit: Retrofit): PhotoApi {
        return retrofit.create(PhotoApi::class.java)
    }

    // TODO configure different api key for production
    @Provides
    @Singleton
    fun providePhotoAPIKey() = "3e7cc266ae2b0e0d78e279ce8e361736"

    fun provideExtraString() = "url_t"

    @Provides
    @Singleton
    fun providePhotoService(api: PhotoApi, apiKey: String): PhotoService {
        return PhotoService(api, apiKey, provideExtraString())
    }

}
