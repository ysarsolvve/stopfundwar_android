package sarzhane.e.stopfundwar_android.data.companies.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DB class of detailed Movie stored in room
 */
@Entity(tableName = CompanyEntity.TABLE_NAME)
data class CompanyEntity(

    @PrimaryKey
    @ColumnInfo(name = COMPANY_ID)
    val id: String,

    @ColumnInfo(name = BRAND_NAME)
    val brandName: String?,

    @ColumnInfo(name = LOGO)
    val logo: String?,

    @ColumnInfo(name = STATUS_INFO)
    val statusInfo: String?,

    @ColumnInfo(name = STATUS_RATE)
    val statusRate: String?,

    @ColumnInfo(name = DESCRIPTION)
    val description: String?,
) {

    companion object {

        const val TABLE_NAME = "companies"

        const val COMPANY_ID = "company_id"
        const val BRAND_NAME = "brandName"
        const val LOGO = "logo"
        const val STATUS_INFO = "statusInfo"
        const val STATUS_RATE = "statusRate"
        const val DESCRIPTION = "description"

    }
}