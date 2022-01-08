package com.bassem.clinicdoctorapp.schedule.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinic_userapp.ui.booking.Visits
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.HistoryVisitsFragmentBinding
import com.bassem.clinicdoctorapp.patients.listofpatients.Patientsclass
import com.bassem.clinicdoctorapp.patients.listofpatients.patientsadapter
import com.google.common.collect.LinkedHashMultiset
import com.google.firebase.firestore.*

class HistoryOfvisits : Fragment(R.layout.history_visits_fragment) {
    var _binding: HistoryVisitsFragmentBinding? = null
    val binding get() = _binding
    var selected_date: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: HistoryAdapter
    lateinit var visitsArrayList: ArrayList<Visits>
    lateinit var uniqueArrayList: ArrayList<Visits>

    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding?.calendarView2?.setOnDateChangeListener { calendarView, year, month, day ->
            var realmonth = month + 1
            selected_date = "$day-$realmonth-$year"

            visitsArrayList = arrayListOf()
            RecycleSetup(visitsArrayList)
            EventChangeListner()


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
        adapter = HistoryAdapter(list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

}