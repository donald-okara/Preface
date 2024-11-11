package com.don.preface.components

import com.don.preface.data.model.ImageLinks

fun interface ImageUrlFetcher {
    fun fetchImageUrl(imageLinks: ImageLinks?): String?
}
