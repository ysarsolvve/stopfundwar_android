package sarzhane.e.stopfundwar_android.presentation.charity.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.FragmentSelectLanguageBinding
import sarzhane.e.stopfundwar_android.util.setFullScreen

@AndroidEntryPoint
class SelectLanguageFragment : DialogFragment(R.layout.fragment_select_language) {


    private val binding by viewBinding(FragmentSelectLanguageBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
    }




    companion object {
        fun newInstance(): SelectLanguageFragment = SelectLanguageFragment()

        @JvmStatic
        val TAG = SelectLanguageFragment::class.java.simpleName
    }
}