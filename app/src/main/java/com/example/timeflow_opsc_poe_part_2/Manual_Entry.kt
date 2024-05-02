package com.example.timeflow_opsc_poe_part_2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class Manual_Entry : AppCompatActivity() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var timeEntriesReference : DatabaseReference
    private  lateinit var imageView:ImageView
    private  lateinit var storage:FirebaseStorage

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            imageView.setImageURI(it)
            val imageUri: Uri? = it

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
        val imgnew = findViewById<ImageView>(R.id.imgTimesheet2)

        btnAddphoto.setOnClickListener{
            imageContract.launch("image/*")
        }

        val currentUser = CurrentUser.userID
        rootNode = FirebaseDatabase.getInstance()
        timeEntriesReference = rootNode.getReference("timeEntries/$currentUser")

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener{
            imgnew.setImageBitmap(byteArrayToBitmap(uploadImage()));
        }
    }

    fun uploadImage(): ByteArray{
        val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
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