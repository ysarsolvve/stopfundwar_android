package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.FragmentCompaniesBinding
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.presentation.companies.viewmodel.CompaniesViewModel
import sarzhane.e.stopfundwar_android.util.afterTextChanged
import sarzhane.e.stopfundwar_android.util.exhaustive


@AndroidEntryPoint
class CompaniesFragment : Fragment(R.layout.fragment_companies) {

    private val binding by viewBinding(FragmentCompaniesBinding::bind)
    private val viewModel: CompaniesViewModel by viewModels()
    private val companiesAdapter = CompaniesAdapter()
    private var filter = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCompaniesList()
        viewModel.getListOfCompanies()
        binding.searchInput.afterTextChanged { viewModel.onNewQuery(it, filter) }
        viewModel.searchResult.observe(viewLifecycleOwner, ::handleCompanies)
        binding.chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedStatusType = chip.text.toString()
            val searchInput = binding.searchInput.text.toString()
            viewModel.onNewQuery(searchInput, selectedStatusType)
            filter = selectedStatusType
        }
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

}