package com.bassem.clinicdoctorapp.schedule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bassem.clinicdoctorapp.schedule.history.HistoryOfvisits
import com.bassem.clinicdoctorapp.schedule.history.today_visitors.Today_Visitor

open class PageViewerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                Today_Visitor()
            }
            else -> {
                HistoryOfvisits()
            }
        }
    }
}