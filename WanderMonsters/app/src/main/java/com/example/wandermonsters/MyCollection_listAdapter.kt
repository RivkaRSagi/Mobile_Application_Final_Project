package com.example.wandermonsters

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.widget.EditText

class MyCollection_listAdapter (
    private val context: Context,
    private val listItem: List<ListValues.Monster_card>
) : ArrayAdapter<ListValues.Monster_card>(context,0, listItem){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.my_collection_list_item, parent, false)
        val image = view.findViewById<ImageView>(R.id.CreaturePicture)
        val textPetName = view.findViewById<TextView>(R.id.petName)
        val textMonsterName = view.findViewById<TextView>(R.id.monsterName)

        val item = listItem[position]
        image.setImageResource(item.monster_picture)
        textPetName.text = item.pet_name
        textMonsterName.text = item.monster_type

        view.setOnClickListener {
            showMonsterCard(item)

        }

        return view
    }

    fun showMonsterCard(item: ListValues.Monster_card){
        val view = LayoutInflater.from(context).inflate(R.layout.activity_monster_card, null)
        val monsterType= view.findViewById<TextView>(R.id.monsterType)
        val monsterImage = view.findViewById<ImageView>(R.id.monsterImage)
        val petName = view.findViewById<EditText>(R.id.petName)
        val size = view.findViewById<TextView>(R.id.size)
        val intellect = view.findViewById<TextView>(R.id.intellect)
        val weight = view.findViewById<TextView>(R.id.weight)
        val hobby = view.findViewById<TextView>(R.id.hobby)

        monsterType.text=item.monster_type
        monsterImage.setImageResource(item.monster_picture)
        petName.setText(item.pet_name)
        size.text=item.size.toString()
        intellect.text=item.intellect.toString()
        weight.text=item.weight.toString()
        hobby.text=item.hobby

        //change pet name in monster card once user clicks enter
        petName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)
            ) {
                val newName = v.text.toString()

                val dbHelper = DatabaseHelper(context)
                dbHelper.renameMonster(item.monster_id, newName)
                item.pet_name = newName
                petName.setText(newName)
                notifyDataSetChanged()
                false
            } else {
                false
            }
        }


        AlertDialog.Builder(context)
            .setView(view)
            .show()
            .setCancelable(true)
    }


}