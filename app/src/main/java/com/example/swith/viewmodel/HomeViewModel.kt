package com.example.swith.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.swith.SwithApplication
import com.example.swith.data.remote.home.HomeRemoteDataSource
import com.example.swith.data.repository.home.HomeRepository
import com.example.swith.domain.entity.GroupList
import com.example.swith.utils.SingleLiveEvent
import com.example.swith.utils.base.BaseViewModel
import com.example.swith.utils.error.ScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel() : BaseViewModel() {
    private val repository: HomeRepository = HomeRepository(HomeRemoteDataSource())
    private var _groupLiveData = SingleLiveEvent<GroupList>()

    val groupLiveData: LiveData<GroupList>
        get() = _groupLiveData

    fun loadData() {
        val userId: Long =
            if (SwithApplication.spfManager.getLoginData() != null) SwithApplication.spfManager.getLoginData()?.userIdx!! else 1
        viewModelScope.launch {
            val res = repository.getAllStudy(this@HomeViewModel, userId)
            withContext(Dispatchers.Main) {
                if (res == null) mutableScreenState.postValue(ScreenState.RENDER)
                res?.let {
                    _groupLiveData.value = it
                    mutableScreenState.postValue(ScreenState.RENDER)
                }
            }
        }
    }


    fun getEmptyOrNull(): Boolean {
        return if (_groupLiveData.value == null) true
        else _groupLiveData.value?.group.isNullOrEmpty()
    }

}