package com.bassem.clinicdoctorapp.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bassem.clinicdoctorapp.R
import kotlinx.android.synthetic.main.newpatient_fragment.*

class Newpatients () : Fragment(R.layout.newpatient_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addnow.setOnClickListener {
            findNavController().navigate(R.id.action_newpatients_to_patients)
        }
    }
}