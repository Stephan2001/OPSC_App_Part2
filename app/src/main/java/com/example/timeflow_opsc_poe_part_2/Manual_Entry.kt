package com.example.timeflow_opsc_poe_part_2

import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.play.integrity.internal.w
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Manual_Entry : AppCompatActivity() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var timeEntriesReference : DatabaseReference
    private  lateinit var imageView:ImageView
    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            imageView.setImageURI(it)
        })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manual_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // image stuff
        val btnAddphoto = findViewById<Button>(R.id.btnAddPhoto)
        imageView = findViewById(R.id.imgTimesheet)

        btnAddphoto.setOnClickListener{
            imageContract.launch("image/*")
        }

        val currentUser = CurrentUser.userID
        rootNode = FirebaseDatabase.getInstance()
        timeEntriesReference = rootNode.getReference("timeEntries/$currentUser")

    }

    fun writeTimeEntry(date: String, project: String, startTime: String, endTime: String, photoRefernece: String) {
        var myRef = timeEntriesReference.push()
        var key = myRef.key
        val timeEntry = TimesheetEntry( date, project, startTime, endTime, photoRefernece)
        if (key != null) {
            timeEntriesReference.child(key).setValue(timeEntry)
        }
    }

    companion object{
        val IMAGE_REQUEST_CODE = 100
    }
}