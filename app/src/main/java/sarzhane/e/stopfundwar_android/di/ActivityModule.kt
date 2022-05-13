package sarzhane.e.stopfundwar_android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.NavigatorImpl


@Module
@InstallIn(ActivityComponent::class)
interface ActivityModule {

    @Binds
    fun provideNavigator(
        impl: NavigatorImpl,
    ): Navigator
}