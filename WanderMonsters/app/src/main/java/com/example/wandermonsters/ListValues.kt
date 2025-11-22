package com.example.wandermonsters

class ListValues {

    data class Dictionary_table(val monster_image: Int, val monster_Type: String, val rarity: String)

    data class Monster_card( val monster_id: Int,val monster_type: String, val monster_picture: Int, var pet_name: String, val size: Float,
        val weight: Float, val intellect: Int, val hobby: String)

}