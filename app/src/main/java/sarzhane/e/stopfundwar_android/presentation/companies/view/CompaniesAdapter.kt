package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyBinding
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyHeaderBinding
import sarzhane.e.stopfundwar_android.domain.companies.DataModel


class CompaniesAdapter() : ListAdapter<DataModel, RecyclerView.ViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCompanyBinding.inflate(layoutInflater, parent, false)
        val bindingHeader = ItemCompanyHeaderBinding.inflate(layoutInflater, parent, false)
        return when(viewType){
            R.layout.item_company -> CompaniesViewHolder(binding)
            R.layout.item_company_header -> HeaderViewHolder(bindingHeader)
            else ->  throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CompaniesViewHolder -> holder.bind(getItem(position) as DataModel.Company)
            is HeaderViewHolder -> holder.bind(getItem(position) as DataModel.Header)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is DataModel.Header -> R.layout.item_company_header
            is DataModel.Company -> R.layout.item_company
        }
    }
}

private class BrandDiffCallback : DiffUtil.ItemCallback<DataModel>() {

    override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel): Boolean =
        oldItem is DataModel.Company
                && newItem is DataModel.Company
                && oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean =
        oldItem == newItem
}
