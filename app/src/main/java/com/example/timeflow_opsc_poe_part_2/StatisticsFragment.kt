package com.example.timeflow_opsc_poe_part_2

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class StatisticsFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference
    private  lateinit var timeEntriesReference : DatabaseReference
    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
    private val formatterTime = SimpleDateFormat("hh:mm a", Locale.UK)
    private val formatterTime2 = SimpleDateFormat("HH:mm", Locale.UK)
    var currentDate = formatter.format(Date())
    val currentUser = CurrentUser.userID
    var currentProject = ""
    var projectsList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    //to populate duration dropdown list
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootNode = FirebaseDatabase.getInstance()
        projectReference = rootNode.getReference("projects/$currentUser")
        val context = context as MainActivity

        readData(object : FirebaseCallbackstats {
            override fun onCallback(prjList: ArrayList<String>) {
                Log.w("needhelp", prjList.toString())
                populatePrg(prjList)
            }
        })

        val btnCalc = view.findViewById<Button>(R.id.btnCalc)
        btnCalc.setOnClickListener {
            calcDay(object :FirebaseCallbackCalcDay{
                override fun onCallback(total: ArrayList<Duration>) {
                    var totalTime:Duration = 0.hours + 0.minutes
                    for (item in total){
                        totalTime = totalTime.plus(item)
                    }
                    val displayTime = view.findViewById<TextView>(R.id.txtDisplayTotalTime)
                    displayTime.text = totalTime.toString()
                    Log.w("totalhelp", totalTime.toString())
                }
            })
        }

        var date = view.findViewById<TextView>(R.id.txtSelectedCalcDate)

        date.setOnClickListener {
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

    }

    fun populatePrg(list: ArrayList<String>){
        val context = context as MainActivity
        val spinnerID2 = view?.findViewById<Spinner>(R.id.mySpinnerprgStats)
        val arrayAdapt2 = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, list)
        spinnerID2?.adapter = arrayAdapt2
        spinnerID2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentProject = projectsList[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    fun readData(firebaseCallback: FirebaseCallbackstats){
        Log.w("needhelp", "projects read ")
        projectsList.clear()
        projectReference = rootNode.getReference("projects/${CurrentUser.userID}")
        projectReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    projectsList.add(dc2!!.name)
                }
                firebaseCallback.onCallback(projectsList)
            }
            override fun onCancelled(error: DatabaseError){
            }
        })
    }
    interface FirebaseCallbackstats{
        fun onCallback(prjList:ArrayList<String>)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender.set(year, month, dayOfMonth)
        displayFormatDate(calender.timeInMillis)
    }

    private fun displayFormatDate(timestamp: Long) {
        var selectedDate = view?.findViewById<TextView>(R.id.txtSelectedCalcDate)
        if (selectedDate != null) {
            selectedDate.text = formatter.format(timestamp)
            currentDate = formatter.format(timestamp)
        }
    }

    fun calcDay(firebaseCallback: FirebaseCallbackCalcDay){
        var totaldur =  ArrayList<Duration>()
        timeEntriesReference = rootNode.getReference("timeEntries/$currentUser/$currentDate/$currentProject")
        timeEntriesReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){

                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(TimesheetEntry::class.java)
                    var start = 0
                    var end = 0
                    var startTime = dc2!!.startTime.removeRange(5, 8)
                    var endTime = dc2!!.endTime.removeRange(5, 8)
                    if (dc2!!.startTime.takeLast(2) == "pm"){
                        start += 12
                    }
                    if (dc2!!.endTime.takeLast(2) == "pm"){
                        end += 12
                    }
                    var newstartTime = startTime.split(":").toTypedArray()
                    var newendTime = endTime.split(":").toTypedArray()
                    // start
                    if (newstartTime[0].take(1)== "0"){
                        newstartTime[0] = newstartTime[0].takeLast(1)
                    }
                    if (newstartTime[1].take(1)== "0"){
                        newstartTime[1] = newstartTime[0].takeLast(1)
                    }
                    // end
                    if (newendTime[0].take(1)== "0"){
                        newendTime[0] = newendTime[0].takeLast(1)
                    }
                    if (newendTime[1].take(1)== "0"){
                        newendTime[1] = newendTime[0].takeLast(1)
                    }
                    var startHoursInt: Int = newstartTime[0].toInt()
                    var startMinInt: Int = newstartTime[1].toInt()
                    var endHoursInt: Int = newendTime[0].toInt()
                    var endMinInt: Int = newendTime[1].toInt()

                    val startDuration = (startHoursInt + start).hours + startMinInt.minutes
                    val endDuration = (endHoursInt + end).hours + endMinInt.minutes
                    val difference: Duration = endDuration.minus(startDuration)

                    if (difference.isPositive()){
                        totaldur.add(difference)
                    }

                    Log.w("startDuration",startDuration.toString())
                    Log.w("endDuration", endDuration.toString())
                    Log.w("needhelp", difference.toString())
                }
                firebaseCallback.onCallback(totaldur)
            }
            override fun onCancelled(error: DatabaseError){

            }
        })
    }

    interface FirebaseCallbackCalcDay{
        fun onCallback(total: ArrayList<Duration>)
    }

}