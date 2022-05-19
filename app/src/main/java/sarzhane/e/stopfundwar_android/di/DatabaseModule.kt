package sarzhane.e.stopfundwar_android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesDao
import sarzhane.e.stopfundwar_android.data.core.db.CompaniesDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCompaniesDb(
        @ApplicationContext appContext: Context
    ): CompaniesDb = CompaniesDb.create(appContext)

    @Provides
    fun provideCompaniesDao(moviesDb: CompaniesDb): CompaniesDao = moviesDb.companiesDao()

}