package com.bassem.clinicdoctorapp.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bassem.clinicdoctorapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Schedule_Container : Fragment(R.layout.schedule_container) {
    var tabtitle = arrayOf("Today", "All")
    lateinit var tableLayout: TabLayout
    lateinit var pager2: ViewPager2

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
        tableLayout = view.findViewById(R.id.tab)
        pager2 = view.findViewById(R.id.viewpager)
        pager2.adapter = PageViewerAdapter(activity!!.supportFragmentManager, activity!!.lifecycle)

        TabLayoutMediator(tableLayout, pager2) {

                tab, position ->
            tab.text = tabtitle[position]

        }.attach()

    }


}