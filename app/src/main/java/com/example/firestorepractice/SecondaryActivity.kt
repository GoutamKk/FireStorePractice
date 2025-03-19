package com.example.firestorepractice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomappbar.BottomAppBar

class SecondaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_secondary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_view)

        ViewCompat.setOnApplyWindowInsetsListener(bottomAppBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

//        findViewById<NestedScrollView>(R.id.nested_scroll).setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (scrollY > oldScrollY){
//                Toast.makeText(this,"X asix scroll",Toast.LENGTH_SHORT).show()
//            }
//        }


    }

}