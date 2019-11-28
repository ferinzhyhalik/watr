package com.github.watr.helpers

import android.content.SharedPreferences
import kotlin.math.roundToInt

class calcTDEE  {

    fun getTDEE(info: SharedPreferences) : Int {

        val bodyfat = info.getInt("bodyfat",-1)
        val sex = info.getBoolean("sex",false)
        val weight = info.getInt("weight",-1)
        val ft = info.getInt("ft",-1)
        val inches = info.getInt("inches",-1)
        val age = info.getInt("age",-1)
        val activity = info.getInt("activity",-1)
        val aMult = 1.2+(.175*activity)
        var tdee = 0.0
        val sVar = if (sex) 5 else -161

        if(bodyfat>0){ // Katch-McArdle Equation
            val LBM_kg = (weight-(weight*(bodyfat*.01)))*0.45359237
            tdee = 370 + (21.6 * LBM_kg)
            tdee *= aMult
        } else{ // Mifflin-St Jeor Equation
            tdee = 10*(weight*0.45359237)+6.25*((ft*30.48)+(inches*2.54))-5*(age)+sVar
            tdee*= aMult
        }
        return tdee.roundToInt()
    }


}