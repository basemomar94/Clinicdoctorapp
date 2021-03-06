package com.bassem.clinicdoctorapp.settings

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.SettingsFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.settings_fragment.*
import java.lang.Exception
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Settings : Fragment(R.layout.settings_fragment),AdapterView.OnItemSelectedListener {
    var _binding: SettingsFragmentBinding? = null
    val binding get() = _binding
    var openingTime: String? = null
    var closingTime: String? = null
    var db: FirebaseFirestore? = null
    private var holiDay: String? = null
    private var daysList = arrayOf(
        "ـــــــــــــــــــــــــــــــ",
        "SUNDAY",
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GetData()
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        binding?.opening?.setOnClickListener {
            GetOpenTime()
        }
        binding?.closing?.setOnClickListener {
            GetCloseTime()

        }
        binding?.confirm?.setOnClickListener {
            try {

                binding?.confirm?.text = ""
                binding?.loading?.visibility = View.VISIBLE
                binding?.confirm?.alpha = .5F
                binding?.confirm?.isClickable = false
                UpdateData()
            } catch (E: Exception) {
                println(E.message)
                binding?.confirm!!.text = "Update"
                binding?.loading!!.visibility = View.INVISIBLE
                binding?.confirm!!.alpha = 1F
                binding?.confirm!!.isClickable = true
            }

        }


        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, daysList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner!!.onItemSelectedListener = this
        binding?.holidayTV?.setOnClickListener {
            binding?.holidayTV!!.visibility=View.GONE
            binding?.spinner!!.visibility=View.VISIBLE

        }




    }

    fun GetOpenTime() {

        val cal = Calendar.getInstance()
        val timePickerlistener =
            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.AM_PM, cal.get(Calendar.AM_PM))

                openingTime = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time).toString()
                println("$openingTime======================OO")
                binding?.opening?.text = openingTime
            }
        TimePickerDialog(
            context,
            timePickerlistener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()

    }


    fun GetCloseTime() {

        val cal = Calendar.getInstance()
        val timePickerlistener =
            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.AM_PM, cal.get(Calendar.AM_PM))
                closingTime = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time).toString()
                binding?.closing?.text = closingTime
            }
        TimePickerDialog(
            context,
            timePickerlistener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()

    }

    fun UpdateData() {
        println(holiDay)
        db = FirebaseFirestore.getInstance()
        val settingsHasmap = HashMap<String, Any>()
        settingsHasmap["close"] = binding?.closing!!.text
        settingsHasmap["open"] = binding?.opening!!.text
        settingsHasmap["fees"] = binding?.fees!!.text.toString()
        settingsHasmap["average"] = binding?.averageTime!!.text.toString()
        settingsHasmap["max"] = binding?.max!!.text.toString()
        settingsHasmap["holiday"] = holiDay!!
        db!!.collection("settings").document("settings").set(settingsHasmap)
            .addOnSuccessListener {
                binding?.confirm!!.text = "Update"
                binding?.loading!!.visibility = View.INVISIBLE
                binding?.confirm!!.alpha = 1F
                binding?.confirm!!.isClickable = true
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    binding?.holidayTV!!.visibility=View.VISIBLE
                    binding?.spinner!!.visibility=View.GONE
                    Toast.makeText(context, "Settings has been updated", Toast.LENGTH_LONG).show()
                }
            }


    }

    fun GetData() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("settings").document("settings").addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                binding?.opening?.text = value?.getString("open")
                binding?.closing?.text = value?.getString("close")
                binding?.fees?.setText(value?.getString("fees"))
                binding?.averageTime?.setText(value?.getString("average"))
                binding?.max?.setText(value?.getString("max"))
                binding?.holidayTV?.text=value?.getString("holiday")
                holiDay=value?.getString("holiday")

            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
       holiDay=daysList[position]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}