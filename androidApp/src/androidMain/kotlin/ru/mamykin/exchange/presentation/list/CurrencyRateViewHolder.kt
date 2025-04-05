// package ru.mamykin.exchange.presentation.list
//
// import android.text.TextWatcher
// import androidx.annotation.DrawableRes
// import androidx.core.widget.addTextChangedListener
// import androidx.recyclerview.widget.RecyclerView
// import com.squareup.picasso.Picasso
// import ru.mamykin.exchange.databinding.ItemCurrencyRateBinding
// import ru.mamykin.exchange.presentation.AndroidCurrencyRateViewData
// import ru.mamykin.exchange.presentation.CurrentCurrencyRate
//
// internal class CurrencyRateViewHolder(
//     private val binding: ItemCurrencyRateBinding,
// ) : RecyclerView.ViewHolder(binding.root) {
//
//     private var amountTextWatcher: TextWatcher? = null
//
//     fun bind(
//         viewData: AndroidCurrencyRateViewData,
//         onCurrencyOrAmountChanged: (currentCurrency: CurrentCurrencyRate) -> Unit,
//     ) {
//         binding.textCurrencyCode.text = viewData.code
//         bindAmount(viewData)
//         bindCurrencyIcon(viewData.icon)
//         subscribeToEvents(viewData, onCurrencyOrAmountChanged)
//     }
//
//     fun unbind() {
//         binding.editExchangeAmount.onFocusChangeListener = null
//         binding.editExchangeAmount.removeTextChangedListener(amountTextWatcher)
//         amountTextWatcher = null
//     }
//
//     private fun bindAmount(viewData: AndroidCurrencyRateViewData) = binding.apply {
//         binding.editExchangeAmount.setText(viewData.amountStr)
//         viewData.selectionPosition?.let(binding.editExchangeAmount::setSelection)
//     }
//
//     private fun subscribeToEvents(
//         viewData: AndroidCurrencyRateViewData,
//         onCurrencyOrAmountChanged: (currentCurrency: CurrentCurrencyRate) -> Unit,
//     ) {
//         binding.editExchangeAmount.setOnFocusChangeListener { _, focused ->
//             if (focused) {
//                 onCurrencyOrAmountChanged(
//                     CurrentCurrencyRate(
//                         code = viewData.code,
//                         amountStr = viewData.amountStr,
//                     )
//                 )
//             }
//         }
//         amountTextWatcher = binding.editExchangeAmount.addTextChangedListener {
//             val newText = it.toString()
//             if (binding.editExchangeAmount.isFocused && newText.isNotBlank()) {
//                 onCurrencyOrAmountChanged(
//                     CurrentCurrencyRate(
//                         code = viewData.code,
//                         amountStr = newText,
//                         selectionPosition = binding.editExchangeAmount.selectionEnd
//                     )
//                 )
//             }
//         }
//     }
//
//     private fun bindCurrencyIcon(@DrawableRes icon: Int) = binding.apply {
//         Picasso.get()
//             .load(icon)
//             .resize(100, 100)
//             .centerInside()
//             .into(imageCurrencyIcon)
//     }
// }