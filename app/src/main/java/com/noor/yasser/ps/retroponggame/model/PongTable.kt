package com.noor.yasser.ps.retroponggame.model

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class PongTable : SurfaceView, SurfaceHolder.Callback {

    val TAG = "PongTable"
    private var mPlayer: Player? = null
    private var mOpponent: Player? = null
    private var mBall: Ball? = null
    private var mNetPaint: Paint? = null
    private var mTableBoundPaint: Paint? = null
    private var mTableWidth: Int? = null
    private var mTableHeight: Int? = null
    private var mContext: Context? = null

    lateinit var mHolder: SurfaceHolder
    private var mAiMoveProbability: Float? = null
    private var moving: Boolean = false
    private var mLastTouchY: Float? = null


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun surfaceCreated(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    fun initPongTable(context: Context,attr: AttributeSet) {

    }

}