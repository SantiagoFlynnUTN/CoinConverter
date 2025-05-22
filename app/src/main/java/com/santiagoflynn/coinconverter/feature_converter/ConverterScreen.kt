package com.santiagoflynn.coinconverter.feature_converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.tooling.preview.Preview
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import com.santiagoflynn.coinconverter.domain.model.Currency
import com.santiagoflynn.coinconverter.ui.theme.CoinConverterTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    viewModel: ConverterViewModel = viewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    ConverterContent(modifier, uiState, viewModel::onIntent)
}

@Composable
fun ConverterContent(
    modifier: Modifier,
    uiState: ConverterState,
    onIntent: (ConverterIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {
        ConverterInputCard(
            uiState = uiState,
            onAmountChange = { onIntent(ConverterIntent.AmountChanged(it)) },
            onFromCurrencySelected = { onIntent(ConverterIntent.FromCurrencySelected(it)) },
            onToCurrencySelected = { onIntent(ConverterIntent.ToCurrencySelected(it)) },
            onSwapCurrencies = { onIntent(ConverterIntent.SwapCurrencies) },
            onConvertClicked = { onIntent(ConverterIntent.ConvertClicked) }
        )

        ErrorMessage(errorMessage = uiState.error)

        uiState.conversionResult?.let { result ->
            ConversionResultCard(result = result)
        }
    }
}

@Composable
private fun ConverterInputCard(
    uiState: ConverterState,
    onAmountChange: (String) -> Unit,
    onFromCurrencySelected: (Currency) -> Unit,
    onToCurrencySelected: (Currency) -> Unit,
    onSwapCurrencies: () -> Unit,
    onConvertClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConverterHeader()

            Spacer(modifier = Modifier.height(16.dp))

            AmountTextField(
                amount = uiState.amount,
                onAmountChange = onAmountChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            CurrencySelectionRow(
                fromCurrency = uiState.fromCurrency,
                toCurrency = uiState.toCurrency,
                availableCurrencies = uiState.currencies,
                onFromCurrencySelected = onFromCurrencySelected,
                onToCurrencySelected = onToCurrencySelected,
                onSwapCurrencies = onSwapCurrencies
            )

            PreviewResultSection(
                previewResult = uiState.previewResult,
                isLoading = uiState.isPreviewLoading,
                error = uiState.previewError
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConvertButton(
                isLoading = uiState.isLoading,
                isEnabled = uiState.amount.isNotEmpty(),
                onClick = onConvertClicked
            )
        }
    }
}

@Composable
private fun ConverterHeader() {
    Text(
        text = "Coin Converter",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun AmountTextField(
    amount: String,
    onAmountChange: (String) -> Unit
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text("Amount") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencySelectionRow(
    fromCurrency: Currency?,
    toCurrency: Currency?,
    availableCurrencies: List<Currency>,
    onFromCurrencySelected: (Currency) -> Unit,
    onToCurrencySelected: (Currency) -> Unit,
    onSwapCurrencies: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyDropdown(
            selectedCurrency = fromCurrency,
            availableCurrencies = availableCurrencies,
            onCurrencySelected = onFromCurrencySelected,
            label = "From",
            modifier = Modifier.weight(1f)
        )

        SwapCurrenciesButton(onClick = onSwapCurrencies)

        CurrencyDropdown(
            selectedCurrency = toCurrency,
            availableCurrencies = availableCurrencies,
            onCurrencySelected = onToCurrencySelected,
            label = "To",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SwapCurrenciesButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    selectedCurrency: Currency?,
    availableCurrencies: List<Currency>,
    onCurrencySelected: (Currency) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCurrency?.code ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            label = { Text(label) },
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableCurrencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text("${currency.code} - ${currency.name}") },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PreviewResultSection(
    previewResult: ConversionResult?,
    isLoading: Boolean,
    error: String?
) {
    AnimatedVisibility(
        visible = previewResult != null || isLoading,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PreviewHeader(isLoading)

                Spacer(modifier = Modifier.height(8.dp))

                previewResult?.let { preview ->
                    PreviewContent(preview)
                }

                error?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewHeader(isLoading: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun PreviewContent(preview: ConversionResult) {
    Text(
        text = "${String.format("%.2f", preview.fromAmount)} ${preview.fromCurrency}",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold
    )

    Text(
        text = "=",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 4.dp)
    )

    Text(
        text = "${String.format("%.2f", preview.toAmount)} ${preview.toCurrency}",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Text(
        text = "Rate: ${String.format("%.4f", preview.rate)}",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 4.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ConvertButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Convert & Save", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ErrorMessage(errorMessage: String?) {
    errorMessage?.let { message ->
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ConversionResultCard(result: ConversionResult) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ConversionResultHeader()

            Spacer(modifier = Modifier.height(12.dp))

            ConversionResultContent(result)

            Spacer(modifier = Modifier.height(16.dp))

            ConversionResultFooter(result, dateFormat)
        }
    }
}

@Composable
private fun ConversionResultHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Conversion Result",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ConversionResultContent(result: ConversionResult) {
    Text(
        text = String.format("%.2f", result.fromAmount),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Text(
        text = result.fromCurrency,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "=",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = String.format("%.2f", result.toAmount),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Text(
        text = result.toCurrency,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
    )
}

@Composable
private fun ConversionResultFooter(result: ConversionResult, dateFormat: SimpleDateFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Rate: ${String.format("%.4f", result.rate)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )

        Text(
            text = dateFormat.format(result.date),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConverterContentPreview() {
    val mockUiState = ConverterState(
        amount = "100",
        fromCurrency = Currency("USD", "US Dollar"),
        toCurrency = Currency("EUR", "Euro"),
        currencies = listOf(
            Currency("USD", "US Dollar"),
            Currency("EUR", "Euro"),
            Currency("JPY", "Japanese Yen")
        ),
        conversionResult = ConversionResult(
            id = 1L,
            fromCurrency = "USD",
            toCurrency = "EUR",
            fromAmount = 100.0,
            toAmount = 92.5,
            rate = 0.925,
            date = Date()
        ),
        previewResult = ConversionResult(
            id = 0L,
            fromCurrency = "USD",
            toCurrency = "EUR",
            fromAmount = 100.0,
            toAmount = 92.5,
            rate = 0.925,
            date = Date()
        )
    )
    MaterialTheme {
        ConverterContent(
            modifier = Modifier.fillMaxSize(),
            uiState = mockUiState,
            onIntent = {}
        )
    }
}