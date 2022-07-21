package sarzhane.e.stopfundwar_android.presentation.camera.view

import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionSkeletonBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company


class SkeletonBrandViewHolder(
    private val binding: ItemRecognitionSkeletonBinding,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(brand: Company) {
        setBrand(brand.brandName)
        setDescription(brand.description)
        setStatus(brand.statusInfo)
        setStatusColor(brand.statusRate)
        setThumbnail(brand.logo)
    }

    private fun setBrand(brand: String?) {
        if (brand!!.isEmpty()) return
    }

    private fun setDescription(brand: String?) {
        if (brand!!.isEmpty()) return

    }

    private fun setStatus(brand: String?) {
        binding.tvStatus.text = brand
    }

    private fun setStatusColor(brand: String?) {

    }

    private fun setThumbnail(brand: String?) {
        if (brand!!.isEmpty()) return
    }
}
