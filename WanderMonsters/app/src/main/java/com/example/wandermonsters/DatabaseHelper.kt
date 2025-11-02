package com.example.wandermonsters

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context): SQLiteOpenHelper(
    context, "WanderMonsters.db",null,1
){

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS Accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                password TEXT NOT NULL
                )
            """.trimIndent()
        )
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS MonsterDefinitions (
                Type_Id INTEGER PRIMARY KEY NOT NULL,
                Type_Name TEXT NOT NULL,
                Image TEXT NOT NULL,
                Discovered INTEGER NOT NULL,
                Rarity INTEGER NOT NULL
                )
            """.trimIndent()
        )
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS PetMonsters (
                Owner_ID INTEGER NOT NULL,
                Name TEXT NOT NULL,
                Type INTEGER NOT NULL,
                Size FLOAT NOT NULL,
                Weight FLOAT NOT NULL,
                Intellect INTEGER NOT NULL,
                Hobby INTEGER NOT NULL,
                Rarity INTEGER NOT NULL,
                FOREIGN KEY (Owner_ID) REFERENCES Accounts(id)
                )
            """.trimIndent()
        )
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        db.execSQL("DROP TABLE IF EXISTS Accounts")
        db.execSQL("DROP TABLE IF EXISTS MonsterDefinitions")
        db.execSQL("DROP TABLE IF EXISTS PetMonsters")
        onCreate(db)
    }

    //hobbies are stored as integer values, pulls from string list of hobbies in resources
    //monster types are stored as integer values, pulls from string list of monster types in resources
    //rarity stored as integer values from 1-5
    //intellect is an integer value from 1-10
    //discovered value is boolean, stored as integer value (0,1) and reassigned to the table entries based
    // on the user who is logged in and which monsters they've discovered in their pet collection
}