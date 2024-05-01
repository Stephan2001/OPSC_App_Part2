package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Select_Option : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_option)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnManual = findViewById<Button>(R.id.btnManualEntry)
        val btnTimer = findViewById<Button>(R.id.btnTimerEntry)

        btnManual.setOnClickListener{
            val intent = Intent(this, Manual_Entry::class.java)
            startActivity(intent)
            this.finish()
        }

        btnTimer.setOnClickListener{
            Toast.makeText(baseContext,"Coming soon",Toast.LENGTH_SHORT).show()
        }
    }
}