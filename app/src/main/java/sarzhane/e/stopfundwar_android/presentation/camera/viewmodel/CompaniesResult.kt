package sarzhane.e.stopfundwar_android.presentation.camera.viewmodel

import sarzhane.e.stopfundwar_android.domain.companies.Company


sealed class CompaniesResult {

    object EmptyResult : CompaniesResult()
    data class SuccessResult(val result: List<Company>) : CompaniesResult()
    data class ErrorResult(val e: Throwable) : CompaniesResult()
}
