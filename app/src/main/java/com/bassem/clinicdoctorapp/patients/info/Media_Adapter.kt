package com.bassem.clinicdoctorapp.patients.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.schedule.PageViewerAdapter
import com.smarteist.autoimageslider.SliderViewAdapter

class Media_Adapter(val mediaList:ArrayList<Int>) : SliderViewAdapter<Media_Adapter.viewHolder>() {
    class viewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {
        var imageView:ImageView=itemView!!.findViewById(R.id.media_item)


    }

    override fun getCount(): Int {
     return   mediaList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): viewHolder {
     val v = LayoutInflater.from(parent?.context).inflate(R.layout.image_view,null)
        return viewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: viewHolder?, position: Int) {
        var media = mediaList[position]


    }

}