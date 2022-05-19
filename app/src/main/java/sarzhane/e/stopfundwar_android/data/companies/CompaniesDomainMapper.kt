package sarzhane.e.stopfundwar_android.data.companies


import sarzhane.e.stopfundwar_android.data.companies.local.CompanyEntity
import sarzhane.e.stopfundwar_android.domain.companies.Company


internal fun CompanyEntity.toModel(): Company = Company(
    id = this.id,
    brandName = this.brandName,
    logo = this.logo,
    statusInfo = this.statusInfo,
    statusRate = this.statusRate,
    description = this.description
)