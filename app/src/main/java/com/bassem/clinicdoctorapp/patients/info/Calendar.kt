package com.bassem.clinicdoctorapp.patients.info

import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.CalendarbookingFragmentBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap


class Calendar : Fragment(R.layout.calendarbooking_fragment) {
    var _binding: CalendarbookingFragmentBinding? = null
    val binding get() = _binding
    var date: String? = null
    var db: FirebaseFirestore? = null
    var mobile: String? = null
    var id: String? = null
    var visit: String? = null
    var complain: String? = null
    var fullname: String? = null
    var token: String? = null
    var turn: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id")
            complain = bundle.getString("complain")
            fullname = bundle.getString("name")
            token = bundle.getString("token")
            println(fullname)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.calendarView?.setOnDateChangeListener { calendarView, year, month, dayofMonth ->
            var realmonth: Int = month + 1
            date = "$dayofMonth-$realmonth-$year"
            if (ValidBooking(date!!)) {
                binding?.nextvisit?.setTextColor(Color.GREEN)

                binding?.nextvisit?.text = AfterDays(date!!)
                binding?.card?.visibility = View.VISIBLE
                binding?.confrimC?.visibility = View.VISIBLE
                binding?.note?.visibility = View.VISIBLE
            } else {
                binding?.nextvisit?.setTextColor(Color.RED)

                binding?.nextvisit?.text = "the visit should be in the future"
                binding?.card?.visibility = View.VISIBLE
                binding?.confirm?.visibility = View.GONE
                binding?.note?.visibility = View.GONE
            }


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
        data.put("complain", complain!!)
        data.put("name", fullname!!)

        db?.collection("visits")?.add(data)?.addOnCompleteListener {
            if (it.isSuccessful) {
                visit = it.result?.id
                VisitTurn()
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
        updates.put("next_visit", date!!)

        db!!.collection("patiens_info").document(id!!)
            .update(updates)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    SendBookingNotification()
                    println("Done")
                    db!!.collection("visits").document(visit!!).update("visit", visit)
                    activity?.supportFragmentManager?.popBackStack()

                }


            }
    }

    fun SendBookingNotification() {
        val servertoken: String =
            "AAAA8wp6gvE:APA91bGkhZC4jPFfmqTiExrbYIi8-hdgqq1W9cC7EC0CMGRUM37o0a36nez9cQI4LKgNQ2Pc1VrBhL9Y04koZsZ97JCXnrctVYmYiI3LUYWZ2egnLHoxgnOGVn2wJmv_Xv0VU2ynnvGN"
        val jsonObject: JSONObject = JSONObject()
        try {
            jsonObject.put("to", token)
            jsonObject.put("title", "Dr Bassem's clinc")
            jsonObject.put("body", "We have booked you an appointment on $date")
            val notification: JSONObject = JSONObject()
            jsonObject.put("notification", notification)
        } catch (e: JSONException) {
            println(e.message)
        }

        val mediaType: MediaType = MediaType.parse("application/json")
        val client: OkHttpClient = OkHttpClient()
        var body: RequestBody = RequestBody.create(mediaType, jsonObject.toString())
        var testbody: RequestBody = RequestBody.create(
            mediaType,
            "{\r\n \"to\" : \"eAJNg0jiSQu5LhJgxib4K7:APA91bEYzF8IdujkKT2e-TlOsbZUWyMr8unjNKgMroQYnYAiZpU9E-3oqhJYEUcOUZnkCxEBV5yUPw1oXOz4GygqWMUJEBiFiieCrdwEeryIsub60A19DORCA9sAN6xtn--3k39bdR5I\",\r\n \"notification\" : {\r\n     \"body\" : \"Body of Your Notification\",\r\n     \"title\": \"Title of Your Notification\"\r\n }\r\n}"
        )
        val request: Request? =
            Request.Builder().url("https://fcm.googleapis.com/fcm/send").method("POST", testbody)
                .addHeader("Authorization", servertoken)
                .addHeader("Content-Type", "application/json").build()
        Thread(Runnable {
            val response: Response = client.newCall(request).execute()
            println("response=========================================${response.message()}")
        }).start()


    }

    fun VisitTurn() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("date", date).whereEqualTo("status", "Pending").get()
            .addOnCompleteListener {
                turn = it.result?.size().toString()
                println("$turn ============test")
                Addvisit()

            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ValidBooking(date: String): Boolean {
        val locale = Locale.ENGLISH
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate: LocalDate = LocalDate.parse(date, sdf)
        val dateNow = LocalDate.now()
        return visitDate >= dateNow
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun AfterDays(date: String): String {
        var result: String
        val dateNow = LocalDate.now()
        val locale = Locale.US
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate = LocalDate.parse(date, sdf)
        if (visitDate == dateNow) {
            result = "Your visit will be today"
        } else {
            var differnt = ChronoUnit.DAYS.between(dateNow, visitDate)
            result = "Your visit will be after $differnt days"
        }
        return result

    }

}