package com.example.swith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swith.data.DateTime
import com.example.swith.data.Round
import java.util.*
import kotlin.collections.ArrayList

class RoundViewModel() : ViewModel() {
    private var allData = ArrayList<Round>()
    private var postData = ArrayList<Round>()
    val curCount = 3

    private var pastVisible = false
    private var _currentLiveData = MutableLiveData<Round>()
    private var _roundLiveData = MutableLiveData<ArrayList<Round>>()

    // 캘린더 화면에 관한 것
    private var _calendarLiveData = MutableLiveData<ArrayList<Round>>()


    val currentLiveData : LiveData<Round>
        get() = _currentLiveData

    val roundLiveData : LiveData<ArrayList<Round>>
        get() = _roundLiveData

    val calendarLiveData : LiveData<ArrayList<Round>>
        get () = _calendarLiveData


    init{
        // 임시로 여기다 추가
        // 후에 repository로 받아오는 부분
        val roundData = ArrayList<Round>()
        roundData.add(Round(1, DateTime(2022, 7, 12, 10, 0), DateTime(2022, 7, 12, 11, 0), "영어 1회차 스터디", true, null, 1))
        roundData.add(Round(2, DateTime(2022, 7, 13, 11, 0), DateTime(2022, 7, 12, 12, 0), "영어 2회차 스터디", true, null, 3))
        roundData.add(Round(3, DateTime(2022, 7, 14, 10, 0), DateTime(2022, 7, 14, 11, 0), "영어 3회차 스터디", true, null, 5))
        roundData.add(Round(4, DateTime(2022, 7, 15, 22, 0), DateTime(2022, 7, 15, 23, 0), "영어 4회차 스터디", true, null, 0))
        roundData.add(Round(5, DateTime(2022, 7, 15, 23, 30), DateTime(2022, 7, 16, 0, 0), "영어 5회차 스터디", true, null, 0))
        roundData.add(Round(6, DateTime(2022, 7, 16, 22, 0), DateTime(2022, 7, 16, 23, 0), "영어 6회차 스터디", true, null, 0 ))

        allData.addAll(roundData)
        roundData.forEach { if (it.count >= curCount) postData.add(it)  }
        _roundLiveData.value = postData
    }

    fun addData(round: Round){
        postData.add(round)
        allData.add(round)

        _roundLiveData.value = if (pastVisible) allData else postData
    }

    fun setCurrentData(round: Round){
        _currentLiveData.value = round
    }

    fun setPastData(pastVisible: Boolean){
        this.pastVisible = pastVisible
        _roundLiveData.value = if (pastVisible) allData else postData
    }

    // 해당 날짜에 회차가 있는지 여부 체크
    fun roundDayExists(year: Int, month: Int, day: Int) : Boolean{
        val thisTimeToLong = String.format("%4d%02d%02d", year, month, day).toLong()
        allData.forEach {
            val startTimeToLong = String.format("%4d%02d%02d", it.startTime.year, it.startTime.month, it.startTime.day).toLong()
            val endTimeToLong = String.format("%4d%02d%02d", it.endTime.year, it.endTime.month, it.endTime.day).toLong()

            if (thisTimeToLong in startTimeToLong..endTimeToLong) return true
        }
        return false
    }

    // 캘린더에 보여질 data 설정
    fun setCalendarData(year: Int, month: Int, day: Int){
        val tempList = ArrayList<Round>()
        val thisTimeToLong = String.format("%4d%02d%02d", year, month, day).toLong()
        allData.forEach {
            val startTimeToLong = String.format("%4d%02d%02d", it.startTime.year, it.startTime.month, it.startTime.day).toLong()
            val endTimeToLong = String.format("%4d%02d%02d", it.endTime.year, it.endTime.month, it.endTime.day).toLong()

            if (thisTimeToLong in startTimeToLong..endTimeToLong) tempList.add(it)
        }
        _calendarLiveData.value = tempList
    }

}