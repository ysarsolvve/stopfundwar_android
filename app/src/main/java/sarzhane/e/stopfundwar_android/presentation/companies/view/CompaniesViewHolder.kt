package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.squareup.picasso.Picasso
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyBinding
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyCollapseBinding
import sarzhane.e.stopfundwar_android.domain.companies.DataModel
import sarzhane.e.stopfundwar_android.util.dpToPx
import sarzhane.e.stopfundwar_android.util.toGone
import sarzhane.e.stopfundwar_android.util.toVisible


class CompaniesViewHolder(
    private val binding: ItemCompanyBinding,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(brand: DataModel.Company) {
        var isViewExpanded = true
        itemView.setOnClickListener {
            val smallItemConstraint = ConstraintSet()
            smallItemConstraint.clone(itemView.context, R.layout.item_company)
            val largeItemConstraint = ConstraintSet()
            largeItemConstraint.clone(itemView.context, R.layout.item_company_collapse)
            val largeItemWithAlertConstraint = ConstraintSet()
            largeItemWithAlertConstraint.clone(itemView.context, R.layout.item_company_collapse_alert)

            if (!isViewExpanded) {
                val typeface = ResourcesCompat.getFont(itemView.context,R.font.raleway400)
                binding.tvBrandName.typeface = typeface
                binding.ivChevron.setImageResource(R.drawable.chevron_down)
            } else {
                val typeface = ResourcesCompat.getFont(itemView.context,R.font.raleway700)
                binding.tvBrandName.typeface = typeface
                binding.ivChevron.setImageResource(R.drawable.chevron_up)
        }

            val constraintToApply =
                if (isViewExpanded) {
                    if (brand.statusRate == "F" || brand.statusRate == "D") {
                        largeItemWithAlertConstraint
                    } else largeItemConstraint
                } else smallItemConstraint

            animateItemView(constraintToApply, binding.brandConstrainLayout)

            isViewExpanded = !isViewExpanded
        }

        setBrand(brand.brandName)
        setDescription(brand.description)
        setStatus(brand.statusInfo)
        setStatusColor(brand.statusRate)
        setThumbnail(brand.logo)
    }

    private fun animateItemView(
        constraintToApply: ConstraintSet,
        constraintLayout: ConstraintLayout
    ) {
        TransitionManager.beginDelayedTransition(constraintLayout)
        constraintToApply.applyTo(constraintLayout)
    }

    private fun setBrand(brand: String?) {
        binding.tvBrandName.text = brand
    }

    private fun setStatus(brand: String?) {
        binding.tvStatus.text = brand
        binding.tvStatus.setPadding(16)
    }
    private fun setDescription(brand: String?) {
        binding.tvDescription.text = brand
    }
    private fun setThumbnail(brand: String?) {
        Picasso.get()
            .load(brand)
            .placeholder( R.drawable.progress_animation )
            .fit()
            .centerInside()
            .into(binding.ivBrand)
    }

    private fun setStatusColor(brand: String?) {
        val padding:Number = 8
        val paddingInPx = padding.dpToPx().toInt()
        when (brand) {
            "A","B" -> {
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_green,
                    null
                )
                binding.tvDescription.setTextColor(Color.parseColor("#288818"))
                binding.tvStatus.setPadding(paddingInPx)
            }
            "C" -> {
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_orange,
                    null
                )
                binding.tvDescription.setTextColor(Color.parseColor("#C6811A"))
                binding.tvStatus.setPadding(paddingInPx)
            }
            "F","D" -> {
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.tvStatus.background = ResourcesCompat.getDrawable(
                    binding.root.resources,
                    R.drawable.rounded_corner_red,
                    null
                )
                binding.tvDescription.setTextColor(Color.parseColor("#CF2424"))
                binding.tvStatus.setPadding(paddingInPx)
            }
        }
    }
}
