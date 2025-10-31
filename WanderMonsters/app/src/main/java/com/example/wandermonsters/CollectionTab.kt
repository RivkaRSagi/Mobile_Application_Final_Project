package com.example.wandermonsters

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CollectionTab : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_collection_tab)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //create instance of tab bar
        val tab = findViewById<TabLayout>(R.id.tabLayout)

        //create instance of page content
        val tabPage = findViewById<ViewPager2>(R.id.viewPager)

        //using custom adapter to map out elements of the tab bar
        val adapter = Collection_tab_adapter(this)
        tabPage.adapter=adapter

        TabLayoutMediator(tab, tabPage) {tab, position ->
            tab.text = when (position) {
                0 -> "My Collection"
                1 -> "Monster Dictionary"
                else -> "Tab ${position + 1}"
            }
        }.attach()


    }
}