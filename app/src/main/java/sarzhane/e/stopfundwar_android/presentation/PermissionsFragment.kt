package sarzhane.e.stopfundwar_android.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.HomeScreen
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import javax.inject.Inject

private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

@AndroidEntryPoint
class PermissionsFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onGotCameraPermissionResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)

    }

    private fun onGotCameraPermissionResult(granted: Boolean) {
        if (granted) {
            onCameraPermissionGranted()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context?.packageName, null)
        )
        if (context?.packageManager?.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(context, R.string.permissions_denied_forever, Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(context!!)
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setPositiveButton(R.string.open) { _, _ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun navigateToCamera() {
        lifecycleScope.launchWhenStarted {
            navigator.navigateTo(screen = HomeScreen(),addToBackStack = false)
        }
    }

    private fun onCameraPermissionGranted() {
        navigateToCamera()
        Toast.makeText(context, R.string.camera_permission_granted, Toast.LENGTH_SHORT).show()
    }

    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        fun newInstance(): PermissionsFragment = PermissionsFragment()
    }
}
