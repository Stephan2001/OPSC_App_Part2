package com.example.timeflow_opsc_poe_part_2

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.graphics.set
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.integrity.internal.s
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var timeEntriesReference : DatabaseReference
    private  lateinit var storage: FirebaseStorage
    private  lateinit var projectReference : DatabaseReference
    val currentUser = CurrentUser.userID

    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
    var currentDate = formatter.format(Date())
    val listData : MutableList<ParentData> = ArrayList()
    var parentData = ArrayList<String>()
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
        Log.w("needhelp", "start")
        rootNode = FirebaseDatabase.getInstance()
        val context = context as MainActivity
        var selectedDate = view.findViewById<TextView>(R.id.txtSelectedDate)
        selectedDate.text = currentDate

        readData(object : FirebaseCallback {
            override fun onCallback(prjList: ArrayList<String>) {
                Log.w("needhelp", prjList.toString())
                UserProjects.projectsList.clear()
                UserProjects.projectsList = prjList
                retrieveTimesheets()
                updateDisplay()
            }
        })



        var btnchangeDate = view.findViewById<ImageButton>(R.id.btnSelectDate)
        btnchangeDate.setOnClickListener {
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

        val btnRefresh = view.findViewById<ImageButton>(R.id.btnRefresh)
        btnRefresh.setOnClickListener {
            listData.clear()
            parentData.clear()
            readData(object : FirebaseCallback {
                override fun onCallback(prjList: ArrayList<String>) {
                    Log.w("needhelp", prjList.toString())
                    UserProjects.projectsList.clear()
                    UserProjects.projectsList = prjList
                    retrieveTimesheets()
                    updateDisplay()
                }
            })
        }
    }

    fun retrieveTimesheets(){
        Log.w("needhelp", "Timesheets retrieved")
        for (project in UserProjects.projectsList) {
            timeEntriesReference = rootNode.getReference("timeEntries/$currentUser/$currentDate/$project")
            ReadTime(object : FirebaseCallbackTime {
                override fun onCallback(timeList: ArrayList<ChildData>) {
                    Log.w("needhelp", timeList.toString())
                    var parentObj = ParentData(parentTitle = project, subList = timeList)
                    listData.add(parentObj)
                    updateDisplay()
                }
            })

        }
    }

    fun updateDisplay(){
        Log.w("needhelp", "display updated")
        val context = context as MainActivity
        val RecyclerView = view?.findViewById<RecyclerView>(R.id.Recycler)
        RecyclerView?.layoutManager = LinearLayoutManager(context)
        RecyclerView?.adapter = RecycleAdapter(context,listData)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender.set(year, month, dayOfMonth)
        displayFormatDate(calender.timeInMillis)
    }

    private fun displayFormatDate(timestamp: Long) {
        var selectedDate = view?.findViewById<TextView>(R.id.txtSelectedDate)
        if (selectedDate != null) {
            selectedDate.text = formatter.format(timestamp)
            currentDate = formatter.format(timestamp)
        }
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    fun ReadTime(firebaseCallback:FirebaseCallbackTime){
        Log.w("needhelp", "read time")
        var childDataData = ArrayList<ChildData>()
        var reffs = ArrayList<String>()
        timeEntriesReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(TimesheetEntry::class.java)
                    reffs.add(dc2!!.photoReference)
                    childDataData.add(ChildData("${dc2!!.startTime} - ${dc2!!.endTime}", dc2.date , dc2.photoReference ,null))
                }
                var counter = 0
                for (child in childDataData){
                    readIMG(object : FirebaseCallbackIMG {
                        override fun onCallback(bitmap: Bitmap) {
                            child.bitmap = bitmap
                        }

                    }, reffs[counter])
                    counter++
                }
                firebaseCallback.onCallback(childDataData)
            }
            override fun onCancelled(error: DatabaseError){

            }
        })
    }

    fun readData(firebaseCallback:FirebaseCallback){
        Log.w("needhelp", "projects read ")
        parentData.clear()
        projectReference = rootNode.getReference("projects/${CurrentUser.userID}")
        projectReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (dc2!!.highPriority) {
                        parentData.add(dc2!!.name)
                    }
                }
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (!dc2!!.highPriority) {
                        parentData.add(dc2!!.name)
                    }
                }
                firebaseCallback.onCallback(parentData)
            }
            override fun onCancelled(error: DatabaseError){
            }
        })
    }

    fun readIMG(firebaseCallback:FirebaseCallbackIMG, reff:String){
        storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("images/$currentUser/${reff}")
        val ONE_MEGABYTE: Long = 1024 * 1024
        mountainsRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            firebaseCallback.onCallback(byteArrayToBitmap(it))
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    interface FirebaseCallback{
        fun onCallback(prjList:ArrayList<String>)
    }
    interface FirebaseCallbackTime{
        fun onCallback(timeList:ArrayList<ChildData>)
    }
    interface FirebaseCallbackIMG{
        fun onCallback(bitmap: Bitmap)
    }
}