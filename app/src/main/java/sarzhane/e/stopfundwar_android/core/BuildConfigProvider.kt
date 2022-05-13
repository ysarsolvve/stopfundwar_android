package sarzhane.e.stopfundwar_android.core

data class BuildConfigProvider(
    val debug: Boolean,
    val appId: String,
    val buildType: String,
    val versionCode: Int,
    val versionName: String,
)