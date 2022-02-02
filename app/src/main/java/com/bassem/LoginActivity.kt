package com.bassem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.bassem.clinicdoctorapp.MainActivity
import com.bassem.clinicdoctorapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class LoginActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    lateinit var mail: EditText
    lateinit var password: EditText
    lateinit var loginBu: Button
    lateinit var loading: ProgressBar

    override fun onStart() {
        super.onStart()
        checkLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mail = findViewById(R.id.mail_log)
        password = findViewById(R.id.password_log)
        loginBu = findViewById(R.id.confirmBu)
        loading = findViewById(R.id.progressLogin)
        loginBu.setOnClickListener {
            if (mail.text.isNotEmpty() && password.text.isNotEmpty()) {
                checkDoctor()
            }

        }


    }

    fun checkDoctor() {
        showLoading()
        db = FirebaseFirestore.getInstance()
        db.collection("doctors")
            .document(mail?.text.toString().lowercase(Locale.getDefault()).trim())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "please check your username",
                        Snackbar.LENGTH_LONG
                    ).show()
                    showButton()
                } else {
                    val firebaseKey = value?.getString("key")
                    if (firebaseKey == password?.text.toString().lowercase(Locale.getDefault())
                            .trim()
                    ) {
                        saveLogin()
                        goToDashboard()
                    } else {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "please check your password",
                            Snackbar.LENGTH_LONG
                        ).show()
                        showButton()

                    }


                }
            }
    }

    fun goToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showLoading() {
        loginBu.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    fun showButton() {
        loginBu.visibility = View.VISIBLE
        loading.visibility = View.GONE
    }

    fun saveLogin() {
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("key", true)
        editor.apply()
    }

    fun checkLogin() {
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val key = pref.getBoolean("key", false)
        if (key) {
            goToDashboard()
        }

    }
}