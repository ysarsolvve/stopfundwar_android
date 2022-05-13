package sarzhane.e.stopfundwar_android.core.navigation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import dagger.hilt.android.qualifiers.ActivityContext
import sarzhane.e.stopfundwar_android.R
import timber.log.Timber
import javax.inject.Inject

interface Navigator {

    fun navigateTo(
        screen: Screen,
        addToBackStack: Boolean = true,
    )

    fun backTo(tag: String)

    fun back()
}

class NavigatorImpl @Inject constructor(
    @ActivityContext private val context: Context,
) : Navigator {

    override fun navigateTo(screen: Screen, addToBackStack: Boolean) {
        Timber.d("Navigate to ${screen::class.simpleName}")
        getFragmentManager().commit(allowStateLoss = true) {
            replace(R.id.container, screen.destination(), screen.tag)
            if (addToBackStack) {
                addToBackStack(screen.tag)
            }
        }
    }

    override fun backTo(tag: String) {
        Timber.d("Navigate Back to $tag")
        getFragmentManager().popBackStack(tag, 0)
    }

    override fun back() {
        getFragmentManager().popBackStackImmediate()
    }

    private fun getFragmentManager() = (context as AppCompatActivity).supportFragmentManager
}
