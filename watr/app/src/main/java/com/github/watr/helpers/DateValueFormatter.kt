package com.github.watr.helpers

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*


class DateValueFormatter : IAxisValueFormatter {
    override fun getDecimalDigits(): Int {
        return 0
    }

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val df = SimpleDateFormat("MM-dd-yy", Locale.ENGLISH)
        var localtime = df.format(Date(value.toLong()))
        return localtime
    }
}