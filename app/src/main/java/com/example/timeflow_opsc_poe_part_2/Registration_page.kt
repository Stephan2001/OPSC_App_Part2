package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class Registration_page : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnReg = findViewById<Button>(R.id.btnReg)
        btnReg.setOnClickListener ()
        {
            val email: EditText = findViewById(R.id.txtEmail)
            val pass: EditText = findViewById(R.id.txtPassword)
            val firstName: EditText = findViewById(R.id.txtName)
            val lastName: EditText = findViewById(R.id.txtLastName)
            createAccount(email.text.toString(), pass.text.toString())
            addProfile(firstName.text.toString(), lastName.text.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addProfile(firstName: String, lastName: String){
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = "$firstName $lastName"
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("auth", "User profile updated.")
                }
            }
        user?.let {
            for (profile in it.providerData) {

                // UID specific to the provider
                val uid = profile.uid

                // Name, email address, and profile photo Url
                val name = profile.displayName
                val email = profile.email
                Log.d("auth", name.toString() + " " + email.toString())
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("auth", "createUserWithEmail:success")
                    val user = auth.currentUser
                } else {
                    Log.w("auth", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}
