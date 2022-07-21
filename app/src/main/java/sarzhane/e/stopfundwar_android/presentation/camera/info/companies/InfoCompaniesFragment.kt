package sarzhane.e.stopfundwar_android.presentation.camera.info.companies

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.core.navigation.WebNavigator
import sarzhane.e.stopfundwar_android.databinding.FragmentCompaniesInfoBinding
import javax.inject.Inject

@AndroidEntryPoint
class InfoCompaniesFragment : DialogFragment(R.layout.fragment_companies_info) {


    @Inject
    lateinit var webNavigator: WebNavigator

    private val binding by viewBinding(FragmentCompaniesInfoBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDismiss.setOnClickListener { dismiss() }
        val spannableString = "The data for the companies list collected from the Yale school of management".makeLinks(
            "Yale school of management",
            resources.getColor(androidx.appcompat.R.color.material_deep_teal_200)
        )
        binding.infoText.movementMethod = LinkMovementMethod.getInstance()
        binding.infoText.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun String.makeLinks(phrase: String, phraseColor: Int): SpannableString {
        val spannableString = SpannableString(this)
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = phraseColor
                ds.isUnderlineText = false
            }
            override fun onClick(view: View) {
                webNavigator.navigateTo(YALE_LINK)
            }
        }
        val start = indexOf(phrase)
        val end = start + phrase.length
        spannableString.setSpan(
            clickableSpan,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }


    companion object {
        private const val YALE_LINK = "https://som.yale.edu/story/2022/over-1000-companies-have-curtailed-operations-russia-some-remain"
        fun newInstance(): InfoCompaniesFragment = InfoCompaniesFragment()

        @JvmStatic
        val TAG = InfoCompaniesFragment::class.java.simpleName
    }
}