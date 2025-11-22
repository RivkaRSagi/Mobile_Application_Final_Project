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
                Monster_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Name TEXT NOT NULL,
                Type TEXT NOT NULL,
                Size FLOAT NOT NULL,
                Weight FLOAT NOT NULL,
                Intellect INTEGER NOT NULL,
                Hobby INTEGER NOT NULL,
                Rarity INTEGER NOT NULL,
                FOREIGN KEY (Owner_ID) REFERENCES Accounts(id),
                FOREIGN KEY (Type) REFERENCES MonsterDefinitions(Type_Name)
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

    //Accounts functions:


    //function to create a new account
    fun newUser(username: String, password: String){
        val db = writableDatabase
        db.execSQL("INSERT INTO Accounts (username, password) VALUES (?,?)", arrayOf(username, password))
    }

    //function to delete user account
    fun deleteAccount(ID: Int){
        val db = writableDatabase
        db.execSQL("DELETE FROM Accounts WHERE id = ?", arrayOf(ID))
    }

    // function to delete monster definitions table
    fun deleteMonsters(){
        val db= writableDatabase
        db.delete("MonsterDefinitions",null,null)
        db.close()
    }

    //validate user account
    fun validateUserAccount(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Accounts WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    //function to retrieve owner id based on username and password
    fun getOwnerID(username: String, password: String): Int {
        val db = readableDatabase
        var ownerID = 0
        val cursor = db.rawQuery(
            "SELECT id FROM Accounts WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )

        if (cursor.moveToFirst()) {
            ownerID = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        return ownerID
    }

    //get username based on owner id
    fun getUsername(ownerID: Int): String {
        val db = readableDatabase
        var username = ""
        val cursor = db.rawQuery(
            "SELECT username FROM Accounts WHERE id = ?",
            arrayOf(ownerID.toString())
        )

        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
        }
        cursor.close()
        return username
    }


    //PetMonsters functions:

    //function to add monster
    fun addPetMonster(ownerID: Int, thisMonster: Monster){
        val db = writableDatabase
        db.execSQL("INSERT INTO PetMonsters (Owner_ID, Name, Type, Size, Weight, Intellect, Hobby, Rarity) VALUES (?,?,?,?,?,?,?,?)",
            arrayOf(ownerID, thisMonster.petName, thisMonster.type, thisMonster.size, thisMonster.weight, thisMonster.intellect, thisMonster.hobby, thisMonster.rarity))

        db.execSQL("UPDATE MonsterDefinitions SET Discovered = 1 WHERE Type_Name = ?", arrayOf(thisMonster.type))
    }

    //function to delete all monsters in PetMonsters table
    fun deletePetMonsters(){
        val db = writableDatabase
        db.delete("PetMonsters",null,null)
        db.close()
    }

    //function to rename monster
    fun renameMonster(petID: Int, newName: String) {
        val db = writableDatabase
        db.execSQL("UPDATE PetMonsters SET Name = ? WHERE Monster_ID = ?", arrayOf(newName, petID))
    }

    //function to retrieve a single monster based on id
    fun getPetMonster(petID: Int): Monster {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT PetMonsters.Name, PetMonsters.Type, PetMonsters.Size, PetMonsters.Weight, " +
                    "PetMonsters.Intellect, PetMonsters.Hobby, PetMonsters.Rarity, MonsterDefinitions.Image " +
            " FROM PetMonsters JOIN MonsterDefinitions ON PetMonsters.Type = MonsterDefinitions.Type_Name " +
            " WHERE Monster_ID = ?",
            arrayOf(petID.toString())
        )
        var monster = Monster("", "", 0f, 0f, 0, "", 0,"")

        if (cursor.moveToFirst()) {
            monster = Monster(
                cursor.getString(cursor.getColumnIndexOrThrow("Type")),
                cursor.getString(cursor.getColumnIndexOrThrow("Name")),
                cursor.getFloat(cursor.getColumnIndexOrThrow("Size")),
                cursor.getFloat(cursor.getColumnIndexOrThrow("Weight")),
                cursor.getInt(cursor.getColumnIndexOrThrow("Intellect")),
                cursor.getString(cursor.getColumnIndexOrThrow("Hobby")),
                cursor.getInt(cursor.getColumnIndexOrThrow("Rarity")),
                cursor.getString(cursor.getColumnIndexOrThrow("Image"))
            )
        }
        cursor.close()
        return monster
    }

    fun monsterRarity(ownerID: Int, rarity: Int): Int {
        val db = readableDatabase
        var count = 0

        val cursor = db.rawQuery("SELECT COUNT(*) FROM PetMonsters WHERE Owner_ID=? AND Rarity=?",
            arrayOf(ownerID.toString(), rarity.toString()))
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    //function to get monster id based on name, owner, and size
    fun getPetID(ownerID: Int, petName: String, petSize: Float): Int {
        val db = readableDatabase
        var petID = 0
        val cursor = db.rawQuery("SELECT Monster_ID FROM PetMonsters WHERE Owner_ID=? AND Name=? AND Size=?",
            arrayOf(ownerID.toString(), petName, petSize.toString()))
        if (cursor.moveToFirst()) {
            petID = cursor.getInt(cursor.getColumnIndexOrThrow("Monster_ID"))
        }
        cursor.close()
        return petID
    }

    //function to get a list of all pet monsters
    fun getPetMonsters(ownerID: Int, context: Context): List<ListValues.Monster_card> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT PetMonsters.Monster_ID, PetMonsters.Name, PetMonsters.Type, PetMonsters.Size, PetMonsters.Weight, " +
                    "PetMonsters.Intellect, PetMonsters.Hobby, PetMonsters.Rarity, MonsterDefinitions.Image " +
                    " FROM PetMonsters JOIN MonsterDefinitions ON PetMonsters.Type = MonsterDefinitions.Type_Name " +
                    " WHERE Owner_ID = ?",
            arrayOf(ownerID.toString())
        )

        val monsters = mutableListOf<ListValues.Monster_card>()
//        val monsters = ArrayList<Monster>()
//        val myPet = ArrayList<Monster>()

        if(cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("Monster_ID"))
                val type = cursor.getString(cursor.getColumnIndexOrThrow("Type"))
                val petname = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
                val size = cursor.getFloat(cursor.getColumnIndexOrThrow("Size"))
                val weight = cursor.getFloat(cursor.getColumnIndexOrThrow("Weight"))
                val intellect = cursor.getInt(cursor.getColumnIndexOrThrow("Intellect"))
                val hobby = cursor.getString(cursor.getColumnIndexOrThrow("Hobby"))
                val rarity = cursor.getInt(cursor.getColumnIndexOrThrow("Rarity"))
                val image = cursor.getString(cursor.getColumnIndexOrThrow("Image"))

                val imageResource = image.substringBefore(".")
                val imageResId = context.resources.getIdentifier(imageResource, "drawable", context.packageName)


                val monster = ListValues.Monster_card(id,type,imageResId,petname,size,weight,intellect,hobby)
                monsters.add(monster)
            }while (cursor.moveToNext())

//            monsters.add(monster)
        }
        cursor.close()
        return monsters
    }


    //MonsterDefinitions functions:

    //function to set discovered booleans according to logged in user
    fun setDiscovered(ownerID: Int){
        val dbwrite = writableDatabase
        val dbread = readableDatabase
        val cursor = dbread.rawQuery(
            "SELECT Type_Name FROM MonsterDefinitions WHERE Owner_ID = ?",
            arrayOf(ownerID.toString())
        )
        while (cursor.moveToNext()){
            dbwrite.execSQL("UPDATE MonsterDefinitions SET Discovered = 1 WHERE Type_Name = ?",
                arrayOf(cursor.getString(cursor.getColumnIndexOrThrow("Type_Name"))))
        }

        cursor.close()
    }

    //function to reset the monster definitions to undiscovered
    fun resetDiscovered(){
        val db = writableDatabase
        db.execSQL("UPDATE MonsterDefinitions SET Discovered = 0")
    }

//    fun emptyTables(){
//        val db = writableDatabase
//        db.execSQL("DELETE FROM Accounts")
//        db.execSQL("DELETE FROM PetMonsters")
//        db.execSQL("DELETE FROM MonsterDefinitions")
//    }

    fun getMonsterCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM MonsterDefinitions", null)
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }


    //how to add the images to the monsters? currently saved in the drawables
    //function to initialize monster types
    fun initializeMonsterTypes(context: Context){
        val db = writableDatabase
        //common monsters
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Bonzoa", "bonzoa.png", 0, 0))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("GrayLock", "graylock.png", 0, 0))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("ScowlGee", "scowlgee.png", 0, 0))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Squirmy", "squirmy.png", 0, 0))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("SnailBoo", "snailboo.png", 0, 0))

        //uncommon monsters
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Breezle", "breezle.png", 0, 1))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("KrikiToo", "krikitoo.png", 0, 1))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("PiggyWhirl", "piggywhirl.png", 0, 1))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Rattimus", "rattimus.png", 0, 1))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Salmoosus", "salmosus.png", 0, 1))

        //rare monsters
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Arrow", "arrow.png", 0, 2))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("CurlyGator", "curlygator.png", 0, 2))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Freezy", "freezy.png", 0, 2))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("PsyCactus", "psyactus.png", 0, 2))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("SkelyWalker", "skelywalker.png", 0, 2))

        //epic monsters
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("CheckerLord", "checkerlord.png", 0, 3))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Creepi", "creepi.png", 0, 3))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("FireFly", "firefly.png", 0, 3))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("FlyMousse", "flymousse.png", 0, 3))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Granite", "granite.png", 0, 3))


        //legendary monsters
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Arvolok", "arvolok.png", 0, 4))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Awstrich", "awstrich.png", 0, 4))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Bramble", "bramble.png", 0, 4))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("Clastan", "clastan.png", 0, 4))
        db.execSQL("INSERT INTO MonsterDefinitions (Type_Name, Image, Discovered, Rarity) VALUES (?,?,?,?)",
            arrayOf("TenTwirl", "tentwirl.png", 0, 4))

    }

    //hobbies are stored as integer values, pulls from string list of hobbies in resources
    //monster types are stored as integer values, pulls from string list of monster types in resources
    //rarity stored as integer values from 1-5
    //intellect is an integer value from 1-10
    //discovered value is boolean, stored as integer value (0,1) and reassigned to the table entries based
    // on the user who is logged in and which monsters they've discovered in their pet collection
}