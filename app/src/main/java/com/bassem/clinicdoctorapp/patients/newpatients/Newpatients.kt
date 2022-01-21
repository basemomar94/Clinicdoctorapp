package com.bassem.clinicdoctorapp.patients.newpatients

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.MainActivity
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.NewpatientFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.newpatient_fragment.*
import java.util.*

class Newpatients() : Fragment(R.layout.newpatient_fragment) {

    private var _binding: NewpatientFragmentBinding? = null
    private val binding get() = _binding
    var db: FirebaseFirestore? = null
    var userid: String? = null
    private lateinit var auth: FirebaseAuth;
    var imageuri: Uri? = null
    var imageLink: String? = null
    private val pickImage = 100
    var regesited_date: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title = "Add a new patient"
        GetToday()


    }

    override fun onResume() {
        super.onResume()
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title = "Add a new patient"

    }

    override fun onDestroyView() {
        super.onDestroyView()

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
        /*var bottomAppBar=activity?.findViewById<BottomNavigationView>(R.id.bottomAppBar)
        bottomAppBar?.visibility=View.GONE */
        db = Firebase.firestore
        addnow.setOnClickListener {
            LoadingButton()

            try {
                Signup()
            } catch (E: Exception) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show()
                NormalButton()


            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    fun senddata() {

        val name = binding?.fullname?.text.toString()
        val age = binding?.age?.text.toString().toInt()
        val complain = binding?.complain?.text.toString()
        val note = binding?.notes?.text.toString()
        val phone = binding?.phone?.text.toString()
        val mail = binding?.mail?.text.toString()
        val job = binding?.job?.text.toString()
        var sex: String? = null
        var id: Int = binding?.sexadd!!.checkedRadioButtonId
        if (id != -1) {
            val radio: RadioButton = activity!!.findViewById(id)
            sex = radio.text.toString()
        }
        if (name.isNotEmpty() && complain.isNotEmpty()
            && phone.toString().isNotEmpty() && age.toString().isNotEmpty() && mail.isNotEmpty()

        ) {
            val defaultImage =
                "https://firebasestorage.googleapis.com/v0/b/clinicapp-884ba.appspot.com/o/profile%2Fpatient.png?alt=media&token=a3a1c853-a490-40e2-a431-a783bc7b1ad1"
            val user = hashMapOf(
                "fullname" to name,
                "age" to age,
                "complain" to complain,
                "note" to note,
                "phone" to phone,
                "mail" to mail,
                "first_visit" to FieldValue.serverTimestamp(),
                "id" to userid,
                "job" to job,
                "sex" to sex,
                "img" to imageLink,
                "registered_date" to regesited_date,
                "IsVisit" to false,
                "image" to defaultImage

            )

            db?.collection("patiens_info")?.document(userid!!)?.set(user)?.addOnSuccessListener {


                findNavController().navigate(R.id.action_newpatients_to_patients)

            }!!.addOnFailureListener {
                NormalButton()
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()


            }


        } else {
            Toast.makeText(context, "Please complete patiens info", Toast.LENGTH_LONG).show()
            NormalButton()

        }


    }

    fun Signup() {
        val mail = binding?.mail?.text.toString()
        val phone = binding?.phone?.text.toString()

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(mail, phone.toString()).addOnSuccessListener {
            userid = auth.uid
            senddata()


        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            NormalButton()
            println("Signup Fail")
        }

    }


    fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        regesited_date = "$day-$month-$year"
    }

    fun LoadingButton() {
        binding?.addnow?.text = ""
        binding?.loading?.visibility = View.VISIBLE
        binding?.addnow?.alpha = .5F
        binding?.addnow?.isClickable = false

    }

    fun NormalButton() {
        binding?.addnow?.text = "ADD"
        binding?.loading?.visibility = View.INVISIBLE
        binding?.addnow?.alpha = 1F
        binding?.addnow?.isClickable = true

    }

}