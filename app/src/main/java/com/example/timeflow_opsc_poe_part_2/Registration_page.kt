package com.example.timeflow_opsc_poe_part_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputBinding
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timeflow_opsc_poe_part_2.databinding.ActivityRegistrationPageBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest


class Registration_page : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegistrationPageBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPageBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        nameFocusListener()
        lastNameFocusListener()
        emailFocusListener()
        passwordFocusListener()
        confirmPasswordFocusListener()
        binding.btnReg.setOnClickListener{register()}
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnReg = findViewById<Button>(R.id.btnReg)
        /*btnReg.setOnClickListener ()
        {
            val email: EditText = findViewById(R.id.txtEmail)
            val pass: EditText = findViewById(R.id.txtPassword) // must be 6 chars long
            val firstName: EditText = findViewById(R.id.txtName)
            val lastName: EditText = findViewById(R.id.txtLastName)
            createAccount(email.text.toString(), pass.text.toString())
            addProfile(firstName.text.toString(), lastName.text.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }*/
    }

    private fun register() {
        binding.nameContainer.helperText = validName()
        binding.lastnameContainer.helperText = validLastName()
        binding.emailContainer.helperText = validEmail()
        binding.passwordContainer.helperText = validPassword()
        binding.confirmPasswordContainer.helperText = validConfirmPassword()


        val validName = binding.nameContainer.helperText == null
        val validLastName = binding.lastnameContainer.helperText == null
        val validEmail = binding.emailContainer.helperText == null
        val validPassword = binding.passwordContainer.helperText == null
        val validConfirmPassword = binding.confirmPasswordContainer.helperText == null

        if (validName && validLastName && validEmail && validPassword && validConfirmPassword)
        {
            successful()
        }
        else
        {
            invalidForm()
        }

    }

    private fun successful() {
        val email: EditText = findViewById(R.id.txtEmail)
        val pass: EditText = findViewById(R.id.txtPassword)
        val firstName: EditText = findViewById(R.id.txtName)
        val lastName: EditText = findViewById(R.id.txtLastName)
        createAccount(email.text.toString(), pass.text.toString())
        addProfile(firstName.text.toString(), lastName.text.toString())
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)


    }

    private fun invalidForm() {
        var message = ""
        if (binding.nameContainer.helperText != null)
            message += "\nFirst name: " + binding.nameContainer.helperText
        if (binding.lastnameContainer.helperText != null)
            message += "\nLast name: " + binding.lastnameContainer.helperText
        if (binding.emailContainer.helperText != null)
            message += "\nEmail: " + binding.emailContainer.helperText
        if (binding.passwordContainer.helperText != null)
            message += "\nPassword: " + binding.passwordContainer.helperText
        if (binding.confirmPasswordContainer.helperText != null)
            message += "\nConfirm password: " + binding.confirmPasswordContainer.helperText

        AlertDialog.Builder(this)
            .setTitle("Invalid format")
            .setMessage(message)
            .setPositiveButton("Okay"){_,_ ->}.show()
    }


    //validate first name
    private fun nameFocusListener(){
        binding.txtName.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.nameContainer.helperText = validName()
            }
        }
    }

    private fun validName(): String? {
        val txtName = binding.txtName.text.toString()
        if (txtName.isEmpty())
        {
            return "Must contain value"
        }
        return null
    }

    //validate last name
    private fun lastNameFocusListener(){
        binding.txtLastName.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.lastnameContainer.helperText = validLastName()
            }
        }
    }

    private fun validLastName(): String? {
        val txtLastName = binding.txtLastName.text.toString()
        if (txtLastName.isEmpty())
        {
            return "Must contain value"
        }
        return null
    }

    //validate email
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
            return "Must be longer than 6 characters"
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

    //validate confirm password
    private fun confirmPasswordFocusListener() {
        binding.txtConfirmPassword.setOnFocusChangeListener{_, focused ->
            if (!focused)
            {
                binding.passwordContainer.helperText = validConfirmPassword()
            }
        }
    }

    private fun validConfirmPassword(): String? {
        val txtConfirmPassword = binding.txtConfirmPassword.text.toString()
        if (txtConfirmPassword.length < 6)
        {
            return "Must be 6 characters long"
        }
        if (!txtConfirmPassword.matches(".*[A-Z].*".toRegex()))
        {
            return "Must contain at least 1 upper case character"
        }
        if (!txtConfirmPassword.matches(".*[a-z].*".toRegex()))
        {
            return "Must contain at least 1 lower case character"
        }
        if (!txtConfirmPassword.matches(".*[!@#$%&*.].*".toRegex()))
        {
            return "Must contain at least 1 special character (!@#$%&*.)"
        }
        return null
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
                CurrentUser.userID = profile.uid
                val name = profile.displayName
                val email = profile.email
                Log.d("auth", name.toString() + " " + email.toString())
                break
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("auth", "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Authentication successful.",
                        Toast.LENGTH_SHORT,
                    ).show()
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
