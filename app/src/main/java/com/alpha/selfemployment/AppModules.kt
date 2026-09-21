package com.alpha.selfemployment

import com.alpha.selfemployment.Startup.di.viewModels.AuthApiViewModel
import com.alpha.selfemployment.Startup.di.viewModels.CredentialViewModel
import com.alpha.selfemployment.Startup.domain.repository.AuthRepository
import com.alpha.selfemployment.Startup.network.api.AuthInterface
import com.alpha.selfemployment.Views.CommonView.data.network.CommonViewApi
import com.alpha.selfemployment.Views.CommonView.di.CommonViewModel
import com.alpha.selfemployment.Views.CommonView.domain.CommonRepository
import com.alpha.selfemployment.Views.Explore.data.network.ExploreAPI
import com.alpha.selfemployment.Views.Explore.di.ExploreAPIViewModel
import com.alpha.selfemployment.Views.Explore.di.ExploreUIViewModel
import com.alpha.selfemployment.Views.Explore.domain.repository.ExploreRepository
import com.alpha.selfemployment.Views.Home.Videos.data.remote.HomeAPI
import com.alpha.selfemployment.Views.Home.Videos.di.HomeAPIViewModel
import com.alpha.selfemployment.Views.Home.Videos.domain.repository.HomeRepository
import com.alpha.selfemployment.Views.Message.BackendSupport.data.remote.ChatAPI
import com.alpha.selfemployment.Views.Message.BackendSupport.di.ChatBackEndViewModel
import com.alpha.selfemployment.Views.Message.BackendSupport.domain.repository.ChatBERepository
import com.alpha.selfemployment.Views.Message.FirebaseChat.di.ConversationViewModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.di.PostChatUsersViewModel
import com.alpha.selfemployment.Views.Message.FirebaseChat.domain.repository.ChatRepository
import com.alpha.selfemployment.Views.Notitifcation.di.viewModels.NotificationUIViewModels
import com.alpha.selfemployment.Views.PostUpload.data.network.PostUploadAPI
import com.alpha.selfemployment.Views.PostUpload.domain.repository.PostUploadRepository
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadAPIViewModel
import com.alpha.selfemployment.Views.PostUpload.viewModels.PostUploadUIViewModel
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.data.FFInterface
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFApiViewModel
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.di.FFUiViewModel
import com.alpha.selfemployment.Views.ProfileModule.Following.Followers.domain.repository.FFRepository
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.data.network.api.ProfileApi
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.di.ProfileAPIViewModel
import com.alpha.selfemployment.Views.ProfileModule.MyProfile.domain.repository.ProfileRepository
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsApiViewModel
import com.alpha.selfemployment.Views.ProfileModule.Settings.di.viewModels.SettingsUIViewModel
import com.alpha.selfemployment.Views.ProfileModule.Settings.domain.repository.SettingsRepository
import com.alpha.selfemployment.Views.ProfileModule.Settings.network.api.SettingsInterface
import com.alpha.selfemployment.Views.SharedRepository
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()

        val request = chain.request().newBuilder().apply {
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        return chain.proceed(request)
    }
}
val networkModule = module {

    single {
        // OkHttpClient
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, SecureRandom())
        }

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(AuthInterceptor {

                    AppPreferences(androidContext()).getUserToken()
                }
            )
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    single {
        // Retrofit
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
}



val sharedPreference = module {

    single {
        AppPreferences(androidContext())
    }
}


val uiViewModels = module {
    viewModel { CredentialViewModel() }
    viewModel { NotificationUIViewModels() }
    viewModel { FFUiViewModel() }
    single { CommonViewModel(get() , get()) }
    single { SettingsUIViewModel() }
    viewModel { PostUploadUIViewModel() }
    viewModel { ExploreUIViewModel() }
}


val apiInterface  = module {

    single<AuthInterface> { get<Retrofit>().create(AuthInterface::class.java) }
    single<SettingsInterface> { get<Retrofit>().create(SettingsInterface::class.java) }
    single<FFInterface> { get<Retrofit>().create(FFInterface::class.java) }
    single<PostUploadAPI> { get<Retrofit>().create(PostUploadAPI::class.java) }
    single<HomeAPI> { get<Retrofit>().create(HomeAPI::class.java) }
    single<ProfileApi> { get<Retrofit>().create(ProfileApi::class.java) }
    single<CommonViewApi> { get<Retrofit>().create(CommonViewApi::class.java) }
    single<ExploreAPI> { get<Retrofit>().create(ExploreAPI::class.java) }
    single<ChatAPI> { get<Retrofit>().create(ChatAPI::class.java) }
}

val apiRepository  = module {

    single { AuthRepository(get()) }
    single { SettingsRepository(get()) }
    single { FFRepository(get()) }
    single { PostUploadRepository(get()) }
    single { HomeRepository(get()) }
    single { ProfileRepository(get()) }
    single { ExploreRepository(get()) }
    single { ChatBERepository(get()) }
}

val apiViewModels = module {
    viewModel { AuthApiViewModel(get() , get()) }
    viewModel { SettingsApiViewModel(get(), get(),get()) }
    viewModel { FFApiViewModel(get()) }
    viewModel { PostUploadAPIViewModel(get()) }
    viewModel { HomeAPIViewModel(get() ,get()) }
    viewModel { ExploreAPIViewModel(get(), get()) }
    viewModel { ChatBackEndViewModel(get(), get()) }
    factory { ProfileAPIViewModel(get(), get()) }
}


val commonModules = module {
    single { SharedRepository() }
    single { CommonRepository(get()) }
}



val firebaseChatModule = module {

    // Firebase
    single<FirebaseDatabase> { FirebaseDatabase.getInstance() }

    // Repositories
    single { ChatRepository(get()) }
//    single { UserRepository(get()) }

    // ViewModels
    viewModel { PostChatUsersViewModel(get() , get()) }
    viewModel { ConversationViewModel(get()) }
}