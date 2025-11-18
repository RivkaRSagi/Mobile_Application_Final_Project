package com.example.wandermonsters

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MonsterCard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_monster_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//
//        val monsterType= findViewById<TextView>(R.id.monsterType)
//        val monsterImage = findViewById<ImageView>(R.id.monsterImage)
//        val petName = findViewById<TextView>(R.id.petName)
//        val size = findViewById<TextView>(R.id.size)
//        val intellect = findViewById<TextView>(R.id.intellect)
//        val weight = findViewById<TextView>(R.id.weight)
//        val hobby = findViewById<TextView>(R.id.hobby)
//
//        val item = ListValues.Monster_card("SnailBoo",R.drawable.snail_boo,"Joe", 3.31f, 23f,9, "Skating")
//
//        monsterType.text=item.monster_type
//        monsterImage.setImageResource(item.monster_picture)
//        petName.text=item.pet_name
//        size.text=item.size.toString()
//        intellect.text=item.intellect.toString()
//        weight.text=item.weight.toString()
//        hobby.text=item.hobby



    }
}