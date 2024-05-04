package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timeflow_opsc_poe_part_2.databinding.ActivityLoginPageBinding
import com.example.timeflow_opsc_poe_part_2.databinding.ActivityRegistrationPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login_Page : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        emailFocusListener()
        passwordFocusListener()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var buttonReg : Button = findViewById(R.id.btnRegister)
        buttonReg.setOnClickListener ()
        {
            val intent = Intent(this, Registration_page::class.java)
            startActivity(intent)
        }

        val btnLogin = findViewById<Button>(R.id.btnSignIn)
        btnLogin.setOnClickListener ()
        {
            val email: EditText = findViewById(R.id.txtEmail)
            val pass: EditText = findViewById(R.id.txtPassword)
            signIn(email.text.toString(), pass.text.toString())
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //validate login
    private fun emailFocusListener() {
        binding.txtEmail.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.emailContainer.helperText = validEmail()
            }
        }
    }
    //validate email
    private fun validEmail(): String? {
        val txtEmail = binding.txtEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches())
        {
            return "Invalid email address"
        }
        return null
    }

    //validate password
    private fun passwordFocusListener() {
        binding.txtPassword.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.passwordContainer.helperText = validPassword()
            }
        }
    }
    //validate password
    private fun validPassword(): String? {
        val txtPassword = binding.txtPassword.text.toString()
        if (txtPassword.length < 6)
        {
            return "Must be 6 characters long"
        }
        if (!txtPassword.matches(".*[A-Z].*".toRegex()))
        {
            return "Must contain at least 1 upper case character"
        }
        if (!txtPassword.matches(".*[a-z].*".toRegex()))
        {
            return "Must contain at least 1 lower case character"
        }
        if (!txtPassword.matches(".*[!@#$%&*.].*".toRegex()))
        {
            return "Must contain at least 1 special character (!@#$%&*.)"
        }
        return null
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    setCurrentUser()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        // [END sign_in_with_email]
    }

    fun setCurrentUser(){
        val user = auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                CurrentUser.userID = profile.uid
                Log.d("setuser", CurrentUser.userID)
                break
            }
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}