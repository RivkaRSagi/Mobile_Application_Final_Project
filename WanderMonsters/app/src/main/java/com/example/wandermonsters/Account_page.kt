package com.example.wandermonsters

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Button
import androidx.core.content.edit


class Account_page : AppCompatActivity() {

    lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //tab bar functionality
        val mapButton = findViewById<ImageButton>(R.id.mapButton)
        val monsterButton = findViewById<ImageButton>(R.id.monsterButton)
        val accountButton = findViewById<ImageButton>(R.id.accountButton)

        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        monsterButton.setOnClickListener {
            val intent = Intent(this, CollectionTab::class.java)
            startActivity(intent)
        }
        accountButton.setOnClickListener {
            val intent = Intent(this, Account_page::class.java)
            startActivity(intent)
        }

        //add username to page
        val username = findViewById<TextView>(R.id.username_placeholder)
        dbHelper = DatabaseHelper(this)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val currentUserID = sharedPreferences.getInt("userID", -1)
        username.text = dbHelper.getUsername(currentUserID)

        //collected monsters
        val common = findViewById<TextView>(R.id.common)
        val uncommon = findViewById<TextView>(R.id.uncommon)
        val rare = findViewById<TextView>(R.id.rare)
        val epic = findViewById<TextView>(R.id.epic)
        val legendary = findViewById<TextView>(R.id.legendary)

        common.text = dbHelper.monsterRarity(currentUserID, 0).toString()
        uncommon.text = dbHelper.monsterRarity(currentUserID, 1).toString()
        rare.text = dbHelper.monsterRarity(currentUserID, 2).toString()
        epic.text = dbHelper.monsterRarity(currentUserID, 3).toString()
        legendary.text = dbHelper.monsterRarity(currentUserID, 4).toString()

        //logout functionality
        val logoutButton = findViewById<Button>(R.id.logout)
        logoutButton.setOnClickListener{
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            sharedPreferences.edit { clear() }

        }

        //watch video functionality
        val watchVideoButton = findViewById<Button>(R.id.watch_video)
        watchVideoButton.setOnClickListener {
            val intent = Intent(this, video::class.java)
            startActivity(intent)
        }

        //delete my collection functionality
        val deleteCollectionButton = findViewById<Button>(R.id.delete_collection)
        deleteCollectionButton.setOnClickListener {
            dbHelper.deletePetMonsters()
            val intent = Intent(this, CollectionTab::class.java)
            startActivity(intent)
        }

        //delete account functionality
        val deleteAccountButton = findViewById<Button>(R.id.delete_account)
        deleteAccountButton.setOnClickListener {
            //opens a popup to make sure account wants to be deleted
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete your account?")
                //if yes
                .setPositiveButton("Yes") { dialog, _ ->

                    dbHelper.deleteAccount(currentUserID)
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPreferences.edit { clear() }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                    //if no
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

}