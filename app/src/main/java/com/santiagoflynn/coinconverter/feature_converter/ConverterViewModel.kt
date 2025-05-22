package com.santiagoflynn.coinconverter.feature_converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import com.santiagoflynn.coinconverter.domain.model.Currency
import com.santiagoflynn.coinconverter.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.Result
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConverterState())
    val state: StateFlow<ConverterState> = _state.asStateFlow()

    private var previewJob: Job? = null

    init {
        onIntent(ConverterIntent.LoadCurrencies)
    }

    fun onIntent(intent: ConverterIntent) {
        when (intent) {
            is ConverterIntent.AmountChanged -> {
                dispatch(ConverterAction.UpdateAmount(intent.amount))
                updatePreview()
            }

            is ConverterIntent.FromCurrencySelected -> {
                dispatch(ConverterAction.SelectFromCurrency(intent.currency))
                updatePreview()
            }

            is ConverterIntent.ToCurrencySelected -> {
                dispatch(ConverterAction.SelectToCurrency(intent.currency))
                updatePreview()
            }

            is ConverterIntent.ConvertClicked -> {
                state.value.error?.let {
                    loadCurrencies()
                }
                convertCurrency()
            }

            is ConverterIntent.SwapCurrencies -> {
                dispatch(ConverterAction.SwapCurrencies)
                updatePreview()
            }

            is ConverterIntent.LoadCurrencies -> {
                loadCurrencies()
            }
        }
    }

    private fun dispatch(action: ConverterAction) {
        _state.value = reducer(_state.value, action)
    }

    private fun reducer(state: ConverterState, action: ConverterAction): ConverterState {
        return when (action) {
            is ConverterAction.SetCurrencies -> state.copy(
                currencies = action.currencies,
                fromCurrency = action.currencies.firstOrNull(),
                toCurrency = action.currencies.getOrNull(1)
            )

            is ConverterAction.UpdateAmount -> state.copy(amount = action.amount)
            is ConverterAction.SelectFromCurrency -> state.copy(fromCurrency = action.currency)
            is ConverterAction.SelectToCurrency -> state.copy(toCurrency = action.currency)
            is ConverterAction.SwapCurrencies -> state.copy(
                fromCurrency = state.toCurrency,
                toCurrency = state.fromCurrency
            )

            is ConverterAction.SetLoading -> state.copy(isLoading = action.isLoading)
            is ConverterAction.SetError -> state.copy(error = action.error)
            is ConverterAction.SetPreviewLoading -> state.copy(isPreviewLoading = action.isLoading)
            is ConverterAction.SetPreviewError -> state.copy(previewError = action.error)
            is ConverterAction.SetPreviewResult -> state.copy(previewResult = action.result)
            is ConverterAction.SetConversionResult -> state.copy(conversionResult = action.result)
        }
    }

    private fun updatePreview() {
        previewJob?.cancel()

        val currentState = state.value
        val amount = currentState.amount.toDoubleOrNull() ?: run {
            dispatch(ConverterAction.SetPreviewResult(null))
            return
        }

        val fromCurrency = currentState.fromCurrency?.code ?: run {
            dispatch(ConverterAction.SetPreviewResult(null))
            return
        }

        val toCurrency = currentState.toCurrency?.code ?: run {
            dispatch(ConverterAction.SetPreviewResult(null))
            return
        }

        previewJob = viewModelScope.launch {
            dispatch(ConverterAction.SetPreviewLoading(true))

            repository.getExchangeRate(fromCurrency, toCurrency)
                .onSuccess { rate ->
                    val convertedAmount = amount * rate

                    val previewResult = ConversionResult(
                        fromCurrency = fromCurrency,
                        toCurrency = toCurrency,
                        fromAmount = amount,
                        toAmount = convertedAmount,
                        rate = rate
                    )

                    dispatch(ConverterAction.SetPreviewResult(previewResult))
                    dispatch(ConverterAction.SetPreviewError(null))
                }
                .onFailure {
                    if(isActive){
                        dispatch(ConverterAction.SetPreviewResult(null))
                        dispatch(ConverterAction.SetPreviewError("Preview unavailable"))
                    }
                }
                .also {
                    dispatch(ConverterAction.SetPreviewLoading(false))
                }
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            dispatch(ConverterAction.SetLoading(true))

            repository.getCurrencies()
                .onSuccess { currencies ->
                    dispatch(ConverterAction.SetCurrencies(currencies))
                    dispatch(ConverterAction.SetError(null))
                    updatePreview()
                }
                .onFailure { e ->
                    dispatch(ConverterAction.SetError(e.message ?: "Unknown error"))
                }
                .also {
                    dispatch(ConverterAction.SetLoading(false))
                }
        }
    }

    private fun convertCurrency() {
        val currentState = state.value
        val amount = currentState.amount.toDoubleOrNull() ?: return
        val fromCurrency = currentState.fromCurrency?.code ?: return
        val toCurrency = currentState.toCurrency?.code ?: return

        viewModelScope.launch {
            dispatch(ConverterAction.SetLoading(true))

            repository.getExchangeRate(fromCurrency, toCurrency)
                .onSuccess { rate ->
                    saveConversionResult(amount, rate, fromCurrency, toCurrency)
                }
                .onFailure { e ->
                    dispatch(ConverterAction.SetError(e.message ?: "Unknown error"))
                }
                .also {
                    dispatch(ConverterAction.SetLoading(false))
                }
        }
    }

    private suspend fun saveConversionResult(amount: Double, rate: Double, fromCurrency: String, toCurrency: String) {
        val convertedAmount = amount * rate

        val result = ConversionResult(
            fromCurrency = fromCurrency,
            toCurrency = toCurrency,
            fromAmount = amount,
            toAmount = convertedAmount,
            rate = rate
        )
        repository.saveConversion(result)
            .onSuccess { id ->
                val finalResult = result.copy(id = id)
                dispatch(ConverterAction.SetConversionResult(finalResult))
            }
            .onFailure { e ->
                dispatch(
                    ConverterAction.SetError(
                        e.message ?: "Failed to save conversion"
                    )
                )
            }
    }
}

data class ConverterState(
    val currencies: List<Currency> = emptyList(),
    val fromCurrency: Currency? = null,
    val toCurrency: Currency? = null,
    val amount: String = "",
    val previewResult: ConversionResult? = null,
    val conversionResult: ConversionResult? = null,
    val isLoading: Boolean = false,
    val isPreviewLoading: Boolean = false,
    val error: String? = null,
    val previewError: String? = null
)

sealed class ConverterIntent {
    data class AmountChanged(val amount: String) : ConverterIntent()
    data class FromCurrencySelected(val currency: Currency) : ConverterIntent()
    data class ToCurrencySelected(val currency: Currency) : ConverterIntent()
    data object ConvertClicked : ConverterIntent()
    data object SwapCurrencies : ConverterIntent()
    data object LoadCurrencies : ConverterIntent()
}

sealed class ConverterAction {
    data class SetCurrencies(val currencies: List<Currency>) : ConverterAction()
    data class UpdateAmount(val amount: String) : ConverterAction()
    data class SelectFromCurrency(val currency: Currency) : ConverterAction()
    data class SelectToCurrency(val currency: Currency) : ConverterAction()
    data object SwapCurrencies : ConverterAction()
    data class SetLoading(val isLoading: Boolean) : ConverterAction()
    data class SetError(val error: String?) : ConverterAction()
    data class SetPreviewLoading(val isLoading: Boolean) : ConverterAction()
    data class SetPreviewError(val error: String?) : ConverterAction()
    data class SetPreviewResult(val result: ConversionResult?) : ConverterAction()
    data class SetConversionResult(val result: ConversionResult?) : ConverterAction()
}