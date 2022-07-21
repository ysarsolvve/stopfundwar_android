package sarzhane.e.stopfundwar_android.presentation.camera.info.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sarzhane.e.stopfundwar_android.R

class InfoItemsAdapter(private val infoItems: List<InfoItem>) :
RecyclerView.Adapter<InfoItemsAdapter.InfoItemViewHolder>(){


    inner class InfoItemViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val imageInfo = view.findViewById<ImageView>(R.id.imageOnboarding)
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)

        fun bind(infoItem: InfoItem){
            imageInfo.setImageResource(infoItem.infoImage)
            textTitle.text = infoItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoItemViewHolder {
        return InfoItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item_container, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InfoItemViewHolder, position: Int) {
        holder.bind(infoItems[position])
    }

    override fun getItemCount(): Int {
        return infoItems.size
    }
}