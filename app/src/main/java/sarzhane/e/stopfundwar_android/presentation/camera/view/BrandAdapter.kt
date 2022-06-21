package sarzhane.e.stopfundwar_android.presentation.camera.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionBinding
import sarzhane.e.stopfundwar_android.databinding.ItemRecognitionSkeletonBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company
import sarzhane.e.stopfundwar_android.domain.companies.DataModel
import sarzhane.e.stopfundwar_android.presentation.companies.view.CompaniesViewHolder
import sarzhane.e.stopfundwar_android.presentation.companies.view.HeaderViewHolder


class BrandAdapter() : ListAdapter<Company, RecyclerView.ViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecognitionBinding.inflate(layoutInflater, parent, false)
        val skeletonBinding = ItemRecognitionSkeletonBinding.inflate(layoutInflater, parent, false)
        return when(viewType){
            R.layout.item_recognition_skeleton -> SkeletonBrandViewHolder(skeletonBinding)
            R.layout.item_recognition -> BrandViewHolder(binding)
            else ->  throw IllegalStateException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BrandViewHolder -> holder.bind(getItem(position))
            is SkeletonBrandViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].id.isNullOrEmpty()) {
            true -> R.layout.item_recognition_skeleton
            false -> R.layout.item_recognition
        }
    }
}

private class BrandDiffCallback : DiffUtil.ItemCallback<Company>() {

    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean =
        oldItem == newItem
}
