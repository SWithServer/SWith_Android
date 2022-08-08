package com.example.swith.data

import com.google.gson.annotations.SerializedName

data class SessionInfo(
    var getAttendanceList: List<GetAttendance>,
    val groupImgUrl: String?,
    val online: Int?,
    val place: String?,
    val sessionContent: String,
    val sessionEnd: List<Int>,
    val sessionIdx: Int,
    val sessionNum: Int,
    val sessionStart: List<Int>,
    val userMemo: String
)

data class GetAttendance(
    val nickname: String,
    val status: Int,
    val userIdx: Int
)

data class SessionInfoResponse(
    @SerializedName("result") val session: SessionInfo
)