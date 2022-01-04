package com.bassem.clinicdoctorapp.patients.newpatients

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.MainActivity
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.NewpatientFragmentBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.newpatient_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class Newpatients() : Fragment(R.layout.newpatient_fragment) {

    private var _binding: NewpatientFragmentBinding? = null
    private val binding get() = _binding
    var db: FirebaseFirestore? = null
    var userid: String? = null
    private lateinit var auth: FirebaseAuth;




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title="Add a new patient"



    }

    override fun onResume() {
        super.onResume()
         val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.title="Add a new patient"

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
        var viewmodel = ViewModelProvider(this).get(NewpatiensViewmodel::class.java)
        addnow.setOnClickListener {
            binding?.addnow?.text = ""
            binding?.loading?.visibility = View.VISIBLE
            binding?.addnow?.alpha = .5F
            binding?.addnow?.isClickable = false

            try {
                Signup()
            } catch (E: Exception) {
                println(E.message)
                binding?.addnow?.text = "ADD"
                binding?.loading?.visibility = View.INVISIBLE
                binding?.addnow?.alpha = 1F
                binding?.addnow?.isClickable = true


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
        var sex : String?=null
        var id : Int = binding?.sexadd!!.checkedRadioButtonId
        if (id!=-1){
            val radio:RadioButton=activity!!.findViewById(id)
            sex= radio.text.toString()
        }
        if (name.isNotEmpty() && complain.isNotEmpty() && complain.isNotEmpty() && note.isNotEmpty()
            && phone.toString().isNotEmpty() && age.toString().isNotEmpty() && mail.isNotEmpty()

        ) {
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
                "sex" to sex

            )

            db?.collection("patiens_info")?.document(userid!!)?.set(user)?.addOnSuccessListener {


                findNavController().navigate(R.id.action_newpatients_to_patients)

            }!!.addOnFailureListener {
                binding?.addnow?.text = "ADD"
                binding?.loading?.visibility = View.INVISIBLE
                binding?.addnow?.alpha = 1F
                binding?.addnow?.isClickable = true



            }


        }


    }

    fun Signup() {
        val mail = binding?.mail?.text.toString()
        val phone = binding?.phone?.text.toString()

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(mail, phone.toString()).addOnSuccessListener {
            println("${it.user?.uid}=========================it====")

            userid = auth.uid
            println("$userid =================auth")
            senddata()


        }.addOnFailureListener {
            println(it.message)
            binding?.addnow?.text = "ADD"
            binding?.loading?.visibility = View.INVISIBLE
            binding?.addnow?.alpha = 1F
            binding?.addnow?.isClickable = true
        }

    }
}