package sarzhane.e.stopfundwar_android.presentation.camera.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepository
import sarzhane.e.stopfundwar_android.presentation.camera.viewmodel.CompaniesResult.*
import sarzhane.e.stopfundwar_android.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val companiesRepository: CompaniesRepository
) : ViewModel() {


    private val _searchResult = MutableStateFlow<CompaniesResult>(EmptyResult)
    val searchResult: LiveData<CompaniesResult>
        get() = _searchResult
            .asLiveData(viewModelScope.coroutineContext)

    fun getCompany(ids: List<String>){
        viewModelScope.launch {
            getCompaniesByIds(ids)
        }
    }

    private suspend fun getCompaniesByIds(ids: List<String>){
        val result = companiesRepository.getDataByIds(ids)
        Log.d("response", "${result}")
        if (result.isNotEmpty())_searchResult.value = SuccessResult(result)
        else _searchResult.value = EmptyResult

    }

}
