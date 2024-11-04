package com.don.preface.data.repositories

import com.don.preface.data.model.LibraryResponse

interface UserRepository {
    suspend fun fetchUserLibrary(): LibraryResponse?
}