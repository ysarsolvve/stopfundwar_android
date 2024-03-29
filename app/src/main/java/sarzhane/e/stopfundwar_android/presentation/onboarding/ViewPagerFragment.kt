package sarzhane.e.stopfundwar_android.presentation.onboarding

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


/**
 * Contains the data that you want to display
 */
@AndroidEntryPoint
class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {
    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    @Inject
    lateinit var navigator: Navigator

    private val binding by viewBinding(FragmentViewPagerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems(){
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.first,
                    title = "Every day Civilians \n" +
                            "are dying in Ukraine",
                    description = "We do not support brands that help\n"+
                            " Russian aggression"
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.second,
                    title = "Every day Russia\n" +
                            "sends new troops",
                    description = "Our app was created to destroy\n" +
                            " Russia’s economy"
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.third,
                    title = "Help the Russian\n" +
                            " economy to collapse\n" +
                            "faster",
                    description = "Make your own decision whether to\n" +
                            " buy brands that support war."
                )

            )
        )
        val onboardingViewPager = binding.viewPager
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.buttonGetStarted.setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount){
                onboardingViewPager.currentItem += 1
            }else{
                navigator.navigateTo(screen = PermissionsScreen(), addToBackStack = false)
                onBoardingFinished()
            }
        }
    }

    private fun setupIndicators(){
        indicatorsContainer = binding.indicatorsContainer
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
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
        // Using LiveTemplate for SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    companion object {

        fun newInstance(): ViewPagerFragment = ViewPagerFragment()
    }

}