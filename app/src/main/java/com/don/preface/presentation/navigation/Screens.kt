package com.don.preface.presentation.navigation

sealed class Screens(
    var route: String
) {
    data object Splash: Screens("splash")
    data object Search: Screens("search")
    data object BookDetails: Screens("book_details/{volumeId}"){
        const val volumeIdNavigationArgument = "volumeId"
    }
}
