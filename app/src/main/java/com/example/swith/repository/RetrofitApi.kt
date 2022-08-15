package com.example.swith.repository

import com.example.swith.data.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDateTime

interface RetrofitApi {
    // 홈화면 정보 받기
    @GET("/groupinfo/home")
    suspend fun getAllStudy(@Query("userIdx") userIdx: Int) : Response<GroupList>

    // 회차 화면 정보 받기
    @GET("/groupinfo/session")
    suspend fun getAllRound(@Query("userIdx") userIdx: Int, @Query("groupIdx") groupIdx: Int) : Response<RoundResponse>

    // 공지사항 받기
    @GET("/groupinfo/announcement/{groupIdx}")
    suspend fun getAllAnnounce(@Path("groupIdx") groupIdx: Int) : Response<AnnounceList>

    // 회차 생성
    @POST("/groupinfo/session")
    suspend fun createRound(@Body session: Session): Response<SessionResponse>

    // 회차 정보(개요, 출석, 메모)
    @GET("/groupinfo/session/info")
    suspend fun getSessionInfo(@Query("userIdx") userIdx: Int, @Query("sessionIdx") sessionIdx: Int) : Response<SessionInfoResponse>

    // 출석 업데이트
    @PATCH("/groupinfo/attendance")
    suspend fun updateAttend(@Query("userIdx") userIdx: Int, @Query("sessionIdx") sessionIdx: Int) : Response<AttendResponse>

    // 공지사항 삭제
    @PATCH("/groupinfo/announcement/{announcementIdx}/status")
    suspend fun deleteAnnounce(@Path("announcementIdx") announcementIdx: Int) : Response<AnnounceDelete>

    // 공지사항 생성
    @POST("/groupinfo/announcement")
    suspend fun createAnnounce(@Body announceCreate: AnnounceCreate) : Response<Any>

    // 공지사항 수정
    @PATCH("/groupinfo/announcement")
    suspend fun updateAnnounce(@Body announceModify: AnnounceModify) : Response<Any>

    //스터디 개설
    @POST("/groupinfo")
     fun createStudy(@Body body:StudyGroup) : Call<StudyResponse>

     //임시 스터디 찾기 - 스터디 불러오기 부분
    @GET("/groupinfo/search")
    fun getSearchStudy(@Query("limit") limit : Int=5,@Query("title") title:String?=null,@Query("regionIdx") regionIdx : String?= null, @Query("interest1") interest1:Int?=null, @Query("interest2") interest2:Int?=null,@Query("groupIdx") groupIdx : Int?=null,@Query("sortCond") sortCond:Int?=null,@Query("ClientTime") ClientTime :LocalDateTime):Call<StudyFindResponse>
}