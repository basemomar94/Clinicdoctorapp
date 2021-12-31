package com.bassem.clinicdoctorapp.patients.listofpatients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinicdoctorapp.R
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.patients_fragment.*

class PatientsList () : Fragment(R.layout.patients_fragment) {

    lateinit var recyclerView: RecyclerView
    lateinit var patientsArrayList: ArrayList<Patientsclass>
    lateinit var myAdapter : patientsadapter
    lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        patientsArrayList= arrayListOf()
        recyclerView=view.findViewById(R.id.patientsRV)
        myAdapter= patientsadapter(patientsArrayList)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=myAdapter
        EventChangedListner()
        addnew.setOnClickListener {
            findNavController().navigate(R.id.action_patients_to_newpatients)

        }
    }

    fun SetupRecycle (){
        recyclerView=activity!!.findViewById(R.id.patientsRV)
      //  patientsArrayList= arrayListOf()
        myAdapter= patientsadapter(patientsArrayList)
        recyclerView.adapter=myAdapter
    //    EventChangedListner ()
    }

    private fun EventChangedListner() {

        db= FirebaseFirestore.getInstance()
        db.collection("patiens_info").addSnapshotListener(object :EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                if (error!=null){
                    println("Firestore error ${error.message}")
                    return
                }
                for (dc:DocumentChange in value?.documentChanges!!){
                    if (dc.type==DocumentChange.Type.ADDED){
                        patientsArrayList.add(dc.document.toObject(Patientsclass::class.java))
                    }
                    println("$patientsArrayList ============list")
                }
                myAdapter.notifyDataSetChanged()


            }


        })
    }


}