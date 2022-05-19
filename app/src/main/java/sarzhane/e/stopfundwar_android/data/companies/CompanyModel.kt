package sarzhane.e.stopfundwar_android.data.companies

import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesResponse

data class CompanyModel(
    val id: String?,
    val brandName: String?,
    val logo: String?,
    val statusInfo: String?,
    val statusRate: String?,
    val description: String?
)

fun CompaniesResponse.toCompanies(): List<CompanyModel> =
    brands.map { brand -> CompanyModel(
        id = brand?.yoloBrandID,
        brandName = brand?.brandName,
        logo = brand?.logoImageURL,
        statusInfo = status?.statusInfo,
        statusRate = status?.statusRate,
        description = description
    ) }
