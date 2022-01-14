package com.bassem.clinicdoctorapp.patients.listofpatients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinicdoctorapp.R
import java.util.ArrayList


class patientsadapter(
    val patientsList: ArrayList<Patientsclass>,
    val listner: Myclicklisener,
) :
    RecyclerView.Adapter<patientsadapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): patientsadapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.patient_item_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: patientsadapter.ViewHolder, position: Int) {
        val patient: Patientsclass = patientsList[position]
        holder.fullname.text = patient.fullname
        holder.complain.text = patient.complain
        holder.phone.text=patient.phone
    }

    override fun getItemCount(): Int {
        return patientsList.size
    }

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val fullname: TextView = itemview.findViewById(R.id.name_list)
        val complain: TextView = itemview.findViewById(R.id.complain_list)
        val phone: TextView = itemview.findViewById(R.id.phone_list)

        init {

            itemview.setOnClickListener {
                val position: Int = absoluteAdapterPosition
                listner.onClick(position)
                println(position)


            }
        }


    }

    interface Myclicklisener {
        fun onClick(position: Int)

    }


}

