package com.github.watr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference
import android.content.DialogInterface
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.text.InputFilter
import android.text.InputType
import com.github.watr.helpers.InputFilterMinMax


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    fun showEditDialog(){
        val builder = AlertDialog.Builder(this)
        var edittext : EditText = EditText(this)
        edittext.inputType = InputType.TYPE_CLASS_NUMBER
        edittext.setFilters(arrayOf<InputFilter>(
            InputFilterMinMax("1", "9999")
        ))
        builder.setTitle("Enter Your Custom TDEE");
        builder.setView(edittext)
        val alert = builder.create()

        alert.setButton(AlertDialog.BUTTON_POSITIVE,"Enter",
            DialogInterface.OnClickListener { dialog, whichButton ->
                //What ever you want to do with the value
                val sharedPref = this.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("tdee", edittext.text.toString().toInt())
                editor.commit()
                Toast.makeText(this, "Custom TDEE set successfully.", Toast.LENGTH_SHORT).show()
            })

        alert.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancel",
            DialogInterface.OnClickListener { dialog, whichButton ->
                alert.dismiss()
            })



        alert.show()
    }


    fun setNeedsRefresh(){
        val sharedPref = this.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("refresh", true)
        editor.commit()
    }
    fun showDialog(){
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val sharedPref = this.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("graphData", "[]")
                    editor.commit()
                    Toast.makeText(this, "Cleared all tracking/graph data", Toast.LENGTH_SHORT).show()

                }

                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val setupPref  = findPreference("setup") as Preference?
            setupPref!!.setOnPreferenceClickListener {
                startActivity(Intent(context, SetupMain::class.java))
                false
            }

            val graphClearPref  = findPreference("clearGraph") as Preference?
            graphClearPref!!.setOnPreferenceClickListener {
                val activity = activity as SettingsActivity?
                activity!!.showDialog()
                activity!!.setNeedsRefresh()
                false
            }

            val customTDEE  = findPreference("custom") as Preference?
            customTDEE!!.setOnPreferenceClickListener {
                val activity = activity as SettingsActivity?
                activity!!.showEditDialog()
                false
            }



        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> finish()
        }
        return true
    }
}