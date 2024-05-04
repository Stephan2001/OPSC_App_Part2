package com.example.timeflow_opsc_poe_part_2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Manual_Entry : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var timeEntriesReference : DatabaseReference
    private  lateinit var imageView:ImageView
    private  lateinit var projectReference : DatabaseReference
    private  lateinit var storage:FirebaseStorage
    var currentProject = ""
    val currentUser = CurrentUser.userID
    var photoRefernece = ""

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            imageView.setImageURI(it)
        })

    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
    private val formatter2 = SimpleDateFormat("hh:mm a", Locale.UK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manual_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // prior values
        val btnAddphoto = findViewById<Button>(R.id.btnAddPhoto)
        imageView = findViewById(R.id.imgTimesheet)
        rootNode = FirebaseDatabase.getInstance()
        var projectsList:ArrayList<String> = populateProjects()

        // populating projects list
        populateProjects()

        // populate dropdownList
        populateDropdown(projectsList)

        // adding photo to imageview
        btnAddphoto.setOnClickListener{
            imageContract.launch("image/*")
        }

        var date = findViewById<TextView>(R.id.txtSetDate)
        var startingTime = findViewById<TextView>(R.id.txtStaringTime)
        var endingTime = findViewById<TextView>(R.id.txtEndingTime)
        val btnSave = findViewById<Button>(R.id.btnSaveprj)
        btnSave.setOnClickListener{
            writeTimeEntry(date.text.toString(), currentProject, startingTime.text.toString(), endingTime.text.toString())
            upLoadImage(uploadToBytes())
        }

        date.setOnClickListener {
            DatePickerDialog(
                this,
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

        startingTime.setOnClickListener {
            displayFormattedTime1(calender.timeInMillis)
            TimePickerDialog(
                this,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        calender.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        displayFormattedTime1(calender.timeInMillis)
                    }
                },
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                false
            ).show()
        }

        endingTime.setOnClickListener {
            displayFormattedTime2(calender.timeInMillis)
            TimePickerDialog(
                this,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        calender.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        displayFormattedTime2(calender.timeInMillis)
                    }
                },
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    fun uploadToBytes(): ByteArray{
        val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun upLoadImage(array:ByteArray){
        storage = Firebase.storage
        var storageRef = storage.reference
        val mountainsRef = storageRef.child("images/$currentUser/$photoRefernece")
        val data = array

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

    fun populateDropdown(projectsList:ArrayList<String>){
        val spinnerID = findViewById<Spinner>(R.id.mySpinnerProjects)
        val arrayAdapt = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, projectsList)
        spinnerID.adapter = arrayAdapt
        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentProject = projectsList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun populateProjects():ArrayList<String>{
        var projectsList = ArrayList<String>()
        projectReference = rootNode.getReference("projects/$currentUser")
        projectReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (dc2 != null) {
                        projectsList.add(dc2.name)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError){

            }
        })
        return projectsList
    }

    // this method is used to reconvert to bitarray for imageview
    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    fun writeTimeEntry(date: String, project: String, startTime: String, endTime: String) {
        timeEntriesReference = rootNode.getReference("timeEntries/$currentUser/$date")
        var myRef = timeEntriesReference.push()
        var key = myRef.key
        if (key != null) {
            photoRefernece = key + "IMG"
        }
        val timeEntry = TimesheetEntry( date, project, startTime, endTime, photoRefernece)
        if (key != null) {
            timeEntriesReference.child(key).setValue(timeEntry)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender.set(year, month, dayOfMonth)
        displayFormatDate(calender.timeInMillis)

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calender.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        displayFormattedTime1(calender.timeInMillis)
    }


    private fun displayFormatDate(timestamp: Long) {
        findViewById<TextView>(R.id.txtSetDate).text = formatter.format(timestamp)
        Log.i("Formatting", formatter.format(timestamp))
    }

    fun displayFormattedTime1(timestamp: Long) {
        findViewById<TextView>(R.id.txtStaringTime).text = formatter2.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }

    fun displayFormattedTime2(timestamp: Long) {
        findViewById<TextView>(R.id.txtEndingTime).text = formatter2.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }

}