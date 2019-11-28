package com.github.watr.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import android.graphics.Color
import android.graphics.DashPathEffect
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.github.watr.*
import com.github.watr.helpers.DateValueFormatter
import com.github.watr.helpers.GraphHelper
import com.github.watr.model.SharedViewModel
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.highlight.Highlight
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class Track : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var mChart : LineChart
    private lateinit var marker : CustomMarkerView
    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        if (visible && isResumed) {
            initGraph(mChart,marker)
            mChart.notifyDataSetChanged()
            mChart.invalidate()

            val activity = activity as MainActivity?
            activity!!.disableSwipe()

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
            R.layout.fragment_track,
            container, false
        )

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        mChart  = view!!.findViewById(R.id.chart)
        marker = CustomMarkerView(view!!.context, R.layout.graphclick)
        initGraph(mChart,marker)
        return view
    }

    fun initGraph(mChart : LineChart,marker: CustomMarkerView){
        setupGraph(loadGraphData(),mChart,marker)
    }


    override fun onResume() {
        super.onResume()
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        if(sharedPref.getBoolean("refresh",false)) {
            val editor = sharedPref.edit()
            editor.putBoolean("refresh", false)
            editor.commit()
            getFragmentManager()!!.beginTransaction().detach(this).attach(this).commit()
        }
    }


    private fun loadGraphData():ArrayList<Entry>{
        val sharedPref = activity!!.getSharedPreferences("com.amooose.Calorie_Center", Context.MODE_PRIVATE)
        val gh = GraphHelper()
        var graphStrings = gh.loadGraphData(sharedPref)
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val values = ArrayList<Entry>()

        if(gh.isEmpty(sharedPref)){
            mChart.clear()

        }

        if(graphStrings.size==0){
            return values
        }

        for (i in graphStrings.indices) {
            val string = graphStrings[i]
            var obj = JSONObject(string)
            var date = df.parse(obj.getString("date"))
            values.add(Entry(date.time.toFloat(), obj.getInt("calories").toFloat()))
        }

        return values
    }

    private fun setupGraph(values: ArrayList<Entry>, mChart: LineChart, marker: CustomMarkerView){
        if(values.size == 0){
            return
        }
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        val calSet: LineDataSet
        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            calSet = mChart.data.getDataSetByIndex(0) as LineDataSet
            calSet.values = values
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            calSet = LineDataSet(values, "Calorie Data")
            calSet.enableDashedLine(10f, 5f, 0f)
            calSet.enableDashedHighlightLine(10f, 5f, 0f)
            calSet.color = Color.DKGRAY
            calSet.setCircleColor(Color.DKGRAY)
            calSet.lineWidth = 1f
            calSet.circleRadius = 3f
            calSet.setDrawCircleHole(false)
            calSet.valueTextSize = 9f
            calSet.setDrawFilled(true)
            calSet.formLineWidth = 1f
            calSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            calSet.formSize = 15f
            calSet.fillColor = Color.BLUE
            mChart.getDescription().setEnabled(false);
            var dataSets = java.util.ArrayList<ILineDataSet>()
            dataSets.add(calSet)
            val data = LineData(dataSets)
            mChart.data = data
        }

        val xAxis = mChart.getXAxis()
        if(values.size<=1) {
            xAxis.isEnabled = false
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setValueFormatter(DateValueFormatter())

        marker.setOffset(-130f,20f)
        mChart.animateXY(100,1000)
        mChart.marker = marker
    }

    inner class CustomMarkerView(context: Context, layoutResource: Int) :
        MarkerView(context, layoutResource) {

        private val tvContent: TextView

       override fun draw(canvas: Canvas, posX: Float, posY: Float) {
            val display= DisplayMetrics()
            activity!!.getWindowManager().getDefaultDisplay().getMetrics(display);
           val width = display.widthPixels
           val stop = width*.82
           val offset = getOffsetForDrawingAtPoint(posX, posY)

            val saveId = canvas.save()
            // translate to the correct position and draw
           var xFix = posX
           if(xFix>stop){
               xFix=stop.toFloat()
           }
            canvas.translate(xFix + offset.x, posY + offset.y)
            draw(canvas)
            canvas.restoreToCount(saveId)
        }

        val yOffset: Int
            get() = -height

        init {
            tvContent = findViewById(R.id.tvContent) as TextView
        }

        override fun refreshContent(e: Entry, highlight: Highlight) {
            val df = SimpleDateFormat("MM-dd-yy", Locale.ENGLISH)
            df.setTimeZone(TimeZone.getTimeZone("GMT"))
            var localtime = df.format(Date(e.x.toLong()))
            tvContent.text = " Date: " + localtime+"\n Calories: "+e.y.toInt() // set the entry-value as the display text
        }
    }


}




