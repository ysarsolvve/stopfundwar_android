package sarzhane.e.stopfundwar_android.data.companies


import android.graphics.Color
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesLocalDataSource
import sarzhane.e.stopfundwar_android.data.companies.local.CompanyEntity
import sarzhane.e.stopfundwar_android.data.companies.remote.CompaniesRemoteDataSource
import sarzhane.e.stopfundwar_android.domain.companies.Company
import java.util.concurrent.atomic.AtomicReference


import javax.inject.Inject

interface CompaniesRepository {

    suspend fun getAllCompanies()

    suspend fun getData(): List<Company>

    suspend fun getDataByFilter(filter: String): List<Company>

    suspend fun getDataByIds(ids: List<String>): List<Company>

    suspend fun getDataBySearchAndFilter(searchQuery: String, filter: String): List<Company>


}

class CompaniesRepositoryImpl @Inject constructor(
    private val companiesRemoteDataSource: CompaniesRemoteDataSource,
    private val companiesLocalDataSource: CompaniesLocalDataSource,
) : CompaniesRepository {

    private val colorMap = AtomicReference<HashMap<String, Int>>()

    override suspend fun getAllCompanies() {
        val companyEntities =
            companiesRemoteDataSource.getCompanies()
                .filterNot { companyModel -> companyModel.brandName.isNullOrBlank() }
                .map { company -> company.toEntity() }
        Log.d("Response", "companyEntities ${companyEntities}")
        companiesLocalDataSource.deleteAll()
        companiesLocalDataSource.insertAll(companyEntities)
}

    override suspend fun getData(): List<Company> {
        return companiesLocalDataSource.getData().map { it.toModel() }
    }

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
