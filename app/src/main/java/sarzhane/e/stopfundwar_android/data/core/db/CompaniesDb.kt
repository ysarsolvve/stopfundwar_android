package sarzhane.e.stopfundwar_android.data.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import sarzhane.e.stopfundwar_android.data.companies.local.CompaniesDao
import sarzhane.e.stopfundwar_android.data.companies.local.CompanyEntity

@Database(
    entities = [
        CompanyEntity::class,
    ],
    version = 1,
    exportSchema = true
)

abstract class CompaniesDb : RoomDatabase() {

    abstract fun companiesDao(): CompaniesDao

    companion object {

        fun create(@ApplicationContext appContext: Context): CompaniesDb =
            Room.databaseBuilder(
                appContext,
                CompaniesDb::class.java,
                "companies_database"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}