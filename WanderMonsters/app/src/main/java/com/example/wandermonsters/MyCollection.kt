package com.example.wandermonsters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.wandermonsters.ListValues

//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [MyCollection.newInstance] factory method to
// * create an instance of this fragment.
// */
class MyCollection : Fragment() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var monster : List<ListValues.Monster_card>
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listView = view.findViewById<ListView>(R.id.MyList)
        dbHelper = DatabaseHelper(requireContext())
        val currentUserID = dbHelper.getOwnerID("testUser", "123")
        val mon_item = dbHelper.getPetMonsters(currentUserID, requireContext())
        monster = mon_item


//        val items = listOf(
//            ListValues.Monster_card("SnailBoo",R.drawable.snailboo,"Joe", 3.31f, 23f,9, "Skating"),
//            ListValues.Monster_card("GreyLock", R.drawable.graylock, "Bob", 22.1f, 2220f, 2, "Running")
//        )

        val adapter = MyCollection_listAdapter(requireContext(), monster)
        listView.adapter = adapter
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MyCollection.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MyCollection().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
//}