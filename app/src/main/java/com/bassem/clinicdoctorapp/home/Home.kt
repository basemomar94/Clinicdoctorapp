package com.bassem.clinicdoctorapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.schedule.history.Visits
import com.bassem.clinicdoctorapp.MainActivity
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.HomeFragmentBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList

class Home() : Fragment(R.layout.home_fragment) {
    var _binding: HomeFragmentBinding? = null
    val binding get() = _binding
    var db: FirebaseFirestore? = null
    var today: String? = null
    lateinit var visitsArrayList: ArrayList<Visits>
    var currentPatient_id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = (activity as MainActivity).supportActionBar
        visitsArrayList = arrayListOf()

        actionBar?.title = "Home"
        GetToday()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        val actionBar = (activity as MainActivity).supportActionBar

        actionBar?.title = "Home"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        GetData()
        GetBookedToday()

        binding?.todayPatiensCard?.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_schedule)
        }
        binding?.currentCard?.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("id", currentPatient_id)
            val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
            navController.navigate(R.id.action_home_to_patientsInfo, bundle)
        }

    }

    private fun GetData() {
        db = FirebaseFirestore.getInstance()
        db?.collection("visits")?.whereEqualTo("date", today)
            ?.orderBy("bookingtime", Query.Direction.ASCENDING)?.addSnapshotListener(


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
                                println("${visitsArrayList.size}==============doctor size")
                                activity?.runOnUiThread { Filter() }


                            }).start()
                        }

                    }
                })

    }

    fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
        println("$today============today")
    }

    fun Filter() {
        var pendingList: ArrayList<Visits> = arrayListOf()
        var AllList: ArrayList<Visits> = arrayListOf()
        var cancelList: ArrayList<Visits> = arrayListOf()
        var completList: ArrayList<Visits> = arrayListOf()
        Thread(Runnable {
            for (visit: Visits in visitsArrayList) {
                var status = visit.status

                if (status == "Pending" || status == "completed") {
                    AllList.add(visit)
                }
                if (status == "Pending") {
                    pendingList.add(visit)
                }
                if (status == "cancelled by clinic" || status == "cancelled by You") {
                    cancelList.add(visit)
                }
                if (status == "completed") {
                    completList.add(visit)
                }

            }
            activity?.runOnUiThread {
                binding?.allHome?.text = AllList.size.toString()
                binding?.pendingHome?.text = pendingList.size.toString()

                if (pendingList.isNotEmpty()) {
                    binding?.currentCard?.visibility = View.VISIBLE
                    currentPatient_id = pendingList[0].id
                    binding?.currentName?.text = pendingList[0].name
                    binding?.currentComplain?.text = pendingList[0].complain

                }
                binding?.cancelHome?.text = cancelList.size.toString()
                binding?.doneHome?.text = completList.size.toString()
                binding?.todayIncome?.text  ="${(200*completList.size).toString()} EGP"
            }
        }).start()
    }

    fun GetBookedToday() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("booking_date", today).get().addOnCompleteListener {
            if (it.isSuccessful) {
                binding?.bookedToday?.text = it.result?.size().toString()

            }

        }
    }
}