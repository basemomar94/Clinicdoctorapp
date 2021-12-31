package com.bassem.clinicdoctorapp.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bassem.clinicdoctorapp.MainActivity
import com.bassem.clinicdoctorapp.R

class Home() : Fragment(R.layout.home_fragment) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = (activity as MainActivity).supportActionBar

        actionBar?.title = "Home"
    }

    override fun onResume() {
        super.onResume()
        val actionBar = (activity as MainActivity).supportActionBar

        actionBar?.title = "Home"

    }
}