package com.bassem.clinicdoctorapp.patients.info

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bassem.clinicdoctorapp.R
import com.bassem.clinicdoctorapp.databinding.MediaFragmentBinding
import com.smarteist.autoimageslider.SliderView

class Media : Fragment(R.layout.media_fragment) {
    var _binding: MediaFragmentBinding ?= null
    val binding get() = _binding
    var slider:SliderView?=null
    var sliderViewAdapter:Media_Adapter?=null
    var mediaList: ArrayList <Int>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= MediaFragmentBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaList= arrayListOf(R.drawable.advice,R.drawable.age)
        slider=view.findViewById(R.id.imageSlider)
       // slider.sliderAdapter=Media_Adapter(mediaList)

    }
}