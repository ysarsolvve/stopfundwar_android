package sarzhane.e.stopfundwar_android.presentation.companies.view

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import sarzhane.e.stopfundwar_android.R
import sarzhane.e.stopfundwar_android.databinding.FragmentCompaniesBinding
import sarzhane.e.stopfundwar_android.domain.companies.Company
import sarzhane.e.stopfundwar_android.domain.companies.DataModel
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.presentation.companies.viewmodel.CompaniesViewModel
import sarzhane.e.stopfundwar_android.util.afterTextChanged
import sarzhane.e.stopfundwar_android.util.exhaustive
import sarzhane.e.stopfundwar_android.util.fastsroll.FastScrollItemIndicator
import sarzhane.e.stopfundwar_android.util.fastsroll.FastScrollerView
import sarzhane.e.stopfundwar_android.util.toGone
import sarzhane.e.stopfundwar_android.util.toVisible
import java.util.*


@AndroidEntryPoint
class CompaniesFragment : Fragment(R.layout.fragment_companies) {

    private val binding by viewBinding(FragmentCompaniesBinding::bind)
    private val viewModel: CompaniesViewModel by viewModels()
    private val companiesAdapter = CompaniesAdapter()
    private var filter = "All"
    private var isInitialize = false

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
        binding.sampleBasicFastscrollerThumb.apply {
            setupWithFastScroller(binding.sampleBasicFastscroller)
        }
    }

    private fun handleCompanies(state: CompaniesResult) {
        when (state) {
            is CompaniesResult.SuccessResult -> {
                val sortedList = sortAlphabetList(state.result)
                if (sortedList.size< 10){
                    binding.sampleBasicFastscroller.toGone()
                    binding.sampleBasicFastscrollerThumb.toGone()
                }else{
                    binding.sampleBasicFastscroller.toVisible()
                    binding.sampleBasicFastscrollerThumb.toVisible()
                }
                companiesAdapter.submitList(sortedList)
                setUpFastScrollView(sortedList)
            }
            is CompaniesResult.ErrorResult -> {
            }
            is CompaniesResult.EmptyResult -> {
            }
            CompaniesResult.Loading -> TODO()
        }.exhaustive
    }

    private fun setUpFastScrollView(sortedList: List<DataModel>){
        if (isInitialize)return
        binding.sampleBasicFastscroller.apply {
            setupWithRecyclerView(
                binding.companiesList,
                { position ->
                    sortedList[position]
                        .let { item ->
                            when (item) {
                                is DataModel.Header -> FastScrollItemIndicator.Text(item.title)
                                is DataModel.Company ->
                                    FastScrollItemIndicator.Text(
                                        item.let {
                                            it.brandName!!
                                                .substring(0, 1)
                                                .toUpperCase(Locale.ROOT)
                                        }
                                    )
                            }
                        }
                }
            )
        }
        isInitialize = true
    }

    private fun sortAlphabetList(list: List<Company>): List<DataModel> {
        val localList = mutableListOf<DataModel>()
        for (item in list.indices) {
            if (item == 0) {
                localList.add( DataModel.Header(list[item].brandName?.substring(0, 1)!!))
                localList.add(DataModel.Company(
                    list[item].id,
                    list[item].brandName,
                    list[item].logo,
                    list[item].statusInfo,
                    list[item].statusRate,
                    list[item].description
                ))
            } else {
                val groupName = list[item].brandName?.substring(0, 1)
                val preGroupName = list[item - 1].brandName?.substring(0, 1)
                if (!TextUtils.equals(groupName, preGroupName)) {
                    localList.add(DataModel.Header(list[item].brandName?.substring(0, 1)!!))
                    localList.add(DataModel.Company(
                        list[item].id,
                        list[item].brandName,
                        list[item].logo,
                        list[item].statusInfo,
                        list[item].statusRate,
                        list[item].description
                    ))
                } else {
                    localList.add(
                        DataModel.Company(
                            list[item].id,
                            list[item].brandName,
                            list[item].logo,
                            list[item].statusInfo,
                            list[item].statusRate,
                            list[item].description
                        )
                    )
                }
            }
        }
        return localList
    }

    private fun setupCompaniesList() {
        binding.companiesList.adapter = companiesAdapter
    }

}