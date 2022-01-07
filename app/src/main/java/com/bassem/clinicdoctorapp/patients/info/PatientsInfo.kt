package com.bassem.clinicdoctorapp.patients.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.PatientinfoFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.patientinfo_fragment.*


class PatientsInfo() : Fragment(R.layout.patientinfo_fragment) {
    lateinit var id: String
    lateinit var db: FirebaseFirestore
    private var _binding: PatientinfoFragmentBinding? = null
    val binding get() = _binding
    var mobile: String? = null
    private var fullname: String? = null
    private var handler: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PatientinfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id", "null")
            println("$id=========id")
            GettingData()

        }
        binding?.call?.setOnClickListener {
            Call()

        }
        binding?.whatsapp?.setOnClickListener {
            Whatsapp()
        }
        binding?.prescription?.setOnClickListener {
            GotoPrescription()
        }
        binding?.bookBu?.setOnClickListener {
            GotoBooking()
        }
        binding?.cancel?.setOnClickListener {
            Cancel()
        }

    }

    fun GettingData() {
        println("data")
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).addSnapshotListener { value, error ->

            if (error != null) {
                println("Firebase error ${error.message}")
            } else {
                fullname = value?.getString("fullname")
                binding!!.fullnameInfo.text = fullname

                binding!!.ageInfo.text = value?.getDouble("age")?.toInt().toString()
                binding!!.jobInfo.text = value?.getString("job")
                binding!!.complainInfo.text = value?.getString("complain")
                binding!!.mailInfo.text = value?.getString("mail")
                mobile = value?.getString("phone")
                binding!!.phoneInfo.text = mobile

                binding!!.notesInfo.text = value?.getString("note")
                val sex = value?.getString("sex")
                binding!!.sexInfo.text = sex
                handler = if (sex.equals("male")) {
                    "Mr"
                } else {
                    "Miss"
                }
                if (value?.getBoolean("IsVisit") == true) {
                    binding?.next?.text = value.getString("next_visit")
                    binding?.bookBu?.visibility = View.GONE
                    binding?.cancel?.visibility = View.VISIBLE
                    binding?.nextlinear?.visibility = View.VISIBLE
                    binding?.prescription?.visibility=View.VISIBLE
                } else {
                    binding?.bookBu?.visibility = View.VISIBLE
                    binding?.cancel?.visibility = View.GONE
                    binding?.nextlinear?.visibility = View.GONE
                    binding?.prescription?.visibility=View.GONE


                }


            }
        }
    }


    fun Call() {

        var intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$mobile")
        startActivity(intent)
    }

    fun Whatsapp() {

        val text = "Hello $handler $fullname It's Dr bassem Clinic"
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=+20$mobile&text=$text")

        val intent = Intent(Intent.ACTION_VIEW, uri)

        context!!.startActivity(intent)


    }

    fun GotoPrescription() {
        val bundle = Bundle()
        bundle.putString("id", id)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_patientsInfo_to_prescription, bundle)
    }

    fun GotoBooking() {
        val bundle = Bundle()
        bundle.putString("id", id)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder.setLaunchSingleTop(true).build()
        navController.navigate(R.id.action_patientsInfo_to_calendar, bundle,navOptions)

    }

    fun Cancel() {
        println("cancel")
        db = FirebaseFirestore.getInstance()
        db?.collection("patiens_info").document(id).update("IsVisit", false)
            ?.addOnCompleteListener {

                if (it.isSuccessful) {
                    binding?.bookBu?.visibility = View.VISIBLE
                    binding?.cancel?.visibility = View.GONE
                    binding?.nextlinear?.visibility = View.GONE
                }

            }
    }


}