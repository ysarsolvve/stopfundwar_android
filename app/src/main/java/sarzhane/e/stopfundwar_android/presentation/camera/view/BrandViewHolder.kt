package sarzhane.e.stopfundwar_android.presentation.camera.view

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company
import sarzhane.e.stopfundwar_android.util.dpToPx


class BrandViewHolder(
    private val binding: ItemRecognitionBinding,
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
        binding.tvDescription.text = brand
    }

    private fun setStatus(brand: String?) {
        binding.tvStatus.text = brand
    }

    private fun setStatusColor(brand: String?) {
        val padding:Number = 8
        val paddingInPx = padding.dpToPx().toInt()
        when (brand) {
            "A","B" -> {
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_green,
                    null
                )
                binding.cvItem.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_green,
                    null
                )
                binding.tvDescription.setTextColor(Color.parseColor("#288818"))
                binding.tvStatus.setPadding(paddingInPx)
            }
            "C" -> {
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_orange,
                    null
                )
                binding.cvItem.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_orange,
                    null
                )
                binding.tvDescription.setTextColor(Color.parseColor("#C6811A"))
                binding.tvStatus.setPadding(paddingInPx)
            }
            "F","D" -> {
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_red,
                    null
                )

                binding.cvItem.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_red,
                    null
                )
                binding.tvDescription.setTextColor(Color.parseColor("#CF2424"))
                binding.tvStatus.setPadding(paddingInPx)
            }
        }
    }

    private fun setThumbnail(brand: String?) {
        if (brand!!.isEmpty()) return
        Picasso.get()
            .load(brand)
            .fit()
            .centerInside()
            .into(binding.ivBrand)
    }

}
