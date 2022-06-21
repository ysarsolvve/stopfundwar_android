package sarzhane.e.stopfundwar_android.presentation.camera.view

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionBinding
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionSkeletonBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company
import sarzhane.e.stopfundwar_android.util.dpToPx


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
      binding.tvBrandName.text = brand
    }

    private fun setDescription(brand: String?) {
        if (brand!!.isEmpty()) return

    }

    private fun setStatus(brand: String?) {
        if (brand!!.isEmpty()) return
    }

    private fun setStatusColor(brand: String?) {
        if (brand!!.isEmpty()) {
            binding.tvStatus.background = ResourcesCompat.getDrawable(
                binding.root.resources,
                R.drawable.rounded_corner_grey,
                null
            )
        }
    }

    private fun setThumbnail(brand: String?) {
        if (brand!!.isEmpty()) return
    }
}
