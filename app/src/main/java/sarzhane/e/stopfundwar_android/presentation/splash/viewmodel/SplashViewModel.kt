package sarzhane.e.stopfundwar_android.presentation.splash.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

import kotlinx.coroutines.launch
import sarzhane.e.stopfundwar_android.data.companies.CompaniesRepository
import sarzhane.e.stopfundwar_android.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val companiesRepository: CompaniesRepository
) : ViewModel() {

    val errorsStream = SingleLiveEvent<Throwable>()
    val doneCommand = SingleLiveEvent<Unit>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        errorsStream.postValue(throwable)
    }

    fun load(shouldPreload: Boolean) {
        if (shouldPreload.not()) doneCommand.postValue(Unit).also { return }
        viewModelScope.launch(exceptionHandler) {
            companiesRepository.getAllCompanies()
        }.invokeOnCompletion { if (it == null) doneCommand.postValue(Unit) }
    }

}
