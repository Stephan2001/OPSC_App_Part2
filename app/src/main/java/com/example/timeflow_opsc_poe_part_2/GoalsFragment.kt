package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class GoalsFragment : Fragment() {

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

        /*val setDailyGoal = view.findViewById<Button>(R.id.btnDailyGoal)
        setDailyGoal.setOnClickListener {
            val intent = Intent(context, Set_Daily_Goals::class.java)
            startActivity(intent)
        }*/
    }
}