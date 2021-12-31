package com.bassem.clinicdoctorapp.patients.listofpatients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinicdoctorapp.MainActivity
import com.bassem.clinicdoctorapp.R
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.patients_fragment.*

class PatientsList() : Fragment(R.layout.patients_fragment), patientsadapter.Myclicklisener {

    lateinit var recyclerView: RecyclerView
    lateinit var patientsArrayList: ArrayList<Patientsclass>
    lateinit var myAdapter: patientsadapter
    lateinit var db: FirebaseFirestore
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title = "Patients list"

    }

    override fun onResume() {
        super.onResume()
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title = "Patients list"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        patientsArrayList = arrayListOf()
        recyclerView = view.findViewById(R.id.patientsRV)
        myAdapter = patientsadapter(patientsArrayList, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myAdapter
        EventChangedListner()
        addnew.setOnClickListener {
            findNavController().navigate(R.id.action_patients_to_newpatients)

        }
    }


    private fun EventChangedListner() {

        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").orderBy("first_visit", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error != null) {
                        println("Firestore error ${error.message}")
                        return
                    }
                    Thread(Runnable {
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                patientsArrayList.add(dc.document.toObject(Patientsclass::class.java))
                            }

                        }
                        activity?.runOnUiThread {
                            myAdapter.notifyDataSetChanged()

                        }


                    }).start()


                }


            })
    }

    override fun onClick(position: Int) {
        val patient = patientsArrayList[position]
        id = patient.id!!
        var bundle: Bundle = Bundle()
        bundle.putString("id", id)
        println(id)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_patients_to_patientsInfo, bundle)


    }


}