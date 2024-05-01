package com.example.timeflow_opsc_poe_part_2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Project_Create : AppCompatActivity() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val currentUser = CurrentUser.userID
        rootNode = FirebaseDatabase.getInstance()
        projectReference = rootNode.getReference("projects/$currentUser")
    }

    fun writeProject(name: String, priority: Boolean) {
        var myRef = projectReference.push()
        var key = myRef.key
        val project = Project( name, priority)
        if (key != null) {
            projectReference.child(key).setValue(project)
        }
    }
}