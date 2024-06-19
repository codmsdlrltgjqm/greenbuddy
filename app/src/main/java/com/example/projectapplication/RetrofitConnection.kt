package com.example.projectapplication

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection{
    companion object {

        private const val BASE_URL = "http://api.nongsaro.go.kr/service/garden/"

        var xmlNetworkService : NetworkService
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val xmlRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(TikXmlConverterFactory.create(parser))
                .build()

        init{
            xmlNetworkService = xmlRetrofit.create(NetworkService::class.java)
        }
    }
}