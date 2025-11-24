package com.example.wandermonsters

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

class MonsterDictionary : Fragment() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var monster : List<ListValues.Dictionary_table>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monster_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val table = view.findViewById<TableLayout>(R.id.monsterTable)
        dbHelper = DatabaseHelper(requireContext())
        val tableData = dbHelper.dictionaryTable(requireContext())
        monster = tableData


//        val tableData = listOf(
//            ListValues.Dictionary_table(R.drawable.snailboo, "SnailBoo", "Common"),
//            ListValues.Dictionary_table(R.drawable.graylock, "GrayLock", "Rare"),
//            ListValues.Dictionary_table(R.drawable.bonzoa, "Bonzoa", " Super Rare")
//        )

        val columns = 3
        for (i in tableData.indices step columns){

            val row = TableRow(context)

            for(j in 0 until columns){
                val index = i+j
                if (index < tableData.size){
                    val cellData = tableData[index]

                    val squares = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(8,8,8,8)
                    layoutParams = TableRow.LayoutParams(0, 400, 1f)
//                        setBackgroundResource(R.drawable.table_sqaure)
                    }

                    val imageView = ImageView(context).apply {

                        setImageResource(cellData.monster_image)
                        if (cellData.discovered == 1) {
                            clearColorFilter()
                        } else {
                            colorFilter = android.graphics.PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)

                        }
                        layoutParams= LinearLayout.LayoutParams(224,224)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }

                    val textView = TextView(context).apply {

                        if (cellData.discovered == 1) {
                            setText(cellData.monster_Type)
                        } else{
                            setText("???")
                        }

                        gravity = Gravity.CENTER
                        textSize = 18f
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    val textView2 = TextView(context).apply {
                        if (cellData.discovered == 1) {
                            text = when (cellData.rarity) {
                                0 -> "Common"
                                1 -> "Uncommon"
                                2 -> "Rare"
                                3 -> "Epic"
                                4 -> "Legendary"
                                else -> ""
                            }
                        } else{
                            setText("???")
                        }
                        gravity = Gravity.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 14f
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    squares.addView(imageView)
                    squares.addView(textView)
                    squares.addView(textView2)
                    row.addView(squares)


                }
            }

            table.addView(row)
        }

    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MonsterDictionary.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MonsterDictionary().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}