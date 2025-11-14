package com.example.wandermonsters

import android.content.Context
import android.content.res.Resources
import java.util.Random

class Monster (
    var type: String,
    var petName: String,
    var size: Float,
    var weight: Float,
    var intellect: Int,
    var hobby: String,
    var rarity: Int
){

    companion object {

        fun createRandomMonster(context: Context): Monster {
            val monsterTypes = context.resources.getStringArray(R.array.monsters)
            val hobbies = context.resources.getStringArray(R.array.hobbies)
            val rarityType = context.resources.getStringArray(R.array.rarity)

            val random = Random()
            val randomType = random.nextInt(25)
            val randomHobby = random.nextInt(20)

            //types 1-5 are common, 6-10 are uncommon, 11-15 are rare, 16-20 are epic, 21-25 are legendary
            var rarity = 0
            if (randomType < 5) {
                rarity = 0
            } else if (randomType > 4 && randomType < 10) {
                rarity = 1
            } else if (randomType > 9 && randomType < 15) {
                rarity = 2
            } else if (randomType > 14 && randomType < 20) {
                rarity = 3
            } else if (randomType > 19 && randomType < 25) {
                rarity = 4
            }


            val newMonster = Monster(
                monsterTypes[randomType],
                monsterTypes[randomType],
                (1..100).random().toFloat(),
                (1..100).random().toFloat(),
                (1..10).random(),
                hobbies[randomHobby],
                rarity
            )

            return newMonster
        }

    }

    //to call the random monster function:
    //val newMonster = Monster.createRandomMonster(this)
}