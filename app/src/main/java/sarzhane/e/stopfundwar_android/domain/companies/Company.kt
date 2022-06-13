package sarzhane.e.stopfundwar_android.domain.companies

/**
 * Business class of detailed Movies
 */
data class Company(
    val id: String? = "",
    var brandName: String? = "",
    val logo: String? = "",
    val statusInfo: String? = "",
    val statusRate: String? = "",
    val description: String? = ""
)

sealed class DataModel {
    data class Header(
        val title: String
    ) : DataModel()

    data class Company(
        val id: String? = "",
        var brandName: String? = "",
        val logo: String? = "",
        val statusInfo: String? = "",
        val statusRate: String? = "",
        val description: String? = ""
    ) : DataModel()
}
