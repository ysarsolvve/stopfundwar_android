package sarzhane.e.stopfundwar_android.core.navigation

import androidx.fragment.app.Fragment
import sarzhane.e.stopfundwar_android.presentation.PermissionsFragment
import sarzhane.e.stopfundwar_android.presentation.dashboard.view.DashboardFragment
import sarzhane.e.stopfundwar_android.presentation.onboard.OnboardFragment
import sarzhane.e.stopfundwar_android.presentation.splash.view.SplashFragment


interface Screen {

    fun destination(): Fragment

    val tag: String? get() = null
}

class SplashScreen : Screen {

    override fun destination(): Fragment = SplashFragment.newInstance()
}

class OnboardScreen : Screen {

    override fun destination(): Fragment = OnboardFragment.newInstance()
}

class HomeScreen : Screen {

    override fun destination(): Fragment = DashboardFragment.newInstance()
}

class PermissionsScreen : Screen {

    override fun destination(): Fragment = PermissionsFragment.newInstance()
}

