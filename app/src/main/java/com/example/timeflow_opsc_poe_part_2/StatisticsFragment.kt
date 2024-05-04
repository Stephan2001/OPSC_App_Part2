package com.example.timeflow_opsc_poe_part_2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatisticsFragment : Fragment() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference
    var durationChoice = arrayOf("This week", "This month", "This year")
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
        val context = context as MainActivity

        val spinnerID = view.findViewById<Spinner>(R.id.mySpinnerprgStats)
        val arrayAdapt = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, durationChoice)
        spinnerID.adapter = arrayAdapt
        val priority = false

        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (durationChoice[p2] == "false"){
                    priority == false
                }
                else{
                    priority == true
                }
                Toast.makeText(context, "item selected: ${durationChoice[p2]}" ,Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(context, "item selected: Nothing" ,Toast.LENGTH_SHORT).show()
            }
        }

        projectReference = rootNode.getReference("projects/$currentUser")

    }



    fun populateDropdown(projectsList:ArrayList<String>, context: Context){
        val spinnerID = view?.findViewById<Spinner>(R.id.mySpinnerprgStats)
        val arrayAdapt = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, projectsList)
        spinnerID?.adapter = arrayAdapt
        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currentProject = projectsList[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    fun populateProjects():ArrayList<String> {
        projectReference = rootNode.getReference("projects/$currentUser")
        projectReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot1 in snapshot.children) {
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (dc2 != null) {
                        projectsList.add(dc2.name)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        return projectsList
    }

    //to populate projects dropdown list
    fun onCreate(view: View, savedInstanceState: Bundle?) {
        rootNode = FirebaseDatabase.getInstance()
        val context = context as MainActivity

        val spinnerID = view.findViewById<Spinner>(R.id.mySpinnerprgStats)
        val arrayAdapt = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, projectsList)
        spinnerID.adapter = arrayAdapt
        val projects = false

        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (projectsList[p2] == "false"){
                    projects == false
                }
                else{
                    projects == true
                }
                Toast.makeText(context, "item selected: ${projectsList[p2]}" ,Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(context, "item selected: Nothing" ,Toast.LENGTH_SHORT).show()
            }
        }
        projectReference = rootNode.getReference("projects/$currentUser")
    }
}