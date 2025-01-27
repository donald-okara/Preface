package ke.don.shared_domain.values

sealed class Screens(
    var route: String
) {
    data object Splash: Screens("splash")
    data object SignIn: Screens("sign_in")
    data object Search: Screens("search")
    data object BookDetails: Screens("book_details/{volumeId}"){
        const val volumeIdNavigationArgument = "volumeId"
    }
}
