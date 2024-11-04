package com.don.preface.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LibraryResponse(
    val kind: String,
    val items: List<Bookshelf>
)

@Serializable
data class Bookshelf(
    val kind: String,
    val id: Int,
    val selfLink: String,
    val title: String,
    val access: String,
    val updated: String,
    val created: String,
    val volumeCount: Int,
    val volumesLastUpdated: String
)
