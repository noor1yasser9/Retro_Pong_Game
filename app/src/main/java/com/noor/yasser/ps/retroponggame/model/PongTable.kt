package com.noor.yasser.ps.retroponggame.model

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.noor.yasser.ps.retroponggame.R
import com.noor.yasser.ps.retroponggame.utils.*
import java.util.*
import kotlin.math.abs

class PongTable : SurfaceView, SurfaceHolder.Callback {
    var game: GameThread? = null
        private set
    private var mStatus: TextView? = null
    private var mScorePlayer: TextView? = null
    private var mScoreOpponent: TextView? = null
    var player: Player? = null
        private set
    private var mOpponent: Player? = null
    var ball: Ball? = null
        private set
    private var mNetPaint: Paint? = null
    private var mTalbeBoundsPaint: Paint? = null
    private var mTableWidth = 0
    private var mTableHeight = 0
    private var mContext: Context? = null
    var mHolder: SurfaceHolder? = null
    private var mAiMovePorbability = 0f
    private var moving = false
    private var mlastTouchY = 0f

    fun initPongTable(ctx: Context, attr: AttributeSet?) {
        mContext = ctx
        mHolder = holder
        mHolder!!.addCallback(this)
        game = GameThread(this.context, mHolder!!, this, object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                mStatus!!.visibility = msg.data.getInt("visibility")
                mStatus!!.text = msg.data.getString("text")
            }
        }, object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                mScorePlayer!!.text = msg.data.getString("player")
                mScoreOpponent!!.text = msg.data.getString("opponent")
            }
        })



        val a = ctx.obtainStyledAttributes(attr, R.styleable.PongTable)
        val racketHeight = a.getInteger(R.styleable.PongTable_racketHeight, 440)
        val racketWidth = a.getInteger(R.styleable.PongTable_racketWidth, 50)
        val ballRadius = a.getInteger(R.styleable.PongTable_ballRadius, 25)

        // Set Player
        val playerPaint = Paint()
        playerPaint.isAntiAlias = true
        playerPaint.color = ContextCompat.getColor(mContext!!, R.color.player_color)
        player = Player(racketWidth, racketHeight, paint = playerPaint)

        // Set Opponent
        val opponentPaint = Paint()
        opponentPaint.isAntiAlias = true
        opponentPaint.color = ContextCompat.getColor(mContext!!, R.color.opponent_color)
        mOpponent = Player(racketWidth, racketHeight, paint = opponentPaint)



        // Set Ball
        val ballPaint = Paint()
        ballPaint.isAntiAlias = true
        ballPaint.color = ContextCompat.getColor(mContext!!, R.color.ball_color)
        ball = Ball(ballRadius.toFloat(), ballPaint)

        // Draw Middle lines
        mNetPaint = Paint()
        mNetPaint!!.isAntiAlias = true
        mNetPaint!!.color = Color.WHITE
        mNetPaint!!.alpha = 100
        mNetPaint!!.style = Paint.Style.FILL_AND_STROKE
        mNetPaint!!.strokeWidth = 10f
        mNetPaint!!.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)

        // Draw Bounds
        mTalbeBoundsPaint = Paint()
        mTalbeBoundsPaint!!.isAntiAlias = true
        mTalbeBoundsPaint!!.color = ContextCompat.getColor(mContext!!, R.color.player_color)
        mTalbeBoundsPaint!!.style = Paint.Style.STROKE
        mTalbeBoundsPaint!!.strokeWidth = 15f
        mAiMovePorbability = 0.8f
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(ContextCompat.getColor(mContext!!, R.color.table_color))
        canvas.drawRect(
            0f, 0f, mTableWidth.toFloat(), mTableHeight.toFloat(),
            mTalbeBoundsPaint!!
        )
        val middle = mTableWidth / 2
        canvas.drawLine(
            middle.toFloat(), 1f, middle.toFloat(), (mTableHeight - 1).toFloat(),
            mNetPaint!!
        )
        game!!.setScoreText(
            player!!.score.toString(), mOpponent!!.score.toString()
        )
        player!!.draw(canvas)
        mOpponent!!.draw(canvas)
        ball!!.draw(canvas)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initPongTable(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initPongTable(context, attrs)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        game!!.setRunning(true)
        game!!.start()
    }

    override fun surfaceChanged(
        surfaceHolder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        mTableWidth = width
        mTableHeight = height
        game!!.setUpNewRound()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        var retry = true
        game!!.setRunning(false)
        while (retry) {
            try {
                game!!.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun doAI() {
        if (mOpponent!!.bounds.top > ball!!.cy) {
            movePlayer(
                mOpponent,
                mOpponent!!.bounds.left,
                mOpponent!!.bounds.top - PHY_RACQUET_SPEED
            )
        } else if (mOpponent!!.bounds.top + mOpponent!!.requestHeight < ball!!.cy) {
            movePlayer(
                mOpponent,
                mOpponent!!.bounds.left,
                mOpponent!!.bounds.top + PHY_RACQUET_SPEED
            )
        }
    }

    fun update(canvas: Canvas?) {
        if (checkCollisionPlayer(player, ball)) {
            handleCollision(player, ball)
        } else if (checkCollisionPlayer(mOpponent, ball)) {
            handleCollision(mOpponent, ball)
        } else if (checkCollisionWithTopOrBottomWall()) {
            ball!!.velocity_y = -ball!!.velocity_y
        } else if (checkCollisionWithLeftWall()) {
            game!!.setState(STATE_LOSE)
            return
        } else if (checkCollisionWithRightWall()) {
            ball!!.radius
            game!!.setState(STATE_WIN)
            return
        }
        if (Random(System.currentTimeMillis()).nextFloat() < mAiMovePorbability) doAI()
        ball!!.moveBall(canvas!!)
    }

    private fun checkCollisionPlayer(player: Player?, ball: Ball?): Boolean {
        return player!!.bounds.intersects(
            ball!!.cx - ball.radius,
            ball.cy - ball.radius,
            ball.cx + ball.radius,
            ball.cy + ball.radius
        )
    }

    private fun checkCollisionWithTopOrBottomWall(): Boolean {
        return ball!!.cy <= ball!!.radius || ball!!.cy + ball!!.radius >= mTableHeight - 1
    }

    private fun checkCollisionWithLeftWall(): Boolean {
        return ball!!.cx <= ball!!.radius
    }

    private fun checkCollisionWithRightWall(): Boolean {
        return ball!!.cx + ball!!.radius >= mTableWidth - 1
    }

    private fun handleCollision(player: Player?, ball: Ball?) {
        ball!!.velocity_x = -ball.velocity_x * 1.05f
        if (player === this.player) {
            ball.cx = player!!.bounds.right + ball.radius
        } else if (player === mOpponent) {
            ball.cx = mOpponent!!.bounds.left - ball.radius
            PHY_RACQUET_SPEED *= 1.05f
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!game!!.SensorsOn()) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> if (game!!.isBetweenRounds) {
                    game!!.setState(STATE_RUNNING)
                } else {
                    if (isTouchOnRacket(event, player)) {
                        moving = true
                        mlastTouchY = event.y
                    }
                }
                MotionEvent.ACTION_MOVE -> if (moving) {
                    val y = event.y
                    val dy = y - mlastTouchY
                    mlastTouchY = y
                    movePlayerRacquet(dy, player)
                }
                MotionEvent.ACTION_UP -> moving = false
            }
        } else {
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (game!!.isBetweenRounds) {
                    game!!.setState(STATE_RUNNING)
                }
            }
        }
        return true
    }

    private fun isTouchOnRacket(event: MotionEvent, mPlayer: Player?): Boolean {
        return mPlayer!!.bounds.contains(event.x, event.y)
    }

    private fun movePlayerRacquet(dy: Float, player: Player?) {
        synchronized(mHolder!!) {
            movePlayer(
                player,
                player!!.bounds.left,
                player.bounds.top + dy
            )
        }
    }

    @Synchronized
    fun movePlayer(player: Player?, left: Float, top: Float) {
        var left = left
        var top = top
        if (left < 2) {
            left = 2f
        } else if (left + player!!.requestWidth >= mTableWidth - 2) {
            left = (mTableWidth - player.requestWidth - 2).toFloat()
        }
        if (top < 0) {
            top = 0f
        } else if (top + player!!.requestHeight >= mTableHeight) {
            top = (mTableHeight - player.requestHeight - 1).toFloat()
        }
        player!!.bounds.offsetTo(left, top)
    }

    fun setupTable() {
        placeBall()
        placePlayers()
    }

    private fun placePlayers() {
        player!!.bounds.offsetTo(2f, ((mTableHeight - player!!.requestHeight) / 2).toFloat())
        mOpponent!!.bounds.offsetTo(
            (mTableWidth - mOpponent!!.requestWidth).toFloat()
                    - 2,
            ((mTableHeight - mOpponent!!.requestHeight) / 2).toFloat()
        )
    }

    private fun placeBall() {
        ball!!.cx = (mTableWidth / 2).toFloat()
        ball!!.cy = (mTableHeight / 2).toFloat()
        ball!!.velocity_y = ball!!.velocity_y / abs(ball!!.velocity_y) * PHY_BALL_SPEED
        ball!!.velocity_x = ball!!.velocity_x / abs(ball!!.velocity_x) * PHY_BALL_SPEED
    }

    fun getMOpponent(): Player? {
        return mOpponent
    }

    fun setScorePlayer(view: TextView?) {
        mScorePlayer = view
    }

    fun setScoreOpponent(view: TextView?) {
        mScoreOpponent = view
    }

    fun setStatus(view: TextView?) {
        mStatus = view
    }


}