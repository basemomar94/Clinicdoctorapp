package com.bassem.clinicdoctorapp.patients.info

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.CalendarbookingFragmentBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.reflect.Field

class Calendar : Fragment(R.layout.calendarbooking_fragment) {
    var _binding: CalendarbookingFragmentBinding? = null
    val binding get() = _binding
    var date: String? = null
    var db: FirebaseFirestore? = null
    var mobile: String? = null
    var id: String? = null
    var visit: String? = null
    var complain:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id")
            complain=bundle.getString("complain")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarbookingFragmentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.calendarView?.setOnDateChangeListener { calendarView, year, month, dayofMonth ->
            var realmonth: Int = month + 1
            date = "$dayofMonth-$realmonth-$year"
            binding?.nextvisit?.text = "Your next visit will be in $date"
            binding?.card?.visibility = View.VISIBLE
            binding?.confrimC?.visibility = View.VISIBLE
            binding?.note?.visibility = View.VISIBLE

        }
        binding?.confirm?.setOnClickListener {
            binding?.confirm?.text = ""
            binding?.loading?.visibility = View.VISIBLE
            binding?.confirm?.alpha = .5F
            binding?.confirm?.isClickable = false
            try {
                Book()


            } catch (E: Exception) {
                println(E.message)
                binding?.confirm!!.text = "Confirm"
                binding?.loading!!.visibility = View.INVISIBLE
                binding?.confirm!!.alpha = 1F
                binding?.confirm!!.isClickable = true
            }


        }
    }

    fun Book() {
        db = FirebaseFirestore.getInstance()
        var note: String = binding?.note?.text.toString()
        var data = HashMap<String, Any>()
        data.put("date", date!!)
        data.put("note", note)
        data.put("bookingtime", FieldValue.serverTimestamp())
        data.put("id", id!!)
        data.put("Booked_by", "Clinic")
        data.put("status", "Pending")
        data.put("complain",complain!!)

        db?.collection("visits")?.add(data)?.addOnCompleteListener {
            if (it.isSuccessful) {
                visit = it.result?.id
                Addvisit()
                return@addOnCompleteListener

            }

        }



    }

    fun Backtoinfo() {
        val bundle = Bundle()
        bundle.putString("id", id)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder.setLaunchSingleTop(true).build()
        navController.navigate(R.id.action_calendar_to_patientsInfo2, bundle, navOptions)
    }

    fun Addvisit() {
        println("first test")
        var updates = HashMap<String, Any>()
        updates.put("visit_id", visit!!)
        updates.put("IsVisit", true)
        updates.put("next_visit",date!!)

        db!!.collection("patiens_info").document(id!!)
            .update(updates)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    println("Done")
                    db!!.collection("visits").document(visit!!).update("visit",visit)
                    activity?.supportFragmentManager?.popBackStack()

                }


            }
    }
}