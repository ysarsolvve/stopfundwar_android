package sarzhane.e.stopfundwar_android.data.companies

import sarzhane.e.stopfundwar_android.data.companies.local.CompanyEntity


internal fun CompanyModel.toEntity(): CompanyEntity = CompanyEntity(
    id = this.id ?:"",
    brandName = this.brandName,
    logo = this.logo,
    statusInfo = this.statusInfo,
    statusRate = this.statusRate,
    description = this.description
)
