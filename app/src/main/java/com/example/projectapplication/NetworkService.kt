package com.example.projectapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("gardenList")
    fun getXmlList(
        @Query("apiKey") apiKey: String,
        @Query("sText") sText: String, // 검색어
        @Query("pageNo") pageNo: Int, // 조회할 페이지 번호
        @Query("numOfRows") numOfRows: Int, // 한 페이지에 제공할 건수
        @Query("returnType") returnType: String // 반환 타입
    ): Call<XmlResponse>

    @GET("gardenDtl")
    fun getXmlDetailList(
        @Query("apiKey") apiKey: String,
        @Query("cntntsNo") cntntsNo: Int, // 검색어
        @Query("pageNo") pageNo: Int, // 조회할 페이지 번호
        @Query("numOfRows") numOfRows: Int, // 한 페이지에 제공할 건수
        @Query("returnType") returnType: String // 반환 타입
    ): Call<XmlDetailResponse>
}

/*@Query("sText") sText: String,
        @Query("numOfRows") numOfRows:Int,
        @Query("returnType") returnType:String,
        @Query("apiKey") apiKey: String,*/