package com.example.swith.data

import java.time.LocalDate
import java.util.*


// 스터디 개설할때 만들어지는 스터디 data들
data class StudyGroup(
    var adminIndx:Long,
    var groupImgUrl:String,
    var title:String,
    var meet:Int,
    var frequency:Int,
    var periods:String,
    var online:Int,
    var regionIdx1:Long,
    var regionIdx2:Long,
    var interest:Int,
    var topic:String,
    var memberLimit:Int,
    var applicationMethod:Int,

    var recruitmentEndDate: LocalDate,
    var groupStart: LocalDate,
    var groupEnd:LocalDate,

    var attendanceValidTime:Int,
    var groupContent:String
)
