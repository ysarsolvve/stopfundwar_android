package sarzhane.e.stopfundwar_android.presentation.companies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepository
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult.*
import javax.inject.Inject


@HiltViewModel
class CompaniesViewModel @Inject constructor(
    private val companiesRepository: CompaniesRepository
) : ViewModel() {

    fun getListOfCompanies() {
        viewModelScope.launch {
            getData()
        }
    }

    fun onNewQuery(query: String, filter: String) {
       viewModelScope.launch {
           if (query.isEmpty() && filter == "All") {
               getData()
           } else if (query.isEmpty() && filter != "All"){
               getDataByFilter(filter)
           }
           else {
               getCompanies(query, filter)
           }
       }
    }

    private val _searchResult = MutableStateFlow<CompaniesResult>(EmptyResult)
    val searchResult: LiveData<CompaniesResult>
        get() = _searchResult
            .asLiveData(viewModelScope.coroutineContext)

    private suspend fun getCompanies(searchQuery: String, filter: String){
        val result = companiesRepository.getCompanies(searchQuery, filter)
        if (result.isNotEmpty())_searchResult.value = SuccessResult(result)
        else _searchResult.value = EmptyResult
    }
    private suspend fun getData(){
        val result = companiesRepository.getData()
        if (result.isNotEmpty())_searchResult.value = SuccessResult(result)
        else _searchResult.value = EmptyResult
    }

    private suspend fun getDataByFilter(filter: String){
        val result = companiesRepository.getDataByFilter(filter)
        if (result.isNotEmpty())_searchResult.value = SuccessResult(result)
        else _searchResult.value = EmptyResult
    }

}
