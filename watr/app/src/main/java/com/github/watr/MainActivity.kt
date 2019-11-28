package com.github.watr

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.github.watr.fragments.Dehydration_Track
import com.github.watr.fragments.LogTab
import com.github.watr.fragments.Track
import com.github.watr.fragments.UrineTab
import com.github.watr.helpers.GraphHelper
import com.github.watr.helpers.dateSubtract
import com.github.watr.model.SharedViewModel
import com.github.watr.pager.CustomViewPager
import com.github.watr.pager.viewPagerAdapter
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: SharedViewModel
    private lateinit var mViewPager: CustomViewPager
    private lateinit var adapter : viewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = this.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val tdee = sharedPref.getInt("tdee",-1)
        if(tdee == -1){
          startActivity(Intent(this@MainActivity, SetupMain::class.java))
        } else {
            setContentView(R.layout.activity_main)

            if(false){
                val editor = sharedPref.edit()
                editor.clear()      //it clears all data.
                editor.commit()
            }

            val mTabLayout : TabLayout = findViewById(R.id.mainTabs)
            mViewPager = findViewById(R.id.viewPager)
            setupViewPager(mViewPager)
            mTabLayout.setupWithViewPager(mViewPager)
            mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if(tab.text!!.equals("Log")) {
                        enableSwipe()
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            });
        }
    }

    fun disableSwipe(){
        mViewPager.setPagingEnabled(false)
    }

    fun enableSwipe(){
        mViewPager.setPagingEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_settings -> {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            return true
         }
            R.id.action_help -> {
                startActivity(Intent(this@MainActivity, Help::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewPager(viewPager: ViewPager){
        adapter = viewPagerAdapter(supportFragmentManager)
        adapter.addFragment(LogTab(), "Log")
        adapter.addFragment(Track(), "Track")
        adapter.addFragment(UrineTab(), "Dehydration Table")
      //  adapter.addFragment(Dehydration_Track(), "Dehydration Track")
        viewPager.adapter = adapter
    }

    fun sumCheck(): Boolean{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences.getBoolean(("sum"), true)
    }


    fun saveDay(cals : Int) {
        var currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sharedPref = this.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gh = GraphHelper()

        val prefMng = PreferenceManager.getDefaultSharedPreferences(this)
        val subDate = prefMng.getBoolean(("owl"), true)
        val date = Date()
        val ampm = SimpleDateFormat("a").format(date)
        val hour = SimpleDateFormat("K").format(date)
        if(subDate && ampm == "AM" && hour == "4"){
            val subHelp = dateSubtract()
            currentDate = subHelp.subDate()
        }

        var graphStrings = gh.loadGraphData(sharedPref)

        if (currentDate.equals(gh.getMostRecentSaveDate(sharedPref))) {

            var obj = JSONObject(graphStrings[graphStrings.size - 1])
            if(sumCheck()) {
                obj.put("calories", obj.getInt("calories") + cals)
            }else{
                obj.put("calories", cals)
            }
            graphStrings[graphStrings.size - 1] = obj.toString()

        } else {
            val rootObject = JSONObject()
            rootObject.put("date", currentDate)
            rootObject.put("calories", cals)
            graphStrings.add(rootObject.toString())
        }

        var jsonArray = JSONArray(graphStrings)
        editor.putString("graphData", jsonArray.toString())
        editor.commit()

    }


}

