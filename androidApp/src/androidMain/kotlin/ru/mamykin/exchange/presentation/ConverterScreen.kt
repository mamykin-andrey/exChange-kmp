package ru.mamykin.exchange.presentation

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.mamykin.exchange.AppScreen
import ru.mamykin.exchange.AppStrings
import ru.mamykin.exchange.core.getDrawableResId

// TODO: Fix app icon and title and splash screen
// TODO: Fix unit-tests mocking
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConverterScreen(
    navController: NavController,
    state: ConverterScreenState,
    effectFlow: Flow<ConverterScreenEffect>,
    onIntent: (ConverterScreenIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is ConverterScreenEffect.CurrentRateChanged -> {
                    delay(300)
                    listState.scrollToItem(0)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        onIntent(ConverterScreenIntent.StartLoading)
    }
    DisposableEffect(Unit) {
        onDispose {
            onIntent(ConverterScreenIntent.StopLoading)
        }
    }

    Scaffold(
        topBar = {
            val activity = (LocalContext.current as? Activity)
            TopAppBar(
                title = {
                    Text(text = AppStrings.CONVERTER_TITLE)
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.Filled.Close, "")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(AppScreen.AppInfo.route) }) {
                        Icon(Icons.Filled.Info, "")
                    }
                }
            )
        }, content = { innerPadding ->
            Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                when (state) {
                    is ConverterScreenState.Loading -> GenericLoadingIndicatorComposable()
                    is ConverterScreenState.Error -> NetworkErrorComposable(onIntent)
                    is ConverterScreenState.Loaded -> RatesListComposable(state, onIntent, listState)
                }
            }
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RatesListComposable(
    state: ConverterScreenState.Loaded,
    onIntent: (ConverterScreenIntent) -> Unit,
    listState: LazyListState,
) {
    TrackRecompositions("RatesListComposable")

    LazyColumn(state = listState) {
        itemsIndexed(
            items = state.rates,
            key = { _, item -> item.code }
        ) { _, item ->
            CurrencyListItemComposable(
                item,
                onIntent,
                Modifier.animateItemPlacement(),
            )
        }
    }
}

@Composable
private fun CurrencyListItemComposable(
    viewData: CurrencyRateViewData,
    onIntent: (ConverterScreenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    TrackRecompositions("CurrencyListItemComposable", viewData.code)

    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember(viewData.amountStr) {
        mutableStateOf(TextFieldValue(text = viewData.amountStr))
    }

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
            contentDescription = "",
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

        TextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                if (isFocused && newValue.text != viewData.amountStr && newValue.text.isNotBlank()) {
                    onIntent(
                        ConverterScreenIntent.CurrencyOrAmountChanged(
                            CurrentCurrencyRate(
                                code = viewData.code,
                                amountStr = newValue.text,
                                cursorPosition = newValue.selection.end
                            )
                        )
                    )
                }
            },
            placeholder = { Text(text = "0") },
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
                        onIntent(
                            ConverterScreenIntent.CurrencyOrAmountChanged(
                                CurrentCurrencyRate(
                                    code = viewData.code,
                                    amountStr = textFieldValue.text,
                                    cursorPosition = textFieldValue.selection.end,
                                )
                            )
                        )
                    }
                }
        )
        if (viewData.cursorPosition != null) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
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
private fun NetworkErrorComposable(onIntent: (ConverterScreenIntent) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = AppStrings.ERROR_NETWORK_TITLE,
            fontSize = 16.sp,
        )
        Button(
            onClick = {
                onIntent(ConverterScreenIntent.RetryLoading)
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(
                text = AppStrings.ERROR_NETWORK_RETRY,
            )
        }
    }
}

@Preview
@Composable
fun ConverterScreenPreview() {
    ConverterTheme(isNightTheme = true) {
        // ConverterTheme(isNightTheme = false) {
        ConverterScreen(
            navController = rememberNavController(),
            // state = ConverterScreenState.Loaded(
            //     listOf(
            //         CurrencyRateViewData("RUB", "900.50"),
            //         CurrencyRateViewData("USD", "1"),
            //     )
            // ),
            state = ConverterScreenState.Error,
            effectFlow = emptyFlow(),
            onIntent = {},
        )
    }
}