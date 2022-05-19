package sarzhane.e.stopfundwar_android.data.companies.remote

import com.google.gson.annotations.SerializedName


typealias Response = List<CompaniesResponse?>

data class CompaniesResponse(

	@SerializedName("brands")
	val brands: List<BrandDto?>,

	@SerializedName("class")
	val status: StatusDto?,

	@SerializedName("description")
	val description: String? = "",

)

data class BrandDto(
	@SerializedName("brand_name")
	val brandName: String? = "",

	@SerializedName("logo_image_url")
	val logoImageURL: String? = "",

	@SerializedName("yolo_brand_id")
	val yoloBrandID: String? = "",
)

data class StatusDto(
	@SerializedName("status_info")
	val statusInfo: String? = "",

	@SerializedName("status_name")
	val statusRate: String? = "",
)
