package com.github.watr.fragments


import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.TextView
import android.view.inputmethod.EditorInfo
import org.json.JSONArray
import android.view.animation.BounceInterpolator
import android.animation.ObjectAnimator
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import com.github.watr.helpers.InputFilterMinMax
import com.github.watr.MainActivity
import com.github.watr.R
import com.github.watr.model.UrineSharedViewModel


/**
 * A simple [Fragment] subclass.
 */
class UrineTab : Fragment() {
    lateinit var urineLogList : ListView
    lateinit var urineText : EditText
    lateinit var HydrationLevelText:TextView

    var tdee : Int = 0
    val urine_arrayStrings = ArrayList<String>()
    private lateinit var viewModel: UrineSharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
            R.layout.urine_log,
            container, false
        )
        //Future implement
        viewModel = activity?.run {
            ViewModelProviders.of(this)[UrineSharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        val sharedUrinePref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        tdee = sharedUrinePref.getInt("tdee",-1)

        urineLogList = view.findViewById(R.id.urineLogList)
        urineLogList.adapter = ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, urine_arrayStrings)

        urineText = view.findViewById(R.id.urineText)
        urineText.setFilters(arrayOf<InputFilter>(InputFilterMinMax("1", "5000")))
        urineText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

            }
            false
        }


        loadSavedUrine()
       // updateConsumedCals(-1)
        val radio_group : RadioGroup = view.findViewById(R.id.radio_group)
        radio_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = view.findViewById(checkedId)
                Toast.makeText(context," On checked change :"+
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()
            })
        val submitUrine : Button = view.findViewById(R.id.submitUrine)
        submitUrine.setOnClickListener{
            // Get the checked radio button id from radio group
            var id: Int = radio_group.checkedRadioButtonId
            if (id!=-1){ // If any radio button checked from radio group
                // Get the instance of radio button using id

                val radio:RadioButton = view.findViewById(id)
                val radioButtonCount = radio_group.childCount
                Toast.makeText(context,"On button click :" +
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()
                val urineNum = "${radio.text}".toIntOrNull()
                addUrine(urineNum)
            }else{
                // If no radio button checked in this radio group
                Toast.makeText(context,"On button click :" +
                        " nothing selected",
                    Toast.LENGTH_SHORT).show()
            }
        }
        val icon : ImageView = view.findViewById(R.id.logo)
        icon.setOnLongClickListener {
            val animY = ObjectAnimator.ofFloat(icon, "scaleY", .5f, 1f)
            animY.duration = 500//1sec
            animY.interpolator = BounceInterpolator()
            animY.repeatCount = 0
            animY.start()
            val activity = activity as MainActivity?
            activity!!.saveDay(getUrine())
            //resetAnim()
            true}

        val urineRollButton: Button = view.findViewById(R.id.urineAddBtn)
        urineRollButton.setOnClickListener { }
        setListLongClickListener()

        return view
    }

    // Gets current consumed calories
    private fun getUrine(): Int{
        var cals = 0
        for (i in urine_arrayStrings.indices) {
            cals += getUrineColor1(i)
        }
        return cals
    }

    // Updates the view of Cals Left and updates the progress bar.


    // Inits recycler's longclick listener (For removing calories)
    private fun setListLongClickListener(){
      urineLogList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, pos, _ ->
            Toast.makeText(view!!.context, "Removed "+getUrineColor1(pos)+" calories", Toast.LENGTH_SHORT).show()
            urine_arrayStrings.removeAt(pos)
            updateUrineListView()
            saveUrine()
            true
        }
    }

    private fun updateUrine(cals: Int = -1): Int{
        var calories = cals
        if(calories == -1) {
            calories = tdee-(getUrine())
        }
        val consumed = getUrine()
        HydrationLevelText.setText("How much water you need to drink: "+(calories))
        return cals
    }

    private fun resetAnim(){
        val cals = getUrine()

        for (a in 1..cals) {
            val handler1 = Handler()
            handler1.postDelayed(object : Runnable {
                override fun run() {
                    updateUrine((tdee-cals)+a)
                }
            }, 20+a.toLong())
            if(a == cals){
                resetCalLogList()
                saveUrine()
            }
        }
    }

    // Resets calLogList
    private fun resetCalLogList(){
        urine_arrayStrings.clear()
       urineLogList.adapter = ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, urine_arrayStrings)
    }



    // Displays an adding calories animation (Cals left text and progress bar)


    // Saves current calLog's list to SharedPreferences
    fun saveUrine(){
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val jsonArray = JSONArray(urine_arrayStrings)
        val editor = sharedPref.edit()
        editor.putString("UrineList", jsonArray.toString())
        editor.commit()
    }


    // Loads calLog's saved entries
    private fun loadSavedUrine(){
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val fetch = sharedPref.getString("UrineList", "[]")
        val jsonArray = JSONArray(fetch)

        if(!fetch.equals("[]")) {
            for (i in 0 until jsonArray.length()) {
                urine_arrayStrings.add(jsonArray.get(i).toString())
            }
        }
    }

    // Gets calorie amount at a position within the calLog recycler
    private fun getUrineColor1(pos: Int): Int{
        return urine_arrayStrings[pos].substring(urine_arrayStrings[pos].indexOf('-')+2,urine_arrayStrings[pos].length).toInt()
    }

    // Refreshes calLog
    private fun updateUrineListView(){
        urineLogList.adapter =
            ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, urine_arrayStrings)
       urineLogList.setSelection(urine_arrayStrings.size - 1)
    }

    // Hides visible keyboard
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    // Adds and saves entered calories
    private fun addUrine(urine_string : Int?){
        val date = Date()
        val formatter = SimpleDateFormat("K:mma")
        var time = ""
        var str:String = urine_string.toString()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val isChecked = sharedPreferences.getBoolean(("time"), true)
        if(isChecked){
            time ="["+formatter.format(date).toLowerCase()+"] "
        }
        view!!.hideKeyboard()

            urine_arrayStrings.add(time+" - "+ str)
            urineText.text.clear()
            updateUrineListView()
            saveUrine()

        // saveUrine()
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        if(sharedPref.getInt("tdee",-1) != tdee){
            tdee=sharedPref.getInt("tdee",-1)
           // updateConsumedCals()
        }
    }


}
