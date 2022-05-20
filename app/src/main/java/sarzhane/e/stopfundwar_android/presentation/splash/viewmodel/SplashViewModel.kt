package sarzhane.e.stopfundwar_android.presentation.splash.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val companiesRepository: CompaniesRepository
) : ViewModel() {


    fun getCompanies(){
        viewModelScope.launch {
            companiesRepository.getAllCompanies()
        }
    }



}
