package sarzhane.e.stopfundwar_android.presentation.onboard

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.Navigator
import sarzhane.e.stopfundwar_android.core.navigation.PermissionsScreen
import sarzhane.e.stopfundwar_android.databinding.FragmentViewPagerBinding
import javax.inject.Inject

@AndroidEntryPoint
class OnboardFragment : Fragment(R.layout.fragment_view_pager) {
    private lateinit var onboardItemsAdapter: OnboardItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentViewPagerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnboardItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardItems(){
        onboardItemsAdapter = OnboardItemsAdapter(
            listOf(
                OnboardItem(
                    onboardImage = R.drawable.first_onboard,
                    title = getString(R.string.onboard_first_title),
                    description = getString(R.string.onboard_first_description)
                ),
                OnboardItem(
                    onboardImage = R.drawable.second_onboard,
                    title = getString(R.string.onboard_second_title),
                    description = getString(R.string.onboard_second_description)
                ),
                OnboardItem(
                    onboardImage = R.drawable.third_onboard,
                    title = getString(R.string.onboard_third_title),
                    description = getString(R.string.onboard_third_description)
                )

            )
        )
        val onboardViewPager = binding.viewPager
        onboardViewPager.adapter = onboardItemsAdapter
        onboardViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        (onboardViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.btnCloseInfo.setOnClickListener {
            if (onboardViewPager.currentItem + 1 < onboardItemsAdapter.itemCount){
                onboardViewPager.currentItem += 1
            }else{
                navigator.navigateTo(screen = PermissionsScreen(), addToBackStack = false)
                onBoardingFinished()
            }
        }
    }

    private fun setupIndicators(){
        indicatorsContainer = binding.indicatorsContainer
        val indicators = arrayOfNulls<ImageView>(onboardItemsAdapter.itemCount)
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

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    companion object {

        fun newInstance(): OnboardFragment = OnboardFragment()
    }

}