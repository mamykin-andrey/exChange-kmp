// package ru.mamykin.exchange.presentation.list
//
// import androidx.recyclerview.widget.DiffUtil
// import ru.mamykin.exchange.presentation.AndroidCurrencyRateViewData
//
// internal class CurrencyRatesDiffUtilCallback : DiffUtil.ItemCallback<AndroidCurrencyRateViewData>() {
//
//     override fun areItemsTheSame(oldItem: AndroidCurrencyRateViewData, newItem: AndroidCurrencyRateViewData): Boolean {
//         return oldItem.code == newItem.code
//     }
//
//     override fun areContentsTheSame(
//         oldItem: AndroidCurrencyRateViewData,
//         newItem: AndroidCurrencyRateViewData
//     ): Boolean {
//         return oldItem == newItem
//     }
// }