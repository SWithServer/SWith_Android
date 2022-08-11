package com.example.swith.repository.announce

import com.example.swith.data.AnnounceCreate
import com.example.swith.data.AnnounceList
import com.example.swith.utils.error.RemoteErrorEmitter

class AnnounceRepository(private val announceRemoteDataSource: AnnounceRemoteDataSource) {
    suspend fun getAllAnnounce(errorEmitter: RemoteErrorEmitter, groupIdx: Int): AnnounceList? = announceRemoteDataSource.getAllAnnounce(errorEmitter, groupIdx)
    suspend fun deleteAnnounce(emitter: RemoteErrorEmitter, announcementIdx: Int) = announceRemoteDataSource.deleteAnnounce(emitter, announcementIdx)
    suspend fun createAnnounce(emitter: RemoteErrorEmitter, announceCreate: AnnounceCreate) = announceRemoteDataSource.createAnnounce(emitter, announceCreate)
}