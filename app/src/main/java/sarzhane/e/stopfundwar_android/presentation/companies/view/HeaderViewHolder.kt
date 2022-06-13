package sarzhane.e.stopfundwar_android.presentation.companies.view

import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyHeaderBinding
import sarzhane.e.stopfundwar_android.domain.companies.DataModel


class HeaderViewHolder(
    private val binding: ItemCompanyHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(title: DataModel.Header) {
        setBrand(title.title)
    }


    private fun setBrand(title: String?) {
        binding.tvBrandName.text = title
    }

}
