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


class GoalsFragment : Fragment() {
    private  lateinit var listView : ListView
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var goalsReference : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setDailyGoal = view.findViewById<Button>(R.id.btnDailyGoal)
        setDailyGoal.setOnClickListener {
            val intent = Intent(context, Set_Daily_Goals::class.java)
            startActivity(intent)
        }

        val currentUser = CurrentUser.userID
        val btnDailyGoal = view.findViewById<TextView>(R.id.btnDailyGoal)
        listView = view.findViewById<ListView>(R.id.lvGoals)
        rootNode = FirebaseDatabase.getInstance()
        goalsReference = rootNode.getReference("goals/$currentUser")

        // setting up the list view
        val context = context as MainActivity
        val list = ArrayList<String>()
        val IDList = ArrayList<String>()

        val lv = context.findViewById(R.id.lvGoals) as ListView
        val adapter = ArrayAdapter(context, R.layout.goal_listitems, list)
        lv.adapter = adapter

        // reading from dastabase
        goalsReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                IDList.clear()
                list.clear()
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(DailyGoals::class.java)
                    val txt = " ${dc2?.DailyMin}, ${dc2?.DailyMax}"
                    txt?.let {list.add(it)}
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError){

            }
        })





    }
}