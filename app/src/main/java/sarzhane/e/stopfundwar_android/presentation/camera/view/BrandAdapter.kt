package sarzhane.e.stopfundwar_android.presentation.camera.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company


class BrandAdapter() : ListAdapter<Company, BrandViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecognitionBinding.inflate(layoutInflater, parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class BrandDiffCallback : DiffUtil.ItemCallback<Company>() {

    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean =
        oldItem == newItem
}
