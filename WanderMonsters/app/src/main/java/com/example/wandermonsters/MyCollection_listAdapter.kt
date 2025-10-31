package com.example.wandermonsters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MyCollection_listAdapter (
    private val context: Context,
    private val listItem: List<ListValues.MyCollection_listItem>
) : ArrayAdapter<ListValues.MyCollection_listItem>(context, R.layout.my_collection_list_item,
    listItem){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.my_collection_list_item, parent, false)
        val image = view.findViewById<ImageView>(R.id.CreaturePicture)
        val textPetName = view.findViewById<TextView>(R.id.petName)
        val textMonsterName = view.findViewById<TextView>(R.id.monsterName)

        val item = listItem[position]
        image.setImageResource(item.monsterImage)
        textPetName.text = item.petName
        textMonsterName.text = item.monsterType

        return view
    }


}