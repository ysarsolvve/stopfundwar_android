package sarzhane.e.stopfundwar_android.presentation.splash.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.PermissionsScreen
import sarzhane.e.stopfundwar_android.core.navigation.ViewPagerScreen
import sarzhane.e.stopfundwar_android.presentation.splash.viewmodel.SplashViewModel
import sarzhane.e.stopfundwar_android.util.observe

import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    @Inject
    lateinit var navigator: Navigator
    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCompanies()

        Handler().postDelayed({
            if (onBoardingFinished()) {
                navigator.navigateTo(
                    screen = PermissionsScreen(),
                    addToBackStack = false,)
            } else {
                navigator.navigateTo(
                    screen = ViewPagerScreen(),
                    addToBackStack = false,)
            }
        }, 2000)

    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    companion object {

        fun newInstance(): SplashFragment = SplashFragment()
    }
}
