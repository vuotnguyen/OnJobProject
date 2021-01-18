package com.example.onjobproject.dagger.modul

import com.example.onjobproject.view.RetrofitService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModul  {
    private val BASE_URL:String = "https://fcm.googleapis.com/"

    @Singleton
    @Provides
    fun provideRetrofit() :Retrofit{
        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
    @Singleton
    @Provides
    fun provideService(retrofit: Retrofit): RetrofitService{
        return retrofit.create(RetrofitService::class.java)
    }
}