package sarzhane.e.stopfundwar_android.data.companies.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface CompaniesDao {

    @Query("""
        SELECT *
        FROM ${CompanyEntity.TABLE_NAME}
        WHERE ${CompanyEntity.COMPANY_ID}
        IN (:ids)
        """)
    suspend fun getById(ids: List<String>): List<CompanyEntity>

    @Query("""
        SELECT *
        FROM ${CompanyEntity.TABLE_NAME}
        WHERE ${CompanyEntity.BRAND_NAME} IS NOT NULL
        ORDER BY ${CompanyEntity.BRAND_NAME} ASC
        """)
    suspend fun getData(): List<CompanyEntity>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(companies: List<CompanyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: CompanyEntity)

    @Transaction
    @Query("DELETE FROM ${CompanyEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("""
        SELECT *
        FROM ${CompanyEntity.TABLE_NAME}
        WHERE ${CompanyEntity.BRAND_NAME}
        LIKE (:searchQuery)
        AND ${CompanyEntity.STATUS_INFO}
        LIKE (:filter)
        ORDER BY ${CompanyEntity.BRAND_NAME} ASC
        """)
    suspend fun getCompanies(searchQuery: String, filter: String): List<CompanyEntity>

    @Query("""
        SELECT *
        FROM ${CompanyEntity.TABLE_NAME}
        WHERE ${CompanyEntity.BRAND_NAME}
        LIKE :searchQuery
        ORDER BY ${CompanyEntity.BRAND_NAME} ASC
        """)
    suspend fun getCompaniesWithoutFilter(searchQuery: String): List<CompanyEntity>

    @Query("""
        SELECT *
        FROM ${CompanyEntity.TABLE_NAME}
        WHERE ${CompanyEntity.STATUS_INFO}
        LIKE :filter
        ORDER BY ${CompanyEntity.BRAND_NAME} ASC
        """)
    suspend fun getDataByFilter(filter: String): List<CompanyEntity>
}