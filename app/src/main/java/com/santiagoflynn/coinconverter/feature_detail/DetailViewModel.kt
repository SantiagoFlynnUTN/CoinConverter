package com.santiagoflynn.coinconverter.feature_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import com.santiagoflynn.coinconverter.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadConversion -> loadConversion(intent.id)
        }
    }

    private fun dispatch(action: DetailAction) {
        _state.value = reducer(_state.value, action)
    }

    private fun reducer(state: DetailState, action: DetailAction): DetailState {
        return when (action) {
            is DetailAction.SetLoading -> state.copy(isLoading = action.isLoading)
            is DetailAction.SetError -> state.copy(error = action.error)
            is DetailAction.SetConversion -> state.copy(conversion = action.conversion)
        }
    }

    private fun loadConversion(id: Long) {
        if (id <= 0) {
            dispatch(DetailAction.SetError("Invalid conversion ID"))
            dispatch(DetailAction.SetLoading(false))
            return
        }

        viewModelScope.launch {
            dispatch(DetailAction.SetLoading(true))

            repository.getConversionById(id)
                .onSuccess { conversion ->
                    dispatch(DetailAction.SetConversion(conversion))
                    dispatch(DetailAction.SetError(null))
                }
                .onFailure { e ->
                    dispatch(
                        DetailAction.SetError(
                            e.message ?: "Failed to load conversion details"
                        )
                    )
                }
                .also {
                    dispatch(DetailAction.SetLoading(false))
                }
        }
    }
}

data class DetailState(
    val conversion: ConversionResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class DetailIntent {
    data class LoadConversion(val id: Long) : DetailIntent()
}

sealed class DetailAction {
    data class SetLoading(val isLoading: Boolean) : DetailAction()
    data class SetError(val error: String?) : DetailAction()
    data class SetConversion(val conversion: ConversionResult) : DetailAction()
}