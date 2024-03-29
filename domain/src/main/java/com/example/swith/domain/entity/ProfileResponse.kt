package com.example.swith.domain.entity

data class ProfileResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: ProfileResult,
)

data class ProfileResult(
    val accessToken: String,
    val averageStar: Long,
    val email: String,
    val interestIdx1: Int,
    val interestIdx2: Int,
    val region: String,
    val introduction: String,
    val nickname: String,
    val profileImgUrl: String?,
    val refreshToken: String,
    val role: String,
    val status: Int,
    val userIdx: Long,
): java.io.Serializable