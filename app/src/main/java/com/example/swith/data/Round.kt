package com.example.swith.data

import com.google.gson.annotations.SerializedName

data class Round(
    val admin: Boolean,
    val announcementContent: String,
    val announcementDate: List<Int>,
    var getSessionResList: List<GetSessionRes>,
    val title: String
)

data class GetSessionRes(
    val attendanceRate: Int,
    val online: Any?,
    val place: Any?,
    val sessionContent: String,
    val sessionEnd: List<Int>,
    val sessionIdx: Int,
    val sessionNum: Int,
    val sessionStart: List<Int>
)

data class RoundResponse(
    @SerializedName("result") val round: Round
)
