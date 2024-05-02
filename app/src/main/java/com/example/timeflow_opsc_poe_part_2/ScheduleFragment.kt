package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScheduleFragment : Fragment() {
    private  lateinit var listView : ListView
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = CurrentUser.userID
        val btnCreateProject = view.findViewById<TextView>(R.id.btnAddEntry)
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
            Toast.makeText(context, IDList[position], Toast.LENGTH_SHORT,).show()
        }

        btnCreateProject.setOnClickListener{
            val intent = Intent(context, Project_Create::class.java)
            startActivity(intent)
        }
    }
}