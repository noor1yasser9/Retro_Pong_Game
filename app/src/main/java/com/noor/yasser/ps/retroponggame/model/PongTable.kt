package com.noor.yasser.ps.retroponggame.model

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.noor.yasser.ps.retroponggame.R

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
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    fun initPongTable(context: Context, attr: AttributeSet) {
        mContext = context
        mHolder = holder
        mHolder.addCallback(this)

        //Game Thread or Game Loop initialize

        val a = context.obtainStyledAttributes(attr, R.styleable.PongTable)
        val racketHeight = a.getInteger(R.styleable.PongTable_racketHeight, 340)
        val racketWidth = a.getInteger(R.styleable.PongTable_racketWidth, 100)
        val ballRadius = a.getInteger(R.styleable.PongTable_ballRadius, 25)

        //set Player
        val playerPaint = Paint()
        playerPaint.isAntiAlias = true
        playerPaint.color = ContextCompat.getColor(context, R.color.player_color)
        mPlayer =
            Player(requestHeight = racketHeight, requestWidth = racketWidth, paint = playerPaint)

        //set Opponent
        val opponentPaint = Paint()
        opponentPaint.isAntiAlias = true
        opponentPaint.color = ContextCompat.getColor(context, R.color.opponent_color)
        mPlayer =
            Player(requestHeight = racketHeight, requestWidth = racketWidth, paint = playerPaint)

        //set Ball
        val ballPaint = Paint()
        ballPaint.isAntiAlias = true
        ballPaint.color = ContextCompat.getColor(context, R.color.ball_color)
        mBall =
            Ball(radius = ballRadius,paint = ballPaint)

        //Draw middle line
        mNetPaint = Paint()
        mNetPaint?.isAntiAlias = true
        mNetPaint?.color = Color.WHITE
        mNetPaint?.alpha = 80
        mNetPaint?.style = Paint.Style.FILL_AND_STROKE
        mNetPaint?.strokeWidth = 10f
        mNetPaint?.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)


        //Draw Bounds
        mTableBoundPaint = Paint()
        mTableBoundPaint?.isAntiAlias = true
        mTableBoundPaint?.color = Color.BLACK
        mTableBoundPaint?.style = Paint.Style.STROKE
        mTableBoundPaint?.strokeWidth = 15f

        mAiMoveProbability = 0.8f



    }

}