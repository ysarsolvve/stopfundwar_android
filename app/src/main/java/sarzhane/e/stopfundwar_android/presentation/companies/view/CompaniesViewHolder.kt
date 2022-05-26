package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company


class CompaniesViewHolder(
    private val binding: ItemCompanyBinding,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(brand: Company) {
        setBrand(brand.brandName)
        setStatus(brand.statusInfo)
        setStatusColor(brand.statusRate)
    }

    private fun setBrand(brand: String?) {
        binding.tvBrandName.text = brand
    }

    private fun setStatus(brand: String?) {
        binding.tvStatus.text = brand
    }

    private fun setStatusColor(brand: String?) {
        when (brand) {
            "D" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#288818"))
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_green,
                    null
                )
                binding.tvStatus.setPadding(8)
            }
            "C" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#C6811A"))
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_orange,
                    null
                )
                binding.tvStatus.setPadding(8)
            }
            "F" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#CF2424"))
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_red,
                    null
                )
                binding.tvStatus.setPadding(8)
            }
        }
    }
}
