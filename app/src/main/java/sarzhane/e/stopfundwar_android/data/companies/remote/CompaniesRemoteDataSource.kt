package sarzhane.e.stopfundwar_android.data.companies.remote


import sarzhane.e.stopfundwar_android.data.companies.CompanyModel
import sarzhane.e.stopfundwar_android.data.companies.toCompanies
import javax.inject.Inject

interface CompaniesRemoteDataSource {

    suspend fun getCompanies(): List<CompanyModel>

}

class CompaniesRemoteDataSourceImpl @Inject constructor(
    private val companiesApi: CompaniesApi,
) : CompaniesRemoteDataSource {
    override suspend fun getCompanies(): List<CompanyModel> =
        companiesApi.getCompanies().map { response -> response?.toCompanies() ?: emptyList() }.flatten()
}
