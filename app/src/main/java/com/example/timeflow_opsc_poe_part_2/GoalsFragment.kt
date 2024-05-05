package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GoalsFragment : Fragment() {
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
        val context = context as MainActivity

        val setDailyGoal = view.findViewById<Button>(R.id.btnDailyGoal)
        setDailyGoal.setOnClickListener {
            val intent = Intent(context, Set_Daily_Goals::class.java)
            startActivity(intent)
        }

        var txtMinDailyTime = view?.findViewById<TextView>(R.id.currentMin)
        var txtMaxDailyTime = view?.findViewById<TextView>(R.id.currentMax)
        rootNode = FirebaseDatabase.getInstance()
        goalsReference = rootNode.getReference("goals/${CurrentUser.userID}")
        goalsReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(DailyGoals::class.java)
                    if (dc2 != null) {
                        txtMinDailyTime?.text = dc2.DailyMin
                        txtMaxDailyTime?.text = dc2.DailyMax
                    }
                }
            }
            override fun onCancelled(error: DatabaseError){

            }
        })

    }
}