package com.bassem.clinicdoctorapp.patients.prescription

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.PrescriptionFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.prescription_fragment.*
import java.util.*
import kotlin.collections.HashMap

class Prescription : Fragment(R.layout.prescription_fragment) {
    lateinit var _binding: PrescriptionFragmentBinding
    val binding get() = _binding
    lateinit var id: String
    lateinit var db: FirebaseFirestore
    var req: String? = null
    var inst: String? = null
    var pre: String? = null
    var visit: String? = null
    var sucess: Boolean = false
    var sucess2: Boolean = false

    var add = HashMap<String, Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PrescriptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id", "noid")
            gettingData()
        }

        binding.Addplus.setOnClickListener {
            binding.card.visibility = View.VISIBLE
            binding.framconfirm.visibility = View.VISIBLE
            binding.instructionsTV.text = binding.instructionET.text
            binding.roshta.text = binding.medecine.text
            binding.requestsTV.text = binding.requestsET.text
            binding.instructionsTV.text = binding.instructionET.text
        }
        binding.confirm.setOnClickListener {
            binding?.confirm?.text = ""
            binding?.loading?.visibility = View.VISIBLE
            binding?.confirm?.alpha = .5F
            binding?.confirm?.isClickable = false
            try {
                SendPrescribtion()

            } catch (E: Exception) {
                println(E.message)
                binding.confirm.text = "Confirm"
                binding.loading.visibility = View.INVISIBLE
                binding.confirm.alpha = 1F
                binding.confirm.isClickable = true

            }
        }


    }

    fun SendPrescribtion() {
        pre = binding.roshta.text.toString()
        inst = binding.instructionsTV.text.toString()
        req = binding.requestsTV.text.toString()
        println(pre)


        add.put("pre", pre!!)
        add.put("ins", inst!!)
        add.put("req", req!!)

        db.collection("patiens_info").document(id).update(add).addOnCompleteListener {

            if (it.isSuccessful) {


                sucess = true

            }

        }.addOnFailureListener {
            println(it.message)
            binding.confirm.text = "Confirm"
            binding.loading.visibility = View.INVISIBLE
            binding.confirm.alpha = 1F
            binding.confirm.isClickable = true
        }
        Handler().postDelayed(Runnable {
            if (sucess) {
                Addtovisits()

            }
        }, 1000)




    }

    fun gettingData() {
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).addSnapshotListener { value, error ->
            if (error != null) {
                println("Firestore error $error")

            } else {
                binding.namePrescription.text = value?.getString("fullname")
                visit = value?.getString("visit_id")

            }
        }

    }

    fun Backtoinfo() {
        val bundle = Bundle()
        bundle.putString("id", id)
        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_prescription2_to_patientsInfo, bundle)
    }

    fun Addtovisits() {
        db = FirebaseFirestore.getInstance()
        db.collection("visits").document(visit!!).update(add).addOnCompleteListener {
            if (it.isSuccessful) {
             ClearHasvisit()
            }
        }
    }

    fun ClearHasvisit() {
        println("clear")
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).update("IsVisit", false).addOnCompleteListener {
            if (it.isSuccessful) {
                activity?.supportFragmentManager?.popBackStack()

            }
        }

    }

}