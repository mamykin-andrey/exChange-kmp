package ru.mamykin.exchange.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mamykin.exchange.R
import ru.mamykin.exchange.core.getDrawableResId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConverterScreen(
    state: ConverterViewModel.State,
    currencyOrAmountChanged: (CurrentCurrencyRate) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.rates_and_conversions_title))
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Close, "")
                    }
                }
            )
        }, content = { innerPadding ->
            Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                when (state) {
                    is ConverterViewModel.State.Loading -> GenericLoadingIndicatorComposable()
                    is ConverterViewModel.State.Error -> NetworkErrorComposable()
                    is ConverterViewModel.State.Loaded -> RatesListComposable(state, currencyOrAmountChanged)
                }
            }
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RatesListComposable(
    state: ConverterViewModel.State.Loaded,
    currencyOrAmountChanged: (CurrentCurrencyRate) -> Unit
) {
    LazyColumn {
        items(state.rates.size, key = { state.rates[it].code }) {
            CurrencyListItemComposable(
                state.rates[it],
                currencyOrAmountChanged,
                Modifier.animateItemPlacement(),
            )
        }
    }
}

@Composable
private fun CurrencyListItemComposable(
    viewData: RateViewData,
    onCurrencyOrAmountChanged: (CurrentCurrencyRate) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        @DrawableRes val iconRes = getDrawableResId(viewData.code.lowercase())
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = R.string.desc_currency_icon),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = viewData.code,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        val focusRequester = remember { FocusRequester() }
        var textFieldValue by remember { mutableStateOf(TextFieldValue(text = viewData.amountStr)) }
        TextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                if (isFocused && newValue.text.isNotBlank()) {
                    onCurrencyOrAmountChanged(
                        CurrentCurrencyRate(
                            code = viewData.code,
                            amountStr = newValue.text,
                            cursorPosition = newValue.selection.end
                        )
                    )
                }
            },
            placeholder = { Text(text = stringResource(id = R.string.zero_amount)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier
                .focusRequester(focusRequester)
                .width(90.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    if (focusState.isFocused) {
                        onCurrencyOrAmountChanged(
                            CurrentCurrencyRate(
                                code = viewData.code,
                                amountStr = textFieldValue.text,
                                cursorPosition = textFieldValue.selection.end,
                            )
                        )
                    }
                }
        )
        if (viewData.cursorPosition != null) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun GenericLoadingIndicatorComposable() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun NetworkErrorComposable() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.error_network),
            color = Color.Black,
            fontSize = 16.sp,
        )
        Button(onClick = {}) {
            Text(
                text = stringResource(R.string.error_retry_title),
            )
        }
    }
}

@Composable
internal fun ConverterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = {
            content()
        }
    )
}

@Preview
@Composable
fun ConverterScreenPreview() {
    ConverterTheme {
        ConverterScreen(
            ConverterViewModel.State.Loaded(
                listOf(
                    RateViewData("RUB", "900.50"),
                    RateViewData("USD", "1"),
                )
            ),
        ) {}
    }
}