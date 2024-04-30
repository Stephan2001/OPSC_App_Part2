package com.example.timeflow_opsc_poe_part_2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ProjectsFragment : Fragment() {
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
        val btn = view.findViewById<Button>(R.id.btnproject)
        val currentUser = "one"
        rootNode = FirebaseDatabase.getInstance()
        projectReference = rootNode.getReference("projects/$currentUser")
        btn.setOnClickListener{
            writeProject("Building house", true)
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