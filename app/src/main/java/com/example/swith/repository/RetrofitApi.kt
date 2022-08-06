package com.example.swith.repository

import com.example.swith.data.*
import retrofit2.Response
import retrofit2.http.*

interface RetrofitApi {
    @GET("/groupinfo/home")
    suspend fun getAllStudy(@Query("userIdx") userIdx: Int) : Response<GroupList>

    @GET("/groupinfo/session")
    suspend fun getAllRound(@Query("userIdx") userIdx: Int, @Query("groupIdx") groupIdx: Int) : Response<RoundResponse>

    @POST("/session")
    suspend fun createRound(@Body session: Session): Response<SessionResponse>
}