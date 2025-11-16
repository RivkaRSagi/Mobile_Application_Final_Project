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

        //create dummy monsters for testing database
//        val newMonster = Monster.createRandomMonster(this)
//        val newMonster2 = Monster.createRandomMonster(this)


        dbHelper= DatabaseHelper(this)
        //initialize dictionary
//        dbHelper.initializeMonsterTypes(this)
//        dbHelper.deleteMonsters()
        //create new test user
//        dbHelper.newUser("testUser", "123")
//        val currentUserID = dbHelper.getOwnerID("testUser", "123")
//        dbHelper.addPetMonster(currentUserID, newMonster)
//        dbHelper.addPetMonster(currentUserID, newMonster2)

//        Log.d("MonsterCount", "Total monsters: ${dbHelper.getMonsterCount()}")


        val collection = findViewById<Button>(R.id.collectionButton)
        collection.setOnClickListener {
            val intent = Intent(this, CollectionTab::class.java)
            startActivity(intent)
        }

        val map = findViewById<Button>(R.id.mapButton)
        map.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        val sign_in = findViewById<Button>(R.id.signInButton)
        sign_in.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        val mini_game = findViewById<Button>(R.id.miniGameButton)
        mini_game.setOnClickListener {
            val intent = Intent(this, MiniGameActivity::class.java)
            startActivity(intent)
        }
    }
}