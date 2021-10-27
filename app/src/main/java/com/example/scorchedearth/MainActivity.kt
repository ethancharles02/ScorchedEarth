package com.example.scorchedearth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Initialize View and set it as the current view
        val myCanvasView = MyCanvasView(this)

        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)

        setContentView(myCanvasView)
    }
}