package com.example.swith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.swith.data.Announce
import com.example.swith.data.AnnounceList
import com.example.swith.repository.announce.AnnounceRemoteDataSource
import com.example.swith.repository.announce.AnnounceRepository
import com.example.swith.utils.SingleLiveEvent
import com.example.swith.utils.base.BaseViewModel
import com.example.swith.utils.error.ScreenState
import kotlinx.coroutines.launch

class AnnounceViewModel : BaseViewModel() {
    private val repository = AnnounceRepository(AnnounceRemoteDataSource())
    private var _announceLiveData = SingleLiveEvent<AnnounceList>()
    private var _deleteLiveEvent = SingleLiveEvent<Any>()

    val announceLiveData : LiveData<AnnounceList>
        get() = _announceLiveData

    val deleteLiveEvent : LiveData<Any>
        get() = _deleteLiveEvent

    fun loadData(groupIdx: Int){
        viewModelScope.launch {
            val res = repository.getAllAnnounce(this@AnnounceViewModel, groupIdx)
            if (res == null) mutableScreenState.postValue(ScreenState.RENDER) else{
                mutableScreenState.postValue(ScreenState.RENDER)
                _announceLiveData.value = res!!
            }
        }
    }

    fun deleteAnnounce(announceIdx: Int){
        viewModelScope.launch {
            val res = repository.deleteAnnounce(this@AnnounceViewModel, announceIdx)
            res?.let { _deleteLiveEvent.call()
                mutableScreenState.postValue(ScreenState.LOAD)
            }
        }
    }

}