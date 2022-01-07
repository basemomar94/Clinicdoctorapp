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
    var newdb: FirebaseFirestore? = null
    var mobile: String? = null
    var id: String? = null
    var visit: String? = null
    var sucess: Boolean = false
    var BookB:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id")
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
            BookB=true
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
        println(BookB)
        if (BookB){
            db = FirebaseFirestore.getInstance()
            db?.collection("patiens_info")?.document(id!!)?.addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                } else {
                    var note: String = binding?.note?.text.toString()
                    mobile = value?.getString("phone")
                    var data = HashMap<String, Any>()
                    data.put("date", date!!)
                    data.put("note", note)
                    data.put("bookingtime", FieldValue.serverTimestamp())
                    data.put("id", id!!)
                    data.put("Booked_by","Clinic")
                    data.put("status","Pending")
                    db?.collection("visits")?.add(data)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            visit = it.result?.id
                            sucess = true
                            BookB=false

                        }

                    }

                }
            }
            Handler().postDelayed(Runnable {
                if (sucess) {
                    Addvisit()
                }
            }, 500)
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
        db!!.collection("patiens_info").document(id!!)
            .update(updates)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    println("Done")
                    activity?.supportFragmentManager?.popBackStack()

                }


            }
    }
}