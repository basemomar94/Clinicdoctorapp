package com.bassem.clinicdoctorapp.schedule.history.today_visitors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinicdoctorapp.schedule.history.Visits
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.TodayvisitorsFragmentBinding
import com.bassem.clinicdoctorapp.schedule.history.HistoryAdapter
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.todayvisitors_fragment.view.*
import java.util.*
import kotlin.collections.ArrayList

class Today_Visitor : Fragment(R.layout.todayvisitors_fragment),HistoryAdapter.Myclicklisener {
    var _binding: TodayvisitorsFragmentBinding? = null
    val binding get() = _binding
    var today: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: HistoryAdapter
    lateinit var visitsArrayList: ArrayList<Visits>
    lateinit var db: FirebaseFirestore
    lateinit var filter_item:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GetToday()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TodayvisitorsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitsArrayList = arrayListOf()
        RecySetup(visitsArrayList)
        EventChangeListner()


        binding?.filterRadio?.setOnCheckedChangeListener { group, i ->
            val selected=view.filter_radio.checkedRadioButtonId
            if (selected!=-1){
                filter_item=view.findViewById<RadioButton>(selected).text.toString()
                Filter(filter_item)
            }

        }
    }

    fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
        println("$today============today")
    }

    fun EventChangeListner() {
        db = FirebaseFirestore.getInstance()
        db.collection("visits").whereEqualTo("date", today)
            .orderBy("bookingtime", Query.Direction.ASCENDING).addSnapshotListener(
                object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            println(error.message)
                            return
                        } else {
                            Thread(Runnable {
                                for (dc: DocumentChange in value!!.documentChanges) {
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
                }
            )


    }

    fun RecySetup(list: ArrayList<Visits>) {
        adapter = HistoryAdapter(list,this)
        recyclerView = view!!.findViewById(R.id.today_RV)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


    }
    private fun Filter(item:String){
        if (item == "all"){
            RecySetup(visitsArrayList)
        } else {
            var filter: ArrayList<Visits> = arrayListOf()
            for (visit: Visits in visitsArrayList){
                var status=visit.status
                if (status!!.contains(item)){
                    filter.add(visit)
                }
            }
            RecySetup(filter)
        }

        adapter.notifyDataSetChanged()

    }

    override fun onClick(position: Int) {
        val visit = visitsArrayList[position].visit
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        var bundle = Bundle()
        bundle.putString("visit", visit)
        navController.navigate(R.id.action_schedule_to_expand_visit, bundle)
    }
}