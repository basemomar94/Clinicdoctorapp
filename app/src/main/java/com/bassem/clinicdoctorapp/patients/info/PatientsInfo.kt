package com.bassem.clinicdoctorapp.patients.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        Call()
        binding?.whatsapp?.setOnClickListener {
            Whatsapp()
        }

    }

    fun GettingData() {
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
                handler = if (sex.equals("male")){
                    "Mr"
                } else {
                    "Miss"
                }


            }
        }
    }

    fun Call() {
        binding?.call?.setOnClickListener {
            var intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$mobile")
            startActivity(intent)

        }
    }

    fun Whatsapp() {

        val text = "Hello $handler $fullname It's Dr bassem Clinic"
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=+20$mobile&text=$text")

        val intent = Intent(Intent.ACTION_VIEW, uri)

        context!!.startActivity(intent)


    }


}