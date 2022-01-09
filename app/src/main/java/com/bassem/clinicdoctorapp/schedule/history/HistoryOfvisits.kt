package com.bassem.clinicdoctorapp.schedule.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.HistoryVisitsFragmentBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList

class HistoryOfvisits : Fragment(R.layout.history_visits_fragment),HistoryAdapter.Myclicklisener {
    var _binding: HistoryVisitsFragmentBinding? = null
    val binding get() = _binding
    var selected_date: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: HistoryAdapter
    lateinit var visitsArrayList: ArrayList<Visits>
    lateinit var uniqueArrayList: ArrayList<Visits>
    lateinit var filter_item: String

    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GetToday()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HistoryVisitsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitsArrayList = arrayListOf()
        RecycleSetup(visitsArrayList)
        EventChangeListner()


        binding?.calendarView2?.setOnDateChangeListener { calendarView, year, month, day ->
            var realmonth = month + 1
            selected_date = "$day-$realmonth-$year"

            visitsArrayList = arrayListOf()
            RecycleSetup(visitsArrayList)
            EventChangeListner()
        }
        binding?.filterRadio?.setOnCheckedChangeListener { group, i ->
            val selected = binding!!.filterRadio.checkedRadioButtonId
            if (selected != -1) {
                filter_item = view.findViewById<RadioButton>(selected).text.toString()
                println(filter_item)

            }
            Filter()


        }


    }

    fun EventChangeListner() {
        db = FirebaseFirestore.getInstance()
        db.collection("visits").whereEqualTo("date", selected_date)
            .orderBy("bookingtime", Query.Direction.ASCENDING).addSnapshotListener(
                object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            println("Firestore error ${error.message}")
                            return
                        }
                        Thread(Runnable {
                            for (dc: DocumentChange in value?.documentChanges!!) {
                                if (dc.type == DocumentChange.Type.ADDED) {
                                    visitsArrayList.add(dc.document.toObject(Visits::class.java))
                                }

                            }
                            activity?.runOnUiThread {
                                adapter.notifyDataSetChanged()

                            }


                        }).start()
                    }

                }
            )


    }


    private fun RecycleSetup(list: ArrayList<Visits>) {
        recyclerView = view!!.findViewById(R.id.all_visits_RV)
        adapter = HistoryAdapter(list,this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun Filter() {
        if (filter_item == "all") {
            RecycleSetup(visitsArrayList)
        } else {
            var filter: ArrayList<Visits> = arrayListOf()
            for (visit: Visits in visitsArrayList) {
                var status = visit.status
                if (status!!.contains(filter_item)) {
                    filter.add(visit)
                }
            }
            RecycleSetup(filter)
        }

        adapter.notifyDataSetChanged()

    }

    fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        selected_date = "$day-$month-$year"
    }

    override fun onClick(position: Int) {
        val visit = visitsArrayList[position].visit
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        var bundle = Bundle()
        bundle.putString("visit", visit)
        navController.navigate(R.id.action_schedule_to_expand_visit, bundle)

    }

}