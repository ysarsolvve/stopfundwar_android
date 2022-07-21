package sarzhane.e.stopfundwar_android.presentation.charity.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.WebNavigator
import sarzhane.e.stopfundwar_android.databinding.FragmentCharityBinding
import javax.inject.Inject

@AndroidEntryPoint
class CharityFragment : Fragment(R.layout.fragment_charity) {

    @Inject
    lateinit var webNavigator: WebNavigator
    private val binding by viewBinding(FragmentCharityBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.translate.setOnClickListener { showInfoDialogFragment() }
        setupListeners()
    }

    private fun setupListeners() {
        binding.firstCharity.setOnClickListener {
            webNavigator.navigateTo(FIRST_CHARITY)
        }
        binding.secondCharity.setOnClickListener {
            webNavigator.navigateTo(SECOND_CHARITY)
        }
        binding.thirdCharity.setOnClickListener {
            webNavigator.navigateTo(THIRD_CHARITY)
        }
        binding.fourthCharity.setOnClickListener {
            webNavigator.navigateTo(FOURTH_CHARITY)
        }
        binding.fifthCharity.setOnClickListener {
            webNavigator.navigateTo(FIFTH_CHARITY)
        }
        binding.sixCharity.setOnClickListener {
            webNavigator.navigateTo(SIX_CHARITY)
        }
        binding.sevenCharity.setOnClickListener {
            webNavigator.navigateTo(SEVEN_CHARITY)
        }
        binding.eightCharity.setOnClickListener {
            webNavigator.navigateTo(EIGHT_CHARITY)
        }
        binding.nineCharity.setOnClickListener {
            webNavigator.navigateTo(NINE_CHARITY)
        }
    }

    private fun showInfoDialogFragment() {
        val dialogFragment =SelectLanguageFragment.newInstance()
        dialogFragment.show(childFragmentManager, SelectLanguageFragment.TAG)
    }

    companion object {

        private const val FIRST_CHARITY = "https://bank.gov.ua/en/news/all/natsionalniy-bank-vidkriv-spetsrahunok-dlya-zboru-koshtiv-na-potrebi-armiyi"
        private const val SECOND_CHARITY = "https://www.comebackalive.in.ua/"
        private const val THIRD_CHARITY = "https://armysos.com.ua/donate/"
        private const val FOURTH_CHARITY = "https://uahelp.monobank.ua/"
        private const val FIFTH_CHARITY = "https://novaposhta.ua/eng/"
        private const val SIX_CHARITY = "https://www.unicef.org.uk/donate/donate-now-to-protect-children-in-ukraine/"
        private const val SEVEN_CHARITY = "https://voices.org.ua/en/donat/"
        private const val EIGHT_CHARITY = "https://prytulafoundation.org/"
        private const val NINE_CHARITY = "https://readymag.com/u3166650941/LNJ-fund/"

    }
}