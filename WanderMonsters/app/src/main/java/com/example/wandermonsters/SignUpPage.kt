package com.example.wandermonsters

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.content.Intent
import android.widget.Toast


class SignUpPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //declarations
        val signUpUsername = findViewById<EditText>(R.id.username)
        val signUpPassword = findViewById<EditText>(R.id.password)
        val signUpConfirmPassword = findViewById<EditText>(R.id.confirm_password)
        val back_button = findViewById<Button>(R.id.back_button)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        //on click listener for back button
        back_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // sign up functionalities
        signUpButton.setOnClickListener {
            val usernameText = signUpUsername.text.toString().trim()
            val passwordText = signUpPassword.text.toString().trim()

            if (usernameText.isEmpty() || passwordText.isEmpty()){
                Toast.makeText(this, "Please enter username or password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText != signUpConfirmPassword.text.toString().trim()){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                dbHelper = DatabaseHelper(this)
                dbHelper.newUser(usernameText, passwordText)
                Toast.makeText(this, "Sign Up Successful,Returning to Main Page", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }


        }

    }
}