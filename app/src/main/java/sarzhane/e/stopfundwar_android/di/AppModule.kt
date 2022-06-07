package sarzhane.e.stopfundwar_android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sarzhane.e.stopfundwar_android.core.navigation.WebNavigator
import sarzhane.e.stopfundwar_android.core.navigation.WebNavigatorImpl


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    fun bindWebNavigator(
        impl: WebNavigatorImpl,
    ): WebNavigator
}