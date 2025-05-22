package com.santiagoflynn.coinconverter.feature_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onConversionClick: (Long) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    HistoryScreenContent(uiState = uiState, onConversionClick = onConversionClick)
}

@Composable
private fun HistoryScreenContent(uiState: HistoryState, onConversionClick: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        HistoryScreenHeader()

        when {
            uiState.isLoading -> LoadingState()
            uiState.error != null -> ErrorState(errorMessage = uiState.error)
            uiState.conversions.isEmpty() -> EmptyState()
            else -> ConversionsList(
                conversions = uiState.conversions,
                onConversionClick = onConversionClick
            )
        }
    }
}

@Composable
private fun HistoryScreenHeader() {
    Text(
        text = "Conversion History",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ErrorState(errorMessage: String?) {
    Text(
        text = errorMessage ?: "Unknown error",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No conversion history yet",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ConversionsList(
    conversions: List<ConversionResult>,
    onConversionClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = conversions,
            key = { conversion -> conversion.id }
        ) { conversion ->
            ConversionHistoryItem(
                conversion = conversion,
                onClick = { onConversionClick(conversion.id) }
            )
        }
    }
}

@Composable
private fun ConversionHistoryItem(
    conversion: ConversionResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ConversionAmount(conversion)
            Spacer(modifier = Modifier.height(4.dp))
            ConversionDetails(conversion)
        }
    }
}

@Composable
private fun ConversionAmount(conversion: ConversionResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatCurrencyAmount(conversion.fromAmount, conversion.fromCurrency),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = formatCurrencyAmount(conversion.toAmount, conversion.toCurrency),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ConversionDetails(conversion: ConversionResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Rate: ${formatRate(conversion.rate)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = formatDate(conversion.date),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatCurrencyAmount(amount: Double, currency: String): String {
    return "${String.format("%.2f", amount)} $currency"
}

private fun formatRate(rate: Double): String {
    return String.format("%.4f", rate)
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenErrorPreview() {
    HistoryScreenContent(
        uiState = HistoryState(error = "Failed to load data"),
        onConversionClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenEmptyPreview() {
    HistoryScreenContent(
        uiState = HistoryState(conversions = emptyList()),
        onConversionClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenWithDataPreview() {
    val sampleData = listOf(
        ConversionResult(
            id = 1,
            fromCurrency = "USD",
            toCurrency = "EUR",
            fromAmount = 100.0,
            toAmount = 85.23,
            rate = 0.8523,
            date = Date()
        ),
        ConversionResult(
            id = 2,
            fromCurrency = "EUR",
            toCurrency = "JPY",
            fromAmount = 50.0,
            toAmount = 7245.5,
            rate = 144.91,
            date = Date()
        )
    )

    HistoryScreenContent(
        uiState = HistoryState(conversions = sampleData),
        onConversionClick = {}
    )
}