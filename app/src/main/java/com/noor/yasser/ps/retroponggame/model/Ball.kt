package com.noor.yasser.ps.retroponggame.model

import android.graphics.Canvas
import android.graphics.Paint
import com.noor.yasser.ps.retroponggame.utils.PHY_BALL_SPEED

class Ball(val radius: Float, private val paint: Paint) {
    var cx = 0f
    var cy = 0f
    var velocity_x: Float = PHY_BALL_SPEED
    var velocity_y: Float = PHY_BALL_SPEED
    fun draw(canvas: Canvas) {
        canvas.drawCircle(cx, cy, radius.toFloat(), paint)
    }

    fun moveBall(canvas: Canvas) {
        cx += velocity_x
        cy += velocity_y
        if (cy < radius) {
            cy = radius.toFloat()
        } else if (cy + radius >= canvas.height) {
            cy = (canvas.height - radius - 1).toFloat()
        }
    }

    override fun toString(): String {
        return "Cx = " + cx + "Cy" + cy + "velX= " + velocity_x + " velY= " + velocity_y
    }

}