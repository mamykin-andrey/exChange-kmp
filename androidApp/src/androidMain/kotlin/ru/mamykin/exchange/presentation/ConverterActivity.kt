package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.mamykin.exchange.R
import ru.mamykin.exchange.core.di.Scopes
import toothpick.Toothpick

internal class ConverterActivity : AppCompatActivity() {

    private val viewModel by lazy {
        Toothpick.openScopes(Scopes.APP_SCOPE, this).getInstance(ConverterViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContent {
            ConverterTheme {
                val state by viewModel.stateFlow.collectAsState()
                ConverterScreen(state, viewModel::onCurrencyOrAmountChanged)
            }
        }
        // initToolbar()
        // initRatesList()
        // initViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRatesLoading()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRatesLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            Toothpick.closeScope(this)
        }
    }

    // private fun initToolbar() = binding.apply {
    //     toolbar.title = getString(R.string.rates_and_conversions_title)
    //     toolbar.navigationIcon = ContextCompat.getDrawable(this@ConverterActivity, R.drawable.ic_close)
    //     toolbar.setNavigationOnClickListener { finish() }
    // }
    //
    // private fun initViewModel() {
    //     launchObserve {
    //         viewModel.isLoading.collect { showLoading(it) }
    //     }
    //     launchObserve {
    //         viewModel.rates.collect {
    //             it?.let { it1 -> showRates(RateViewDataMapper.transform(it1, this@ConverterActivity)) }
    //         }
    //     }
    //     launchObserve {
    //         viewModel.error.collect { it?.let { it1 -> showError(it1) } }
    //     }
    //     launchObserve {
    //         viewModel.currentRateChanged.collect {
    //             Handler().post {
    //                 binding.rvRates.scrollToPosition(0)
    //             }
    //         }
    //     }
    // }

    private fun launchObserve(observe: suspend () -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                observe()
            }
        }
    }

    // private fun initRatesList() = binding.apply {
    //     rvRates.setHasFixedSize(true)
    //     rvRates.adapter = adapter
    // }
    //
    // private fun showLoading(show: Boolean) = binding.apply {
    //     vLoadingError.root.isVisible = !show
    //     rvRates.isVisible = !show
    //     pbLoading.isVisible = show
    // }
    //
    //
    // private fun showRates(rates: List<AndroidCurrencyRateViewData>) = binding.apply {
    //     showLoading(false)
    //     pbLoading.isVisible = false
    //     vLoadingError.root.isVisible = false
    //     rvRates.isVisible = true
    //     adapter.submitList(rates)
    // }
    //
    // private fun showError(errorMsg: String) = binding.apply {
    //     pbLoading.isVisible = false
    //     rvRates.isVisible = false
    //     vLoadingError.root.isVisible = true
    //     vLoadingError.tvErrorMessage.text = errorMsg
    //     vLoadingError.btnRetry.setOnClickListener { viewModel.startRatesLoading() }
    // }
}