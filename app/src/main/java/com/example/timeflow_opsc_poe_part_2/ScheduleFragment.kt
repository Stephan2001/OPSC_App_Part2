package com.example.timeflow_opsc_poe_part_2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ScheduleFragment : Fragment() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var timeEntriesReference : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = CurrentUser.userID
        rootNode = FirebaseDatabase.getInstance()
        timeEntriesReference = rootNode.getReference("timeEntries/$currentUser")

        writeTimeEntry("sef", "gfg", "fhdfrg", "dg", "dfg")

    }

    fun writeTimeEntry(date: String, project: String, startTime: String, endTime: String, photoRefernece: String) {
        var myRef = timeEntriesReference.push()
        var key = myRef.key
        val timeEntry = TimesheetEntry( date, project, startTime, endTime, photoRefernece)
        if (key != null) {
            timeEntriesReference.child(key).setValue(timeEntry)
        }
    }

}