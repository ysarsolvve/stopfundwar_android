package sarzhane.e.stopfundwar_android.core.application

import android.app.Application
import com.zeugmasolutions.localehelper.LocaleAwareApplication
import dagger.hilt.android.HiltAndroidApp
import sarzhane.e.stopfundwar_android.core.BuildConfigProvider
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApp : LocaleAwareApplication() {

    @Inject
    lateinit var buildConfig: BuildConfigProvider

    override fun onCreate() {
        super.onCreate()
        if (buildConfig.debug) {
            Timber.plant(Timber.DebugTree())
        }
    }
}