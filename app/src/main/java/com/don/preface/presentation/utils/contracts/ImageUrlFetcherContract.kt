package com.don.preface.presentation.utils.contracts

import com.don.preface.data.model.ImageLinks

fun interface ImageUrlFetcherContract {
    fun fetchImageUrl(imageLinks: ImageLinks?): String?
}
