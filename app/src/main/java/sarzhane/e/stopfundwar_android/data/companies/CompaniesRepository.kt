package sarzhane.e.stopfundwar_android.data.companies


import android.util.Log
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesLocalDataSource
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesRemoteDataSource
import sarzhane.e.stopfundwar_android.domain.companies.Company


import javax.inject.Inject

interface CompaniesRepository {

    suspend fun getAllCompanies()

    suspend fun getData(): List<Company>

    fun getColorMap(): Map<Int, Int>

    suspend fun getDataByFilter(filter: String): List<Company>

    suspend fun getDataByIds(ids: List<String>): List<Company>

    suspend fun getDataBySearchAndFilter(searchQuery: String, filter: String): List<Company>


}

class CompaniesRepositoryImpl @Inject constructor(
    private val companiesRemoteDataSource: CompaniesRemoteDataSource,
    private val companiesLocalDataSource: CompaniesLocalDataSource,
) : CompaniesRepository {

    private val map = mutableMapOf<Int, Int>()

    override suspend fun getAllCompanies() {
        val companyEntities =
            companiesRemoteDataSource.getCompanies()
                .filterNot { companyModel -> companyModel.brandName.isNullOrBlank() }
                .map { company -> company.toEntity() }
        Log.d("Response", "companyEntities ${companyEntities}")
        companiesLocalDataSource.deleteAll()
        companiesLocalDataSource.insertAll(companyEntities)
        val companies = companiesLocalDataSource.getData().map { it.toModel() }
        for (company in companies) {
            when (company.statusRate) {
                "A", "B" -> {
                    map[company.id!!.toInt()] = -16711936
                }
                "C" -> {
                    map[company.id!!.toInt()] = -3768038
                }
                "F", "D" -> {
                    map[company.id!!.toInt()] = -65536
                }
            }
        }
    }

    override suspend fun getData(): List<Company> {
        return companiesLocalDataSource.getData().map { it.toModel() }
    }

    override fun getColorMap(): Map<Int, Int> = map

    override suspend fun getDataByFilter(filter: String): List<Company> {
        return companiesLocalDataSource.getDataByFilter(filter).map { it.toModel() }
    }

    override suspend fun getDataByIds(ids: List<String>): List<Company> {
        return companiesLocalDataSource.getByIds(ids).map { it.toModel() }
    }

    override suspend fun getDataBySearchAndFilter(
        searchQuery: String,
        filter: String
    ): List<Company> {
        return companiesLocalDataSource.getDataBySearchAndFilter(searchQuery, filter)
            .map { it.toModel() }
    }
}
