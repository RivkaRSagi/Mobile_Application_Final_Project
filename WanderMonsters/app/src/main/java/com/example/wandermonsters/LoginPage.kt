package com.example.wandermonsters

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.Toast

class LoginPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //on click listener for back button
        val back = findViewById<Button>(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        dbHelper = DatabaseHelper(this)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        //on click listener for login button
        loginButton.setOnClickListener {
            val usernameText = username.text.toString().trim()
            val passwordText = password.text.toString().trim()

            //if username or password is empty
            if (usernameText.isEmpty() || passwordText.isEmpty()){
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //if user account is validated, retrieve user id and store in shared preferences
            if (dbHelper.validateUserAccount(usernameText, passwordText)){
                val currentUserID = dbHelper.getOwnerID(usernameText, passwordText)
                if (currentUserID != -1) {
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putInt("userID", currentUserID)
                        .apply()

                    val dbHelper = DatabaseHelper(this)
                    val dictionary = dbHelper.getMonsterCount()
                    if (dictionary == 0) {
                        dbHelper.initializeMonsterTypes(this)
                    }

                    //reset monster definitions to undiscovered
                    dbHelper.resetDiscovered()
                    //set monster definitions to discovered based on user
                    dbHelper.setDiscovered(currentUserID)
//                    startActivity(Intent(this, CollectionTab::class.java))
                    startActivity(Intent(this, MapActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()

            }
        }



    }
}