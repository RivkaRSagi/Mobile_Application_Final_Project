package com.example.wandermonsters

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        dbHelper= DatabaseHelper(this)

        //Testing
        //initialize dictionary
//        dbHelper.initializeMonsterTypes(this)
//        dbHelper.deleteMonsters()

        //create new test user
//        dbHelper.newUser("testUser", "123")
//        val currentUserID = dbHelper.getOwnerID("testUser", "123")
//        dbHelper.addPetMonster(currentUserID, newMonster)
//        dbHelper.addPetMonster(currentUserID, newMonster2)

        //create dummy monsters for testing database
//        val newMonster = Monster.createRandomMonster(this)
//        val newMonster2 = Monster.createRandomMonster(this)

//        Log.d("MonsterCount", "Total monsters: ${dbHelper.getMonsterCount()}")




        //setting up shared preferences, if user has already logged in, the mapActivity page will be opened
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        //to get rid of shared preference data, uncomment
//        sharedPreferences.edit().clear().apply()

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MapActivity::class.java))
//            startActivity(Intent(this, CollectionTab::class.java))
            finish()
            return
        }



        //On click listener for logging in
        val logIn = findViewById<Button>(R.id.logIn)
        logIn.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        //On click listener for signing up
        val signUp = findViewById<Button>(R.id.signUp)
        signUp.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }



//        val collection = findViewById<Button>(R.id.collectionButton)
//        collection.setOnClickListener {
//            val intent = Intent(this, CollectionTab::class.java)
//            startActivity(intent)
//        }
//
//        val map = findViewById<Button>(R.id.mapButton)
//        map.setOnClickListener {
//            val intent = Intent(this, MapActivity::class.java)
//            startActivity(intent)
//        }
//
//        val sign_in = findViewById<Button>(R.id.signInButton)
//        sign_in.setOnClickListener {
//            val intent = Intent(this, LoginPage::class.java)
//            startActivity(intent)
//        }
//
//        val mini_game = findViewById<Button>(R.id.miniGameButton)
//        mini_game.setOnClickListener {
//            val intent = Intent(this, MiniGameActivity::class.java)
//            startActivity(intent)
//        }
    }
}