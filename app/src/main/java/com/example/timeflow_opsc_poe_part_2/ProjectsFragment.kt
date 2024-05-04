package com.example.timeflow_opsc_poe_part_2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProjectsFragment : Fragment() {
    private  lateinit var listView : ListView
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = CurrentUser.userID
        val btnCreateProject = view.findViewById<TextView>(R.id.btnAddProject)
        listView = view.findViewById<ListView>(R.id.lvProjects)
        rootNode = FirebaseDatabase.getInstance()
        projectReference = rootNode.getReference("projects/$currentUser")

        // setting up the list view
        val context = context as MainActivity
        val list = ArrayList<String>()
        val IDList = ArrayList<String>()

        val lv = context.findViewById(R.id.lvProjects) as ListView
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
        lv.adapter = adapter

        // reading from dastabase
        projectReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                IDList.clear()
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    val txt = " ${dc2?.name}"
                    IDList.add(snapshot1.key.toString())
                    txt?.let {list.add(it)}
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError){

            }
        })

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = parent.getItemAtPosition(position)
            var id = element.toString().trim()
            deleteProject(id)
        }

        btnCreateProject.setOnClickListener{
            val intent = Intent(context, Project_Create::class.java)
            startActivity(intent)
        }
    }

    fun deleteProject(id:String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder

            .setTitle("Delete the Project?")
            .setPositiveButton("Delete") { dialog, which ->
                // Do something.
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Do something else.
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}