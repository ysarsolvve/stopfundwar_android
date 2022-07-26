package sarzhane.e.stopfundwar_android.presentation.charity.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zeugmasolutions.localehelper.Locales
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.FragmentSelectLanguageBinding
import sarzhane.e.stopfundwar_android.presentation.MainActivity
import sarzhane.e.stopfundwar_android.util.setFullScreen
import java.util.*

@AndroidEntryPoint
class SelectLanguageFragment : DialogFragment(R.layout.fragment_select_language) {


    private val binding by viewBinding(FragmentSelectLanguageBinding::bind)
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("selectLanguage", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        val checkedButtonId = binding.radioGroup.checkedRadioButtonId
        binding.radioGroup.check(if(getCheckedId(CHECKED) == -1)checkedButtonId else getCheckedId(CHECKED) )
        var locale: Locale = Locales.English
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            locale = when(checkedId){
                R.id.rbEnglish -> {setCheckedId(CHECKED, checkedId); Locales.English }
                R.id.rbUkrainian -> {setCheckedId(CHECKED, checkedId); Locales.Ukrainian }
                else -> Locales.English
            }
        }
        binding.btnGetStarted.setOnClickListener { (activity as MainActivity).updateLocale(locale); dismiss() }
    }

    private fun getCheckedId(key: String): Int {
        return sharedPref.getInt(key, -1)
    }

    private fun setCheckedId(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    companion object {
        fun newInstance(): SelectLanguageFragment = SelectLanguageFragment()
        private const val CHECKED = "checkedRadio"
        @JvmStatic
        val TAG: String = SelectLanguageFragment::class.java.simpleName
    }
}