package com.example.scorchedearth

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import java.lang.Math.toRadians
import kotlin.math.*

//private const val STROKE_WIDTH = 12f

// Initializes the View class
class MyCanvasView (context: Context) : View(context) {
//    extraCanvas and extraBitmap are what are drawn to
    private lateinit var extraCanvas : Canvas
    private lateinit var extraBitmap : Bitmap

    private val backgroundColor = ContextCompat.getColor(context, R.color.black)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.white, null)

//    private val paint = Paint().apply {
//        color = drawColor
//        // Smooths out edges of what is drawn without affecting shape.
//        isAntiAlias = true
//        // Dithering affects how colors with higher-precision than the device are down-sampled.
//        isDither = true
//        style = Paint.Style.STROKE // default: FILL
//        strokeJoin = Paint.Join.ROUND // default: MITER
//        strokeCap = Paint.Cap.ROUND // default: BUTT
//        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
//    }

//    Calculates a list of x amounts in an explosion
//    These refer to how many pixels across any y counterpart is. ie. with a radius of 30, there are 30 items in the list with index 0 being the largest (since it starts polling at degree 0)
//    There amounts refer to only one quarter of the circle, if it says 5, there are 5 pixels from that index starting at the middle and going to the edge of the circle
    private fun explosionXList(radius: Int): MutableList<Int> {
        val x_list = mutableListOf<Int>()
        var x = radius
        var y = 0

//        Decides how many polls to correctly get all the x amounts
        val num_deg_polls = radius * 3

//        Loops through the number of degree polls adding to the x_list as it crosses new y_thresholds
        for (degree in 0..num_deg_polls) {
            val y_dist = round(radius * sin(toRadians((degree.toFloat() * (90f / num_deg_polls.toFloat())).toDouble()))).toInt()
            val x_dist = round(radius * cos(toRadians((degree.toFloat() * (90f / num_deg_polls.toFloat())).toDouble()))).toInt()
            if (x_list.isEmpty()) {
                x_list.add(x_dist)
            }
            if (y_dist != y) {
                x_list.add(x_dist)
                y = y_dist
            }

//              Finishes early if the size properly matches the radius
            if (x_list.size == radius) {
                return x_list
            }
        }
        return x_list
    }

//    The size in pixels of the default explosion
    private val explosion_radius = 100
    private val explosion_x_list = explosionXList(explosion_radius)

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {

        super.onSizeChanged(width, height, oldWidth, oldHeight)

        // Clean up the bitmap if it is exists
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

//        Create the canvas and draw the background color to it
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

//        Randomizes terrain generation by a simple maximum increase or decrease of 3 pixel height compared to the last one
        var y_val = (0..height - 1).random()
        for (x in 0..width - 1) {
            y_val += (-3..3).random()
            for (y in y_val..height - 1) {
                extraBitmap.setPixel(x, y, drawColor)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerIndex = event.actionIndex

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> return true
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {

//                Sets all of the pixels within an explosion radius where the user tapped
                val pointX = event.getX(pointerIndex).toInt()
                val pointY = event.getY(pointerIndex).toInt()
                for (y in -(explosion_radius - 1)..explosion_radius - 1) {
                    for (x in -explosion_x_list[abs(y)]..explosion_x_list[abs(y)]) {
                        val new_point_x = pointX + x
                        val new_point_y = pointY + y
                        if (new_point_x < 0 || new_point_x >= width) {
                            continue
                        }
                        else if (new_point_y < 0 || new_point_y >= height) {
                            continue
                        }
                        else {
                            extraBitmap.setPixel(new_point_x, new_point_y, backgroundColor)
                        }
                    }
                }

//                Optional code for drawing a circle which is much more efficient than my homemade version
//                I am not using it right now since it might be hard to do actual calculations later based on destruction
//                val pointX = event.getX(pointerIndex)
//                val pointY = event.getY(pointerIndex)
//                extraCanvas.drawCircle(pointX, pointY, explosion_radius.toFloat(), vert_paint)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    override fun onDraw(canvas: Canvas) {
//        Draws the canvas
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }
}