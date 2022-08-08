package com.example.swith.repository.announce

import com.example.swith.data.AnnounceList
import com.example.swith.utils.error.RemoteErrorEmitter

class AnnounceRepository(private val announceRemoteDataSource: AnnounceRemoteDataSource) {
    suspend fun getAllAnnounce(errorEmitter: RemoteErrorEmitter, groupIdx: Int): AnnounceList? = announceRemoteDataSource.getAllAnnounce(errorEmitter, groupIdx)
}