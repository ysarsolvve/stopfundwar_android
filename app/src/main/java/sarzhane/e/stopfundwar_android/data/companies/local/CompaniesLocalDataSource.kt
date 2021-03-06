package sarzhane.e.stopfundwar_android.data.companies.local

import android.util.Log
import javax.inject.Inject

interface CompaniesLocalDataSource {

    suspend fun getByIds(ids: List<String>): List<CompanyEntity>
    suspend fun insertAll(companies: List<CompanyEntity>)
    suspend fun insert(company: CompanyEntity)
    suspend fun deleteAll()
    suspend fun getData(): List<CompanyEntity>
    suspend fun getDataBySearchAndFilter(searchQuery: String, filter: String): List<CompanyEntity>
    suspend fun getDataByFilter(filter: String): List<CompanyEntity>
}

class CompaniesLocalDataSourceImpl @Inject constructor(
    private val companiesDao: CompaniesDao,
) : CompaniesLocalDataSource {

    override suspend fun getByIds(ids: List<String>): List<CompanyEntity> = companiesDao.getById(ids)

    override suspend fun insertAll(companies: List<CompanyEntity>) {
        companiesDao.insertAll(companies)
    }

    override suspend fun insert(company: CompanyEntity) {
        companiesDao.insert(company)
    }

    override suspend fun deleteAll() {
        companiesDao.deleteAll()
    }

    override suspend fun getData(): List<CompanyEntity> {
       return companiesDao.getData()
    }

    override suspend fun getDataBySearchAndFilter(searchQuery: String, filter: String): List<CompanyEntity> {
        val query = searchQuery + "__%"
        val response = if (filter == "All") {
            companiesDao.getDataWithoutFilter(query)
        } else {
            companiesDao.getDataBySearchAndFilter(query, filter)
        }
        Log.d("search","response ${response}")
        return response
    }

    override suspend fun getDataByFilter(filter: String): List<CompanyEntity> {
        return companiesDao.getDataByFilter(filter)
    }
}