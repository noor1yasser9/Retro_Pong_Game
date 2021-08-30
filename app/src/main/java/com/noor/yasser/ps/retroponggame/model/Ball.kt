package com.noor.yasser.ps.retroponggame.model

import android.graphics.Canvas
import android.graphics.Paint

data class Ball(
    var cx: Float = 0f,
    var cy: Float = 0f,
    var velocity_x: Float = 0f,
    var velocity_y: Float = 0f,
    var radius: Int = 0,
    val paint: Paint
) {

    public fun draw(canvas: Canvas) {
        canvas.drawCircle(cx, cy, radius.toFloat(), paint)
    }

    fun moveBall(canvas: Canvas) {
        cx += velocity_x
        cy += velocity_y
        if (cy < radius) {
            cy = radius.toFloat()
        } else if (cy + radius > canvas.height) {
            cy = (canvas.height - radius - 1).toFloat()
        }
    }
}