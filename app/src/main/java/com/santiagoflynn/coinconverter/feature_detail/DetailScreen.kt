package com.santiagoflynn.coinconverter.feature_detail

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.santiagoflynn.coinconverter.domain.model.ConversionResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateUp: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { DetailTopAppBar(onNavigateUp = onNavigateUp) }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            DetailScreenContent(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopAppBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = { Text("Conversion Details") },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = ""
                )
            }
        }
    )
}

@Composable
private fun DetailScreenContent(
    state: DetailState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorMessage(error = state.error)
            state.conversion != null -> ConversionDetailCard(conversion = state.conversion)
            else -> EmptyState()
        }
    }
}

@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun ErrorMessage(error: String?) {
    Column(
        modifier = Modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error ?: "Unknown error",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = "No conversion found",
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun ConversionDetailCard(
    conversion: ConversionResult
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConversionDetailHeader()

            Spacer(modifier = Modifier.height(8.dp))

            ConversionDetailBody(conversion = conversion)

            ConversionDetailFooter(
                date = dateFormat.format(conversion.date),
                id = conversion.id
            )
        }
    }
}

@Composable
private fun ConversionDetailHeader() {
    Text(
        text = "Conversion Details",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun ConversionDetailBody(
    conversion: ConversionResult
) {
    DetailRow(
        "From",
        "${String.format("%.2f", conversion.fromAmount)} ${conversion.fromCurrency}"
    )
    DetailRow(
        "To",
        "${String.format("%.2f", conversion.toAmount)} ${conversion.toCurrency}"
    )
    DetailRow("Exchange Rate", String.format("%.6f", conversion.rate))
}

@Composable
private fun ConversionDetailFooter(
    date: String,
    id: Long
) {
    DetailRow("Date & Time", date)
    DetailRow("Conversion ID", "#${id}")
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    val sampleConversion = com.santiagoflynn.coinconverter.domain.model.ConversionResult(
        id = 123,
        fromCurrency = "USD",
        toCurrency = "EUR",
        fromAmount = 100.0,
        toAmount = 92.34,
        rate = 0.9234,
        date = Date()
    )

    val state = DetailState(
        conversion = sampleConversion,
        isLoading = false,
        error = null
    )

    MaterialTheme {
        Surface {
            DetailScreenContent(state = state)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    MaterialTheme {
        Surface {
            DetailScreenContent(
                state = DetailState(
                    isLoading = false,
                    conversion = null,
                    error = "Failed to load conversion details"
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    MaterialTheme {
        Surface {
            DetailScreenContent(
                state = DetailState(
                    isLoading = false,
                    conversion = null,
                    error = null
                )
            )
        }
    }
}