package sarzhane.e.stopfundwar_android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sarzhane.e.stopfundwar_android.BuildConfig
import sarzhane.e.stopfundwar_android.core.BuildConfigProvider
import sarzhane.e.stopfundwar_android.core.UrlProvider

@Module
@InstallIn(SingletonComponent::class)
object ConfigsModule {

    @Provides
    fun provideBuildConfigProvider(): BuildConfigProvider =
        BuildConfigProvider(
            debug = BuildConfig.DEBUG,
            appId = BuildConfig.APPLICATION_ID,
            buildType = BuildConfig.BUILD_TYPE,
            versionCode = BuildConfig.VERSION_CODE,
            versionName = BuildConfig.VERSION_NAME
        )

    @Provides
    fun provideUrlProvider(): UrlProvider =
        UrlProvider(
            baseUrl = BuildConfig.BASE_URL,
        )
}