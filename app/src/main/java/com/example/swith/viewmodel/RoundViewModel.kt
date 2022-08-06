package com.example.swith.viewmodel

import androidx.lifecycle.*
import com.example.swith.data.DateTime
import com.example.swith.data.GetSessionRes
import com.example.swith.data.Round
import com.example.swith.repository.round.RoundRemoteDataSource
import com.example.swith.repository.round.RoundRepository
import com.example.swith.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

class RoundViewModel() : ViewModel() {
    private val repository = RoundRepository(RoundRemoteDataSource())
    private var allData = ArrayList<GetSessionRes>()
    private var postData = ArrayList<GetSessionRes>()

    var groupIdx = 0
    private var pastVisible = false
    private var _currentLiveData = MutableLiveData<GetSessionRes>()
    private var _roundLiveData = SingleLiveEvent<Round>()

    // 캘린더 화면에 관한 것
    private var _calendarLiveData = MutableLiveData<Round>()

    val currentLiveData : LiveData<GetSessionRes>
        get() = _currentLiveData

    val roundLiveData : LiveData<Round>
        get() = _roundLiveData

    val calendarLiveData : LiveData<Round>
        get () = _calendarLiveData


    fun loadData(){
        // val userIdx = SharedPrefManager().getLoginData()?.userIdx
        val userIdx = 1
        viewModelScope.launch {
            val res = repository.getAllRound(userIdx, groupIdx)
            withContext(Dispatchers.Main) {
                res?.let {
                    postData.clear()
                    allData.clear()
                    it.getSessionResList.forEach { s ->
                        if (compareWithNow(s)) postData.add(s)
                        allData.add(s)
                    }
                    _roundLiveData.value = it.apply { getSessionResList = postData }
                }
            }
        }
    }

    fun setCurrentData(round: GetSessionRes){
        _currentLiveData.value = round
    }

    fun setPastData(pastVisible: Boolean){
        this.pastVisible = pastVisible
        _roundLiveData.value = _roundLiveData.value?.apply { getSessionResList = if(pastVisible) allData else postData }
    }

    // 현재시간과 각 세션 시간 비교
    private fun compareWithNow(session: GetSessionRes) : Boolean{
        with(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))){
            val nowTimeToLong = String.format("%4d%02d%02d%02d%02d", year, monthValue, dayOfMonth, hour, minute).toLong()
            val sessionTimeToLong = String.format("%4d%02d%02d%02d%02d", session.sessionStart[0], session.sessionStart[1], session.sessionStart[2], session.sessionStart[3], session.sessionStart[4]).toLong()
            if (sessionTimeToLong < nowTimeToLong) return false
        }
        return true
    }

    // 해당 날짜에 회차가 있는지 여부 체크
//    fun roundDayExists(year: Int, month: Int, day: Int) : Boolean{
//        val thisTimeToLong = String.format("%4d%02d%02d", year, month, day).toLong()
//        allData.forEach {
//            val startTimeToLong = String.format("%4d%02d%02d", it.startTime.year, it.startTime.month, it.startTime.day).toLong()
//            val endTimeToLong = String.format("%4d%02d%02d", it.endTime.year, it.endTime.month, it.endTime.day).toLong()
//
//            if (thisTimeToLong in startTimeToLong..endTimeToLong) return true
//        }
//        return false
//    }
//
//    // 캘린더에 보여질 data 설정
//    fun setCalendarData(year: Int, month: Int, day: Int){
//        val tempList = ArrayList<Round>()
//        val thisTimeToLong = String.format("%4d%02d%02d", year, month, day).toLong()
//        allData.forEach {
//            val startTimeToLong = String.format("%4d%02d%02d", it.startTime.year, it.startTime.month, it.startTime.day).toLong()
//            val endTimeToLong = String.format("%4d%02d%02d", it.endTime.year, it.endTime.month, it.endTime.day).toLong()
//
//            if (thisTimeToLong in startTimeToLong..endTimeToLong) tempList.add(it)
//        }
//        _calendarLiveData.value = tempList
//    }

}