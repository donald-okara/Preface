# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
-dontwarn ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
-dontwarn ke.don.common_datasource.local.datastore.user_settings.AppSettings
-dontwarn ke.don.common_datasource.local.datastore.user_settings.SettingsDataStoreManager
-dontwarn ke.don.common_datasource.local.di.DatastoreModule_ProvideInternetAvailabilityFactory
-dontwarn ke.don.common_datasource.local.di.DatastoreModule_ProvideProfileDataStoreManagerFactory
-dontwarn ke.don.common_datasource.local.di.DatastoreModule_ProvideSettingsDataStoreManagerFactory
-dontwarn ke.don.common_datasource.local.di.DatastoreModule_ProvideTokenDataStoreManagerFactory
-dontwarn ke.don.common_datasource.local.roomdb.dao.BookshelfDao
-dontwarn ke.don.common_datasource.local.worker.classes.PrefaceWorkerFactory
-dontwarn ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideBookshelfDaoFactory
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideBookshelfDatabaseFactory
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideBookshelfNetworkClassFactory
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideProfileNetworkClassFactory
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideProfileRepositoryFactory
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideSupabaseClientFactory
-dontwarn ke.don.common_datasource.remote.data.di.DatasourceModule_ProvideUserProfileFactory
-dontwarn ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
-dontwarn ke.don.common_datasource.remote.domain.error_handler.InternetAvailability
-dontwarn ke.don.common_datasource.remote.domain.repositories.ProfileRepository
-dontwarn ke.don.shared_components.mbuku_theme.ui.theme.ThemeKt
-dontwarn ke.don.shared_domain.data_models.Profile
-dontwarn ke.don.shared_navigation.NavGraphKt