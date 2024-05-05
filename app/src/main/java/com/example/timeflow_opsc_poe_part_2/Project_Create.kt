package com.example.timeflow_opsc_poe_part_2

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Project_Create : AppCompatActivity() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference
    var priorities = arrayOf("High", "Low")
    val currentUser = CurrentUser.userID
    var priority = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rootNode = FirebaseDatabase.getInstance()
        val spinnerID = findViewById<Spinner>(R.id.mySpinner)
        val arrayAdapt = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinnerID.adapter = arrayAdapt
        var priority = false

        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (priorities[p2] == "false"){
                    priority = false
                }
                else{
                    priority = true
                }
                Toast.makeText(this@Project_Create, "item selected: ${priorities[p2]}" ,Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@Project_Create, "item selected: Nothing" ,Toast.LENGTH_SHORT).show()
            }
        }

        val btnSave = findViewById<Button>(R.id.btnSaveprj)
        val btnCancelprg = findViewById<Button>(R.id.btnCancelprg)
        var name = findViewById<EditText>(R.id.txtProjects)
        projectReference = rootNode.getReference("projects/$currentUser")

        btnSave.setOnClickListener{
            val project: EditText = findViewById(R.id.txtProjects)
            val spinner: Spinner = findViewById(R.id.mySpinner)

            if (project.text.toString().isEmpty() || spinner.isEmpty()){
                Toast.makeText(
                    baseContext,
                    "Values can't be empty.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                writeProject(name.text.toString(), priority)
                this.finish()
                Toast.makeText(
                    baseContext,
                    "Project added",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        btnCancelprg.setOnClickListener{
            this.finish()
            Toast.makeText(
                baseContext,
                "Project canceled",
                Toast.LENGTH_SHORT,
            ).show()
        }
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