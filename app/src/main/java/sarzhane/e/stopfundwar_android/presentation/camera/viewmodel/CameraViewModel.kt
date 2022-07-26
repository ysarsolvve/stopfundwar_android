package sarzhane.e.stopfundwar_android.presentation.camera.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepository
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult.EmptyResult
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult.SuccessResult
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val companiesRepository: CompaniesRepository
) : ViewModel() {

    private val _searchResult = MutableLiveData<CompaniesResult>(EmptyResult)
    val searchResult: LiveData<CompaniesResult> = _searchResult

    fun getCompany(ids: List<String>) = viewModelScope.launch {getCompaniesByIds(ids)}

    fun getColors(): Map<Int, Int> = companiesRepository.getColorMap()


    private suspend fun getCompaniesByIds(ids: List<String>){
        val result = companiesRepository.getDataByIds(ids)
        if (result.isNotEmpty())_searchResult.value = SuccessResult(result)
        else _searchResult.value = EmptyResult

    }

}
