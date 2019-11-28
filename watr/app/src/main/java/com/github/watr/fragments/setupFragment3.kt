package com.github.watr.fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.graphics.Color
import com.github.watr.*
import com.github.watr.helpers.calcTDEE
import com.github.watr.helpers.spruceHelper


/**
 * A simple [Fragment] subclass.
 */
class setupFragment3 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
            R.layout.fragment_setup_fragment3,
            container, false
        )

        val animview : ViewGroup = view.findViewById(R.id.innerSetup3Layout)
        spruceHelper().animateSlideInRight(animview)

        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)

        val finishBtn: Button = view.findViewById(R.id.finishSetupBtn)
        finishBtn.setOnClickListener{
            calculate(sharedPref)
            val activity = activity as SetupMain?
            activity!!.closeSetupMain()
        }
        val helpBtn: Button = view.findViewById(R.id.helpBtn)
        helpBtn.setOnClickListener{
            calculate(sharedPref)
            val activity = activity as SetupMain?
            startActivity(Intent(activity, Help::class.java))
        }
        val stats: TextView = view.findViewById(R.id.infoText)
        val tdee = calculate(sharedPref)
        var formulaUsed : String
        if(sharedPref.getInt("bodyfat",-1) == -1){
            formulaUsed = "Mifflin-St Jeor Equation (No BF%)"
        } else{
            formulaUsed = "Katch-McArdle Equation"
        }
        val text2 = ("TDEE: " + tdee + "\nFormula used:\n"+formulaUsed)


        stats.setText("\nTDEE: ")
        stats.append(getColoredString(getActivity()!!.getApplicationContext(), tdee.toString(), Color.rgb(44, 163, 46)))
        stats.append("\n\nFormula used:\n"+formulaUsed+"\n")
        return view
    }

    fun getColoredString(context: Context, text: CharSequence, color: Int): Spannable {
        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(color),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    fun calculate(sharedPref: SharedPreferences) : Int{
        val tdee = calcTDEE().getTDEE(sharedPref)
        val editor = sharedPref.edit()
        editor.putInt("tdee", tdee)
        editor.commit()
        return tdee;
    }

}
