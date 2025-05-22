package com.santiagoflynn.coinconverter.feature_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import com.santiagoflynn.coinconverter.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.Result
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        onIntent(HistoryIntent.LoadHistory)
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadHistory -> loadHistory()
        }
    }

    private fun dispatch(action: HistoryAction) {
        _state.value = reducer(_state.value, action)
    }

    private fun reducer(state: HistoryState, action: HistoryAction): HistoryState {
        return when (action) {
            is HistoryAction.SetLoading -> state.copy(isLoading = action.isLoading)
            is HistoryAction.SetError -> state.copy(error = action.error)
            is HistoryAction.SetConversions -> state.copy(conversions = action.conversions)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            dispatch(HistoryAction.SetLoading(true))
            repository.getConversionHistory().collect { result ->
                result
                    .onSuccess { conversions ->
                        dispatch(HistoryAction.SetConversions(conversions))
                        dispatch(HistoryAction.SetError(null))
                    }
                    .onFailure { e ->
                        dispatch(
                            HistoryAction.SetError(
                                e.message ?: "Failed to load conversion history"
                            )
                        )
                    }
                    .also {
                        dispatch(HistoryAction.SetLoading(false))
                    }
            }
        }
    }
}

data class HistoryState(
    val conversions: List<ConversionResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HistoryIntent {
    data object LoadHistory : HistoryIntent()
}

sealed class HistoryAction {
    data class SetLoading(val isLoading: Boolean) : HistoryAction()
    data class SetError(val error: String?) : HistoryAction()
    data class SetConversions(val conversions: List<ConversionResult>) : HistoryAction()
}