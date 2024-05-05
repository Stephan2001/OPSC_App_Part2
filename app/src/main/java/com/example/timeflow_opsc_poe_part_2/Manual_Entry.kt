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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timeflow_opsc_poe_part_2.UserProjects.projectsList
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

        //dropdown stuff
        val spinnerID = findViewById<Spinner>(R.id.mySpinnerprgEntries)
        val arrayAdapt = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, projectsList)
        spinnerID.adapter = arrayAdapt

        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentProject = projectsList[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // adding photo to imageview
        btnAddphoto.setOnClickListener{
            imageContract.launch("image/*")
        }

        // storing details to database
        // setting date
        var date = findViewById<TextView>(R.id.txtSetDate)

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

        // setting starting time
        var startingTime = findViewById<TextView>(R.id.txtStaringTime)

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

        // setting ending time
        var endingTime = findViewById<TextView>(R.id.txtEndingTime)

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


        val btnSave = findViewById<Button>(R.id.btnSaveprj)
        btnSave.setOnClickListener{
            val date: TextView = findViewById(R.id.txtSetDate)
            val startingTime: TextView = findViewById(R.id.txtStaringTime)
            val endingTime: TextView = findViewById(R.id.txtEndingTime)

            if(date.text.toString().isEmpty() || startingTime.text.toString().isEmpty() || endingTime.text.toString().isEmpty()){
                Toast.makeText(
                    baseContext,
                    "Values can't be empty.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else{
                writeTimeEntry(date.text.toString(), currentProject, startingTime.text.toString(), endingTime.text.toString())
                upLoadImage(uploadToBytes())
                this.finish()
            }
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

    fun validateInputs(){

    }

}