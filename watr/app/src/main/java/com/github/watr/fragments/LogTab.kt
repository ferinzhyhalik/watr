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
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import com.github.watr.helpers.InputFilterMinMax
import com.github.watr.MainActivity
import com.github.watr.R
import com.github.watr.model.SharedViewModel


/**
 * A simple [Fragment] subclass.
 */
class LogTab : Fragment() {
    lateinit var calLogList : ListView
    lateinit var calorieText : EditText
    lateinit var foodText : EditText
    lateinit var calsLeftText : TextView
    lateinit var progressB : ProgressBar
    var tdee : Int = 0
    val arrayStrings = ArrayList<String>()
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
            R.layout.fragment_log,
            container, false
        )
        //Future implement
        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        tdee = sharedPref.getInt("tdee",-1)

        calLogList = view.findViewById(R.id.calLogList)
        calLogList.adapter = ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, arrayStrings)

        foodText = view.findViewById(R.id.foodText)
        foodText.setInputType(InputType.TYPE_CLASS_TEXT)

        calorieText = view.findViewById(R.id.calorieText)
        calorieText.setFilters(arrayOf<InputFilter>(InputFilterMinMax("1", "5000")))
        calorieText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCalories()
            }
            false
        }

        progressB = view.findViewById(R.id.calProgress)
        calsLeftText = view.findViewById(R.id.calsLeft)

        loadSavedCalories()
        updateConsumedCals(-1)

        val icon : ImageView = view.findViewById(R.id.logo)
        icon.setOnLongClickListener {
            val animY = ObjectAnimator.ofFloat(icon, "scaleY", .5f, 1f)
            animY.duration = 500//1sec
            animY.interpolator = BounceInterpolator()
            animY.repeatCount = 0
            animY.start()
            val activity = activity as MainActivity?
            activity!!.saveDay(getConsumedCals())
            resetAnim()
            true}

        val rollButton: Button = view.findViewById(R.id.calorieAddBtn)
        rollButton.setOnClickListener { addCalories() }
        setListLongClickListener()

        return view
    }

    // Gets current consumed calories
    private fun getConsumedCals(): Int{
        var cals = 0
        for (i in arrayStrings.indices) {
            cals += getCalAmount(i)
        }
        return cals
    }

    // Updates the view of Cals Left and updates the progress bar.
     private fun updateConsumedCals(cals: Int = -1): Int{
        var calories = cals
        if(calories == -1) {
            calories = tdee-(getConsumedCals())
        }
        val consumed = getConsumedCals()
        calsLeftText.setText("How much water you need to drink: "+(calories) + " ml")
        val percent = 100-((consumed / tdee.toDouble()) * 100.0).toInt()
        ObjectAnimator.ofInt(progressB, "progress", percent).setDuration(300).start()
        return cals
    }

    // Inits recycler's longclick listener (For removing calories)
    private fun setListLongClickListener(){
        calLogList.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, pos, _ ->
            Toast.makeText(view!!.context, "Removed "+getCalAmount(pos)+" calories", Toast.LENGTH_SHORT).show()
            removeAnim(getCalAmount(pos))
            arrayStrings.removeAt(pos)
            updateListView()
            saveCalories()
            true
        }
    }

    // Resets calLogList
    private fun resetCalLogList(){
        arrayStrings.clear()
        calLogList.adapter = ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, arrayStrings)
    }

    // Displays a reset animation (Cals left text and progress bar)
    private fun resetAnim(){
        val cals = getConsumedCals()

        for (a in 1..cals) {
            val handler1 = Handler()
            handler1.postDelayed(object : Runnable {
                override fun run() {
                    updateConsumedCals((tdee-cals)+a)
                }
            }, 20+a.toLong())
            if(a == cals){
                resetCalLogList()
                saveCalories()
            }
        }
    }

    // Displays a removing calories animation (Cals left text and progress bar)
    private fun removeAnim(remove: Int){
        val cals = remove
        val tot = tdee-getConsumedCals()
        for (a in 1..cals) {
            val handler1 = Handler()
            handler1.postDelayed(object : Runnable {
                override fun run() {
                    updateConsumedCals((tot)+a)
                }
            }, 20+a.toLong())
        }
    }

    // Displays an adding calories animation (Cals left text and progress bar)
    private fun addAnim(added: String){
        val prevCals = getConsumedCals()

        val cals = added.toInt()
        val total = tdee-prevCals
        for (a in 1..cals) {
            val handler1 = Handler()
            handler1.postDelayed(object : Runnable {
                override fun run() {
                    updateConsumedCals((total)-a)
                }
            }, 20+a.toLong())
        }

    }

    // Saves current calLog's list to SharedPreferences
    fun saveCalories(){
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val jsonArray = JSONArray(arrayStrings)
        val editor = sharedPref.edit()
        editor.putString("CalList", jsonArray.toString())
        editor.commit()
    }

    // Loads calLog's saved entries
    private fun loadSavedCalories(){
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val fetch = sharedPref.getString("CalList", "[]")
        val jsonArray = JSONArray(fetch)

        if(!fetch.equals("[]")) {
            for (i in 0 until jsonArray.length()) {
                arrayStrings.add(jsonArray.get(i).toString())
            }
        }
    }

    // Gets calorie amount at a position within the calLog recycler
    private fun getCalAmount(pos: Int): Int{
        return arrayStrings[pos].substring(arrayStrings[pos].indexOf('-')+2,arrayStrings[pos].length).toInt()
    }

    // Refreshes calLog
    private fun updateListView(){
        calLogList.adapter =
            ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, arrayStrings)
        calLogList.setSelection(arrayStrings.size - 1)
    }

    // Hides visible keyboard
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    // Adds and saves entered calories
    private fun addCalories(){
        val date = Date()
        val formatter = SimpleDateFormat("K:mma")
        var time = ""
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val isChecked = sharedPreferences.getBoolean(("time"), true)
        if(isChecked){
            time ="["+formatter.format(date).toLowerCase()+"] "
        }
        view!!.hideKeyboard()
        if(calorieText.length()>0) {
            val text = calorieText.text.toString()
            var food = foodText.text.toString()

            // '-' is our delimiter, remove it to avoid issues.
            if(food.contains("-")){
               food = food.replace("-","")
            }
            addAnim(text)
            arrayStrings.add(time+food+" - "+calorieText.text.toString())
            foodText.text.clear()
            calorieText.text.clear()
            updateListView()

        }
        saveCalories()
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        if(sharedPref.getInt("tdee",-1) != tdee){
            tdee=sharedPref.getInt("tdee",-1)
            updateConsumedCals()
        }
    }


}
