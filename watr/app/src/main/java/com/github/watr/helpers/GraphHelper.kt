package com.github.watr.helpers

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class GraphHelper {


     fun getMostRecentSaveDate(sharedPref: SharedPreferences): String{
        val fetch = sharedPref.getString("graphData", "[]")
         if(fetch.equals("[]")){
             return ""
         }
        var jsonArray = JSONArray(fetch)
        var obj : JSONObject = JSONObject(jsonArray[jsonArray.length()-1].toString())

        return obj.getString("date")
    }

    fun loadGraphData(sharedPref: SharedPreferences):  ArrayList<String>{
        var graphStrings = ArrayList<String>()
        val fetch = sharedPref.getString("graphData", "[]")
        if(!fetch.equals("[]")) {
            var jsonArray = JSONArray(fetch)
            for (i in 0 until jsonArray.length()) {
                graphStrings.add(jsonArray.get(i).toString())
            }
        }
        return graphStrings
    }

    fun isEmpty(sharedPref: SharedPreferences):Boolean{
        val fetch = sharedPref.getString("graphData", "[]")
        return fetch.equals("[]")
    }


}