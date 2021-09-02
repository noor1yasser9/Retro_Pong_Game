package com.noor.yasser.ps.retroponggame.model

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.noor.yasser.ps.retroponggame.R
import com.noor.yasser.ps.retroponggame.utils.PHY_RACQUET_SPEED

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


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initPongTable(context!!, attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initPongTable(context!!, attrs!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(ContextCompat.getColor(mContext!!, R.color.table_color))
        canvas?.drawRect(
            0f,
            0f,
            mTableWidth!!.toFloat(),
            mTableHeight!!.toFloat(),
            mTableBoundPaint!!
        )

        val middle = mTableWidth!! / 2
        canvas?.drawLine(
            middle.toFloat(),
            1f,
            middle.toFloat(),
            mTableHeight!!.toFloat() - 1,
            mNetPaint!!
        )


        mPlayer?.draw(canvas!!)
        mOpponent?.draw(canvas!!)
        mBall?.draw(canvas!!)
    }

    fun doAI() {
        if (mOpponent!!.bounds.top > mBall!!.cy) {
            movePlayer(
                mOpponent!!,
                mOpponent!!.bounds.left,
                mOpponent!!.bounds.top - PHY_RACQUET_SPEED
            )
        } else if (mOpponent!!.bounds.top + mOpponent!!.requestHeight < mBall!!.cy) {
            movePlayer(
                mOpponent!!,
                mOpponent!!.bounds.left,
                mOpponent!!.bounds.top + PHY_RACQUET_SPEED
            )
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mTableWidth = width
        mTableHeight = height
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    fun movePlayerRacquet(dy: Float, player: Player) {
        synchronized(mHolder) {
            movePlayer(player, player.bounds.left, player.bounds.top + dy)
        }
    }

    fun isTouchOnRacket(event: MotionEvent?, mPlayer: Player): Boolean {
        return mPlayer.bounds.contains(event!!.x, event.y)

    }

    @Synchronized
    fun movePlayer(player: Player, left: Float, top: Float) {
        var mLeft = left
        if (left < 2) {
            mLeft = 2f
        } else if (mLeft + player.requestWidth >= mTableWidth!! - 2) {
            mLeft = (mTableWidth!! - player.requestWidth - 2).toFloat()
        }
        var mTop = top
        if (top < 0) {
            mTop = 0f
        } else if (top + player.requestHeight > mTableHeight!!) {
            mTop = (mTableHeight!! - player.requestHeight - 1).toFloat()
        }

        player.bounds.offset(mLeft, mTop)
    }

    fun initPongTable(context: Context, attr: AttributeSet) {
        mContext = context
        mHolder = holder
        mHolder.addCallback(this)

        //Game Thread or Game Loop initialize
        val a: TypedArray = context.obtainStyledAttributes(attr, R.styleable.PongTable)
        val racketHeight = a.getInteger(R.styleable.PongTable_racketHeight, 340)
        val racketWidth = a.getInteger(R.styleable.PongTable_racketWidth, 100)
        val ballRadius = a.getInteger(R.styleable.PongTable_ballRadius, 25)

        // Set Player

        // Set Player
        val playerPaint = Paint()
        playerPaint.isAntiAlias = true
        playerPaint.color = ContextCompat.getColor(mContext!!, R.color.player_color)
        mPlayer = Player(racketWidth, racketHeight, paint = playerPaint)

        // Set Opponent

        // Set Opponent
        val opponentPaint = Paint()
        opponentPaint.isAntiAlias = true
        opponentPaint.color = ContextCompat.getColor(mContext!!, R.color.opponent_color)
        mOpponent = Player(racketWidth, racketHeight, paint = opponentPaint)

        // Set Ball

        // Set Ball
        val ballPaint = Paint()
        ballPaint.isAntiAlias = true
        ballPaint.color = ContextCompat.getColor(mContext!!, R.color.ball_color)
        mBall = Ball(radius = ballRadius, paint = ballPaint)

        // Draw Middle lines

        // Draw Middle lines
        mNetPaint = Paint()
        mNetPaint!!.isAntiAlias = true
        mNetPaint!!.color = Color.WHITE
        mNetPaint!!.alpha = 80
        mNetPaint!!.style = Paint.Style.FILL_AND_STROKE
        mNetPaint!!.strokeWidth = 10f
        mNetPaint!!.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)

        // Draw Bounds

        // Draw Bounds
        mTableBoundPaint = Paint()
        mTableBoundPaint?.isAntiAlias = true
        mTableBoundPaint?.color = ContextCompat.getColor(mContext!!, R.color.table_color)
        mTableBoundPaint?.style = Paint.Style.STROKE
        mTableBoundPaint?.strokeWidth = 15f

        mAiMoveProbability = 0.8f


    }


}