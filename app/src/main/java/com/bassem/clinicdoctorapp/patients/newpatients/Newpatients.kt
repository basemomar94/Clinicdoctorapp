package com.bassem.clinicdoctorapp.patients.newpatients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.NewpatientFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.newpatient_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class Newpatients() : Fragment(R.layout.newpatient_fragment) {

    var _binding: NewpatientFragmentBinding? = null
    val binding get() = _binding
    var db: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NewpatientFragmentBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        var viewmodel = ViewModelProvider(this).get(NewpatiensViewmodel::class.java)
        addnow.setOnClickListener {
            senddata()
        }
    }

    fun senddata() {
        val name = binding?.nameadd?.text.toString()
        val age = binding?.age?.text.toString().toInt()
        val complain = binding?.complainadd?.text.toString()
        val note = binding?.notes?.text.toString()
        val phone = binding?.phone?.text.toString().toInt()
        val mail = binding?.mail?.text.toString()
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        if (name.isNotEmpty() && complain.isNotEmpty() && complain.isNotEmpty() && note.isNotEmpty()
            && phone.toString().isNotEmpty() && age.toString().isNotEmpty() && mail.isNotEmpty()

        ) {
            val user = hashMapOf(
                "name" to name,
                "age" to age,
                "complain" to complain,
                "note" to note,
                "phone" to phone,
                "mail" to mail,
                "first_visit" to currentDate

            )

            db?.collection("patiens_info")?.add(user)?.addOnSuccessListener {


                findNavController().navigate(R.id.action_newpatients_to_patients)

            }!!.addOnFailureListener {

            }


        }


    }
}