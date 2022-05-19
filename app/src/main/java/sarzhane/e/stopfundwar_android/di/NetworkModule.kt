package sarzhane.e.stopfundwar_android.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesApi
import sarzhane.e.stopfundwar_android.data.core.network.CompaniesHttpClient
import sarzhane.e.stopfundwar_android.data.core.network.CompaniesHttpClientImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {

    @Binds
    @Singleton
    fun bindMovieDbClient(
        impl: CompaniesHttpClientImpl,
    ): CompaniesHttpClient
}

@Module
@InstallIn(SingletonComponent::class)
object ApiWrapperModule {

    @Provides
    @Singleton
    fun provideMoviesApi(client: CompaniesHttpClient): CompaniesApi = client.companiesApi
}
