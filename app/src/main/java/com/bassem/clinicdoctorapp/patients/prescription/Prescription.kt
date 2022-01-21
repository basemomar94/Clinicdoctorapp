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
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.prescription_fragment.*
import org.json.JSONException
import org.json.JSONObject
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
    var token: String? = null

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
        add.put("status", "completed")
        db = FirebaseFirestore.getInstance()
        db.collection("visits").document(visit!!).update(add).addOnCompleteListener {
            if (it.isSuccessful) {
                SendRxNotification()
            }
        }
    }

    fun ClearHasvisit() {
        println("clear")
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).update("IsVisit", false).addOnCompleteListener {
            if (it.isSuccessful) {
                findNavController().navigateUp()
            }
        }

    }

    fun SendRxNotification() {
        val servertoken: String =
            "key=AAAA8wp6gvE:APA91bGkhZC4jPFfmqTiExrbYIi8-hdgqq1W9cC7EC0CMGRUM37o0a36nez9cQI4LKgNQ2Pc1VrBhL9Y04koZsZ97JCXnrctVYmYiI3LUYWZ2egnLHoxgnOGVn2wJmv_Xv0VU2ynnvGN"
        val jsonObject: JSONObject = JSONObject()
        try {
            jsonObject.put("to", token)
            val notification: JSONObject = JSONObject()
            notification.put("title", "Dr Bassem's clinc")
            notification.put("body", "Check your recent prescription")
            jsonObject.put("notification", notification)
        } catch (e: JSONException) {
            println(e.message)
        }

        val mediaType: MediaType = MediaType.parse("application/json")
        val client: OkHttpClient = OkHttpClient()
        var body: RequestBody = RequestBody.create(mediaType, jsonObject.toString())
        val request: Request? =
            Request.Builder().url("https://fcm.googleapis.com/fcm/send").method("POST", body)
                .addHeader("Authorization", servertoken)
                .addHeader("Content-Type", "application/json").build()
        Thread(Runnable {
            val response: Response = client.newCall(request).execute()
            ClearHasvisit()


            println("response=========================================${response.message()}")
        }).start()


    }

}