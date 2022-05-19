package sarzhane.e.stopfundwar_android.data.companies.remote

import retrofit2.http.GET

/**
 * Movies api of themoviedb.org
 */
interface CompaniesApi {

    @GET("companies.json")
    suspend fun getCompanies(): Response

}