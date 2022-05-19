package sarzhane.e.stopfundwar_android.presentation.camera.view

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company


class BrandViewHolder(
	private val binding: ItemRecognitionBinding,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(brand: Company) {
        setBrand(brand.brandName)
        setDescription(brand.description)
        setStatus(brand.statusInfo)
        setThumbnail(brand.logo)
    }

    private fun setBrand(brand: String?) {
        binding.tvBrandName.text = brand
    }

    private fun setDescription(brand: String?) {
        binding.tvDescription.text = brand
    }

    private fun setStatus(brand: String?) {
        binding.btnStatus.text = brand
    }

    private fun setThumbnail(brand: String?) {
        Picasso.get()
            .load(brand)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.camera)
            .fit()
            .centerCrop()
            .into(binding.ivBrand)
    }
}
