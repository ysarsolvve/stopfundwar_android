package sarzhane.e.stopfundwar_android.presentation.camera.info.camera

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
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.databinding.FragmentInfoPagerBinding
import sarzhane.e.stopfundwar_android.util.setFullScreen
import javax.inject.Inject

@AndroidEntryPoint
class InfoDialogFragment : DialogFragment(R.layout.fragment_info_pager) {
    private lateinit var infoItemsAdapter: InfoItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentInfoPagerBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        setInfoItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setInfoItems(){
        infoItemsAdapter = InfoItemsAdapter(
            listOf(
                InfoItem(
                    infoImage = R.drawable.info_first,
                    title = "Choose the item you are interested in",
                ),
                InfoItem(
                    infoImage = R.drawable.info_second,
                    title = "Point the camera at the logo",
                ),
                InfoItem(
                    infoImage = R.drawable.info_third,
                    title = "You will see a color indicator on the logo",
                ),
                InfoItem(
                    infoImage = R.drawable.info_fourth,
                    title = "You will see a color indicator on the logo",
                ),
                InfoItem(
                    infoImage = R.drawable.info_fifth,
                    title = "Different colors - different statuses of the companies",
                ),
                InfoItem(
                    infoImage = R.drawable.info_six,
                    title = "Meaning of colors",
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
        binding.buttonGetStarted.setOnClickListener { dismiss() }
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
        val TAG = InfoDialogFragment::class.java.simpleName
    }
}