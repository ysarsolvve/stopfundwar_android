package sarzhane.e.stopfundwar_android.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Class responsible for the browser navigation
 */
interface WebNavigator {

    fun navigateTo(url: String)
}

class WebNavigatorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    ) : WebNavigator {

    /**
     * Open a browser for a given url
     */
    override fun navigateTo(url: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        browserIntent.flags = browserIntent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
        return try {
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }
}
