package sarzhane.e.stopfundwar_android.data.companies


import android.util.Log
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesLocalDataSource
import sarzhane.e.stopfundwar_android.data.companies.local.CompanyEntity
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesRemoteDataSource
import sarzhane.e.stopfundwar_android.domain.companies.Company

import timber.log.Timber


import javax.inject.Inject

interface CompaniesRepository {

    suspend fun getAllCompanies()

    suspend fun getData(): List<Company>

    suspend fun getCompaniesByIds(ids: List<String>): List<Company>

    suspend fun getCompanies(searchQuery: String, filter: String): List<Company>


}

class CompaniesRepositoryImpl @Inject constructor(
    private val companiesRemoteDataSource: CompaniesRemoteDataSource,
    private val companiesLocalDataSource: CompaniesLocalDataSource,
) : CompaniesRepository {

    override suspend fun getAllCompanies() {
        val companyEntities =
            companiesRemoteDataSource.getCompanies().map { company -> company.toEntity() }
        Log.d("Response", "companyEntities ${companyEntities}")
        companiesLocalDataSource.deleteAll()
        companiesLocalDataSource.insertAll(companyEntities)
}

    override suspend fun getData(): List<Company> {
        return companiesLocalDataSource.getData().map { it.toModel() }
    }

    override suspend fun getCompaniesByIds(ids: List<String>): List<Company> {
       return companiesLocalDataSource.getById(ids).map { it.toModel() }
    }

    override suspend fun getCompanies(searchQuery: String, filter: String): List<Company> {
        return companiesLocalDataSource.getCompanies(searchQuery, filter).map { it.toModel() }
    }
}
