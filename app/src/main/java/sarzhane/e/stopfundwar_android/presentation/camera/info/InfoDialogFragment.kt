package sarzhane.e.stopfundwar_android.presentation.camera.info

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.FragmentInfoPagerBinding
import sarzhane.e.stopfundwar_android.util.setFullScreen
import sarzhane.e.stopfundwar_android.util.setWidthPercent


class InfoDialogFragment : DialogFragment(R.layout.fragment_info_pager) {
    private lateinit var infoItemsAdapter: InfoItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout
    
    private val binding by viewBinding(FragmentInfoPagerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInfoItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setInfoItems(){
        infoItemsAdapter = InfoItemsAdapter(
            listOf(
                InfoItem(
                    infoImage = R.drawable.info_first,
                    title = getString(R.string.camera_dialog_first_title)
                ),
                InfoItem(
                    infoImage = R.drawable.info_second,
                    title = getString(R.string.camera_dialog_second_title),
                ),
                InfoItem(
                    infoImage = R.drawable.info_third,
                    title = getString(R.string.camera_dialog_third_title),
                ),
                InfoItem(
                    infoImage = R.drawable.info_fourth,
                    title = getString(R.string.camera_dialog_fourth_title),
                ),
                InfoItem(
                    infoImage = R.drawable.info_fifth,
                    title = getString(R.string.camera_dialog_fifth_title),
                ),
                InfoItem(
                    infoImage = R.drawable.info_six,
                    title = getString(R.string.camera_dialog_six_title),
                ),

            )
        )
        val infoViewPager = binding.viewPager
        infoViewPager.adapter = infoItemsAdapter
        infoViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        (infoViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
        binding.btnCloseInfo.setOnClickListener { dismiss() }
    }

    private fun setupIndicators(){
        indicatorsContainer = binding.indicatorsContainer
        val indicators = arrayOfNulls<ImageView>(infoItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices){
            indicators[i] = ImageView(requireActivity())
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int){
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount){
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.indicator_active_background
                    )
                )
            }else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.indicator_inactive_background
                    )
                )
            }
        }

    }

    companion object {
        fun newInstance(): InfoDialogFragment = InfoDialogFragment()

        @JvmStatic
        val TAG: String = InfoDialogFragment::class.java.simpleName
    }
}