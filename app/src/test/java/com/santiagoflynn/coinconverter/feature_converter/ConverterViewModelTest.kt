package com.santiagoflynn.coinconverter.feature_converter

import com.santiagoflynn.coinconverter.domain.model.Currency
import com.santiagoflynn.coinconverter.domain.repository.CurrencyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ConverterViewModelTest {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var repository: CurrencyRepository
    private val testDispatcher = StandardTestDispatcher()

    private val usd = Currency("USD", "US Dollar")
    private val eur = Currency("EUR", "Euro")
    private val gbp = Currency("GBP", "British Pound")
    private val testCurrencies = listOf(usd, eur, gbp)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        coEvery { repository.getCurrencies() } returns Result.success(testCurrencies)
        coEvery { repository.getExchangeRate(any(), any()) } returns Result.success(1.2)
        coEvery { repository.saveConversion(any()) } returns Result.success(1L)

        viewModel = ConverterViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle() // Process the init loading
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads currencies and sets defaults`() = runTest {
        // Verify
        coVerify { repository.getCurrencies() }

        // Assert state
        val state = viewModel.state.value
        assertEquals(testCurrencies, state.currencies)
        assertEquals(usd, state.fromCurrency)
        assertEquals(eur, state.toCurrency)
    }

    @Test
    fun `changing amount updates state and preview`() = runTest {
        // When
        viewModel.onIntent(ConverterIntent.AmountChanged("100"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("100", viewModel.state.value.amount)
        assertNotNull(viewModel.state.value.previewResult)
        coVerify { repository.getExchangeRate(usd.code, eur.code) }
    }

    @Test
    fun `swapping currencies exchanges from and to`() = runTest {
        // When
        viewModel.onIntent(ConverterIntent.SwapCurrencies)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(eur, state.fromCurrency)
        assertEquals(usd, state.toCurrency)
    }

    @Test
    fun `converting currency saves result`() = runTest {
        // Given
        viewModel.onIntent(ConverterIntent.AmountChanged("100"))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onIntent(ConverterIntent.ConvertClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { repository.saveConversion(any()) }
        assertNotNull(viewModel.state.value.conversionResult)
    }

    @Test
    fun `repository error sets error state`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getCurrencies() } returns Result.failure(Exception(errorMessage))

        // When
        viewModel.onIntent(ConverterIntent.LoadCurrencies)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `selecting currencies updates preview`() = runTest {
        // Given - specific exchange rate for GBP to EUR
        val rate = 1.15
        coEvery { repository.getExchangeRate(gbp.code, eur.code) } returns Result.success(rate)

        // When - select GBP as from currency
        viewModel.onIntent(ConverterIntent.AmountChanged("100"))
        viewModel.onIntent(ConverterIntent.FromCurrencySelected(gbp))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(gbp, viewModel.state.value.fromCurrency)
        assertEquals(eur, viewModel.state.value.toCurrency)

        val preview = viewModel.state.value.previewResult
        assertNotNull(preview)
        assertEquals(gbp.code, preview?.fromCurrency)
        assertEquals(eur.code, preview?.toCurrency)
        assertEquals(100.0, preview?.fromAmount)
        assertEquals(100.0 * rate, preview?.toAmount)
    }
}