package sarzhane.e.stopfundwar_android.data.companies.local

import javax.inject.Inject

interface CompaniesLocalDataSource {

    suspend fun getById(ids: List<String>): List<CompanyEntity>
    suspend fun insertAll(companies: List<CompanyEntity>)
    suspend fun insert(company: CompanyEntity)
    suspend fun deleteAll()
}

class CompaniesLocalDataSourceImpl @Inject constructor(
    private val companiesDao: CompaniesDao,
) : CompaniesLocalDataSource {

    override suspend fun getById(ids: List<String>): List<CompanyEntity> = companiesDao.getById(ids)

    override suspend fun insertAll(companies: List<CompanyEntity>) {
        companiesDao.insertAll(companies)
    }

    override suspend fun insert(company: CompanyEntity) {
        companiesDao.insert(company)
    }

    override suspend fun deleteAll() {
        companiesDao.deleteAll()
    }
}