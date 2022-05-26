package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.ChipBinding
import sarzhane.e.stopfundwar_android.databinding.FragmentCompaniesBinding
import sarzhane.e.stopfundwar_android.presentation.camera.view.BrandAdapter
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CameraViewModel
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.presentation.companies.viewmodel.CompaniesViewModel
import sarzhane.e.stopfundwar_android.util.afterTextChanged
import sarzhane.e.stopfundwar_android.util.exhaustive
import sarzhane.e.stopfundwar_android.util.toVisible

@AndroidEntryPoint
class CompaniesFragment : Fragment(R.layout.fragment_companies) {

    private val binding by viewBinding(FragmentCompaniesBinding::bind)
    private val viewModel: CompaniesViewModel by viewModels()
    private val companiesAdapter = CompaniesAdapter()
    private var filter = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChip()
        setupCompaniesList()
        viewModel.searchResult.observe(viewLifecycleOwner, ::handleCompanies)
        binding.searchInput.afterTextChanged { viewModel.onNewQuery(it,filter) }
        viewModel.getListOfCompanies()
    }

    private fun handleCompanies(state: CompaniesResult) {
        when (state) {
            is CompaniesResult.SuccessResult -> {
                companiesAdapter.submitList(state.result)
            }
            is CompaniesResult.ErrorResult -> {
            }
            is CompaniesResult.EmptyResult -> {
            }
            CompaniesResult.Loading -> TODO()
        }.exhaustive
    }

    private fun setupCompaniesList() {
        binding.companiesList.adapter = companiesAdapter
    }

    private fun setupChip() {
        val nameList =
            arrayListOf("All", "Digging In", "Buying Time", "Scaling Back", "Suspension")
        for (name in nameList) {
            val chip = createChip(name)
            binding.chipGroup.addView(chip)
        }
    }

    private fun createChip(label: String): Chip {
        val chip = ChipBinding.inflate(layoutInflater).root
        chip.text = label
        return chip
    }


}