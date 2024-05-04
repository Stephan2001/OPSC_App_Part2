package com.example.timeflow_opsc_poe_part_2

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    val listData : MutableList<ParentData> = ArrayList()
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference
    val currentUser = CurrentUser.userID
    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.UK)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddEntry = view.findViewById<Button>(R.id.btnAddEntry)
        btnAddEntry.setOnClickListener {
            val intent = Intent(context, Select_Option::class.java)
            startActivity(intent)
        }

        // prior values
        val context = context as MainActivity

        var btnchangeDate = view.findViewById<ImageButton>(R.id.btnSelectDate)
        btnchangeDate.setOnClickListener {
            Log.i("Formatting", "got here bro no way")
            DatePickerDialog(
                context,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        calender.set(year, month, dayOfMonth)
                        displayFormatDate(calender.timeInMillis)
                    }

                },
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // expandable lists display
        val parentData: Array<String> = arrayOf("Andhra Pradesh", "Telangana", "Karnataka", "TamilNadu")
        val childDataData1: MutableList<ChildData> = mutableListOf(ChildData("Anathapur", "booma", null),ChildData("Chittoor", "booma", null))
        val childDataData2: MutableList<ChildData> = mutableListOf(ChildData("Rajanna Sircilla", "booma", null), ChildData("Karimnagar", "booma", null))
        val childDataData3: MutableList<ChildData> = mutableListOf(ChildData("Chennai", "booma", null), ChildData("Erode", "booma", null))

        val parentObj1 = ParentData(parentTitle = parentData[0], subList = childDataData1)
        val parentObj2 = ParentData(parentTitle = parentData[1], subList = childDataData2)
        val parentObj3 = ParentData(parentTitle = parentData[2])
        val parentObj4 = ParentData(parentTitle = parentData[1], subList = childDataData3)

        listData.add(parentObj1)
        listData.add(parentObj2)
        listData.add(parentObj3)
        listData.add(parentObj4)

        val RecyclerView = view.findViewById<RecyclerView>(R.id.Recycler)
        RecyclerView.layoutManager = LinearLayoutManager(context)
        RecyclerView.adapter = RecycleAdapter(context,listData)

        // database
        rootNode = FirebaseDatabase.getInstance()
        projectReference = rootNode.getReference("projects/$currentUser")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender.set(year, month, dayOfMonth)
        displayFormatDate(calender.timeInMillis)
        Log.i("Formatting", "got here bro")
    }

    private fun displayFormatDate(timestamp: Long) {
        var selectedDate = view?.findViewById<TextView>(R.id.btnSelectDate)
        if (selectedDate != null) {
            selectedDate.text = formatter.format(timestamp)
        }
        Log.i("Formatting", "got here bro aswell")
    }
}