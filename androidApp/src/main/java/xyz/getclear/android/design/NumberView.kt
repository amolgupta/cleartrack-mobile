package xyz.getclear.android.design

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.math.BigDecimal
import java.text.NumberFormat.getCurrencyInstance

class NumberView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) :
    AppCompatTextView(context, attrs, defStyle) {

    fun setNumber(input: BigDecimal) {
        text = getCurrencyInstance().format(input).clip()
    }

    fun setNumber(input: Float) {
        text = getCurrencyInstance().format(input).clip()
    }

    fun setNumber(input: Float, currency: String) {
        setNumber(input)
        text = "$text $currency"
    }

    fun setNumber(input: BigDecimal, currency: String) {
        setNumber(input)
        text = "$text $currency"
    }

    private fun String.clip(): String {
        return replace("[^0123456789.,()-]".toRegex(), "")
    }
}
