package com.example.swith.data

data class StudyApplicationResponse (
    val isSuccess: Boolean,
    val code: Long,
    val message: String,
    val result: Long
)

data class postApplicationReq(
    val userIdx : Long,
    val applicationContent:String?
)
