package sarzhane.e.stopfundwar_android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepository
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepositoryImpl
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesLocalDataSource
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesLocalDataSourceImpl
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesRemoteDataSource
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesRemoteDataSourceImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface CompaniesModule {

    @Binds
    fun bindCompaniesRemoteDataSource(
        impl: CompaniesRemoteDataSourceImpl,
    ): CompaniesRemoteDataSource

    @Binds
    fun bindCompaniesLocalDataSource(
        impl: CompaniesLocalDataSourceImpl,
    ): CompaniesLocalDataSource

    @Binds
    @Singleton
    fun bindCompaniesRepository(
        impl: CompaniesRepositoryImpl,
    ): CompaniesRepository
}
