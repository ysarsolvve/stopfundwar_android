package sarzhane.e.stopfundwar_android.data.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sarzhane.e.stopfundwar_android.core.UrlProvider
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesApi
import javax.inject.Inject

interface CompaniesHttpClient {

    val companiesApi: CompaniesApi
}

class CompaniesHttpClientImpl @Inject constructor(
    urlProvider: UrlProvider,
) : CompaniesHttpClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(urlProvider.baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override val companiesApi: CompaniesApi by lazy(LazyThreadSafetyMode.NONE) { retrofit.create(CompaniesApi::class.java) }
}