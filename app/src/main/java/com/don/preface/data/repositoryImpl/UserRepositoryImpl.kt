package com.don.preface.data.repositoryImpl

import android.util.Log
import com.don.preface.data.model.LibraryResponse
import com.don.preface.data.repositories.UserRepository
import com.don.preface.network.GoogleBooksApi

class UserRepositoryImpl(
    private val googleBooksApi: GoogleBooksApi,
    private val accessToken: String,
    private val apiKey: String
) : UserRepository {

    override suspend fun fetchUserLibrary(): LibraryResponse? {
        return try {
            googleBooksApi.getUserLibrary(accessToken ="Bearer $accessToken", apiKey = apiKey)
        } catch (e: Exception) {
            Log.e("BooksRepository", "Error fetching library", e)
            null
        }
    }
}