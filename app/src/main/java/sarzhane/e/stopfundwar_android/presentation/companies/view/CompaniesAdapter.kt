package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import sarzhane.e.stopfundwar_android.databinding.ItemCompanyBinding
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company


class CompaniesAdapter() : ListAdapter<Company, CompaniesViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompaniesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCompanyBinding.inflate(layoutInflater, parent, false)
        return CompaniesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompaniesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class BrandDiffCallback : DiffUtil.ItemCallback<Company>() {

    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean =
        oldItem == newItem
}
