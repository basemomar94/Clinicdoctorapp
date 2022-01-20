package com.bassem.clinicdoctorapp.statics

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.StaticsFragmentBinding
import com.bassem.clinicdoctorapp.schedule.history.Visits
import com.google.firebase.firestore.*
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.BarModel
import org.eazegraph.lib.models.PieModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Statics : Fragment(R.layout.statics_fragment) {
    var _binding: StaticsFragmentBinding? = null
    val binding get() = _binding
    lateinit var db: FirebaseFirestore
    private lateinit var barChart: AnyChartView
    lateinit var compelteList: ArrayList<Visits>
    var item1: Float? = null
    var item2: Float? = null
    var item3: Float? = null
    var item4: Float? = null
    var item5: Float? = null
    var item6: Float? = null
    var item7: Float? = null
    var newBooking:Boolean=false
    lateinit var todayGlobal: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StaticsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingAllVisits("status", "completed")
        binding?.filterRadio?.setOnCheckedChangeListener { group, i ->
            val selected = binding!!.filterRadio.checkedRadioButtonId
            if (selected != -1) {
                when (view.findViewById<RadioButton>(selected).text.toString()) {
                    "complete visits" -> {
                        println("1")
                        GettingAllVisits("status", "completed")
                    }
                    "new booking" -> {
                        newBooking=true
                        GettingAllVisits("status", "Pending")
                        println("New Booking=============")
                    }
                }


            }


        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun GettingAllVisits(filed: String, value: String) {
        compelteList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        var d1: Int = 0
        db.collection("visits").whereEqualTo(filed, value)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                } else {
                    Thread(Runnable {

                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                compelteList.add(dc.document.toObject(Visits::class.java))
                            }
                        }

                        activity?.runOnUiThread {
                            println(compelteList.size)
                            if (compelteList.isNotEmpty()) {
                                FilterVisits()
                            }

                        }


                    }).start()
                }
            }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun FilterVisits() {
        val local = Locale.US
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", local)
        val today = LocalDate.now()
        val day7 = sdf.format(today)
        todayGlobal = day7
        val day6 = sdf.format(today.minusDays(1))
        val day5 = sdf.format(today.minusDays(2))
        val day4 = sdf.format(today.minusDays(3))
        val day3 = sdf.format(today.minusDays(4))
        val day2 = sdf.format(today.minusDays(5))
        val day1 = sdf.format(today.minusDays(6))
        val day1List: ArrayList<Visits> = arrayListOf()
        val day2List: ArrayList<Visits> = arrayListOf()
        val day3List: ArrayList<Visits> = arrayListOf()
        val day4List: ArrayList<Visits> = arrayListOf()
        val day5List: ArrayList<Visits> = arrayListOf()
        val day6List: ArrayList<Visits> = arrayListOf()
        val day7List: ArrayList<Visits> = arrayListOf()
        Thread(Runnable {
            for (visit: Visits in compelteList) {
                if (newBooking){
                    when(visit.booking_date){
                        day1 -> {
                            day1List.add(visit)
                        }
                        day2 -> {
                            day2List.add(visit)
                        }
                        day3 -> {
                            day3List.add(visit)
                        }
                        day4 -> {
                            day4List.add(visit)
                        }
                        day5 -> {
                            day5List.add(visit)
                        }
                        day6 -> {
                            day6List.add(visit)
                        }
                        day7 -> {
                            day7List.add(visit)
                        }
                    }
                } else {  when (visit.date) {
                    day1 -> {
                        day1List.add(visit)
                    }
                    day2 -> {
                        day2List.add(visit)
                    }
                    day3 -> {
                        day3List.add(visit)
                    }
                    day4 -> {
                        day4List.add(visit)
                    }
                    day5 -> {
                        day5List.add(visit)
                    }
                    day6 -> {
                        day6List.add(visit)
                    }
                    day7 -> {
                        day7List.add(visit)
                    }


                }}



            }
            val item1 = day1List.size
            val item2 = day2List.size
            val item3 = day3List.size
            val item4 = day4List.size
            val item5 = day5List.size
            val item6 = day6List.size
            val item7 = day7List.size
            activity!!.runOnUiThread {
                setupChart(
                    item7,
                    item6,
                    item5,
                    item4,
                    item3,
                    item2,
                    item1
                )
                newBooking=false
            }
        }).start()


    }

    fun setupChart2(
        item1: Int,
        item2: Int,
        item3: Int,
        item4: Int,
        item5: Int,
        item6: Int,
        item7: Int,
        day1: String,
        day2: String,
        day3: String,
        day4: String,
        day5: String,
        day6: String,
        day7: String,

        ) {


        val bar = AnyChart.bar()
        val data: MutableList<DataEntry> = arrayListOf()
        data.add(ValueDataEntry(day1, item1))
        data.add(ValueDataEntry(day2, item2))
        data.add(ValueDataEntry(day3, item3))
        data.add(ValueDataEntry(day4, item4))
        data.add(ValueDataEntry(day5, item5))
        data.add(ValueDataEntry(day6, item6))
        data.add(ValueDataEntry(day7, item7))
        bar.data(data)
        barChart.setChart(bar)


    }

    fun setupChart(
        value1: Int,
        value2: Int,
        value3: Int,
        value4: Int,
        value5: Int,
        value6: Int,
        value7: Int,
    ) {

        var barChart = view?.findViewById<BarChart>(R.id.barchart)
        barChart!!.addBar(BarModel(value1.toFloat(), Color.parseColor("#FF0000")))
        barChart!!.addBar(BarModel(value2.toFloat(), Color.parseColor("#00FF00")))
        barChart!!.addBar(BarModel(value3.toFloat(), Color.parseColor("#FFFF00")))
        barChart!!.addBar(BarModel(value4.toFloat(), Color.parseColor("#FF0000")))
        barChart!!.addBar(BarModel(value5.toFloat(), Color.parseColor("#808080")))
        barChart!!.addBar(BarModel(value6.toFloat(), Color.parseColor("#008080")))
        barChart!!.addBar(BarModel(value7.toFloat(), Color.parseColor("#800080")))



        barChart.startAnimation()


    }


}