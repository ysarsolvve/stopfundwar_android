package sarzhane.e.stopfundwar_android.presentation.onboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.R

class OnboardItemsAdapter(private val onboardItems: List<OnboardItem>) :
RecyclerView.Adapter<OnboardItemsAdapter.OnboardItemViewHolder>(){


    inner class OnboardItemViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val imageOnboard = view.findViewById<ImageView>(R.id.imageOnboard)
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)
        private val textDescription = view.findViewById<TextView>(R.id.textDescription)

        fun bind(onboardItem: OnboardItem){
            imageOnboard.setImageResource(onboardItem.onboardImage)
            textTitle.text = onboardItem.title
            textDescription.text = onboardItem.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardItemViewHolder {
        return OnboardItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item_container, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnboardItemViewHolder, position: Int) {
        holder.bind(onboardItems[position])
    }

    override fun getItemCount(): Int {
        return onboardItems.size
    }
}