package com.example.timeflow_opsc_poe_part_2

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Set_Daily_Goals : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("hh:mm a", Locale.UK)
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var goalsReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_daily_goals)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // prior values
        rootNode = FirebaseDatabase.getInstance()
        goalsReference = rootNode.getReference("goals/${CurrentUser.userID}")

        findViewById<TextView>(R.id.txtMinDailyTime).setOnClickListener {
            displayFormattedTime1(calender.timeInMillis)
            TimePickerDialog(
                this,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        calender.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        displayFormattedTime1(calender.timeInMillis)
                    }
                },
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                false
            ).show()
        }

        findViewById<TextView>(R.id.txtMaxDailyTime).setOnClickListener {
            displayFormattedTime2(calender.timeInMillis)
            TimePickerDialog(
                this,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        calender.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        displayFormattedTime2(calender.timeInMillis)
                    }
                },
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                false
            ).show()
        }

        var btnSaveTimeDaily = findViewById<Button>(R.id.btnSaveTimeDaily)
        var txtMinDailyTime = findViewById<TextView>(R.id.txtMinDailyTime)
        var txtMaxDailyTime = findViewById<TextView>(R.id.txtMaxDailyTime)
        btnSaveTimeDaily.setOnClickListener{
            if(txtMinDailyTime != null && txtMaxDailyTime != null){
                goalsReference.setValue(DailyGoals(txtMinDailyTime.text.toString(), txtMaxDailyTime.text.toString()))
                this.finish()
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calender.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        displayFormattedTime1(calender.timeInMillis)
    }

    fun displayFormattedTime1(timestamp: Long) {
        findViewById<TextView>(R.id.txtMinDailyTime).text = formatter.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }

    fun displayFormattedTime2(timestamp: Long) {
        findViewById<TextView>(R.id.txtMaxDailyTime).text = formatter.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }
}


