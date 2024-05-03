package com.example.timeflow_opsc_poe_part_2

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Set_One_Time_Goal : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("hh:mm a", Locale.UK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_one_time_goal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.txtMinTimeOnce).setOnClickListener {
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

        findViewById<TextView>(R.id.txtMaxTimeOnce).setOnClickListener {
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
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calender.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        displayFormattedTime1(calender.timeInMillis)
    }


    fun displayFormattedTime1(timestamp: Long) {
        findViewById<TextView>(R.id.txtMinTimeOnce).text = formatter.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }

    fun displayFormattedTime2(timestamp: Long) {
        findViewById<TextView>(R.id.txtMaxTimeOnce).text = formatter.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }
}