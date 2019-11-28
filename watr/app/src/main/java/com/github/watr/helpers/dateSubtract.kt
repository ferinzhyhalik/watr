package com.github.watr.helpers

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class dateSubtract{
    fun subDate():String{
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val format = SimpleDateFormat("yyyy-MM-dd")
        val date = format.parse(currentDate)
        val cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.DATE, -1)
        Log.d("daterrrrr",format.format(cal.time))
        return format.format(cal.time)
    }
}