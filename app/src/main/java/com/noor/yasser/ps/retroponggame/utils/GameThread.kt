package com.noor.yasser.ps.retroponggame.utils

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.SurfaceHolder
import android.view.View
import com.noor.yasser.ps.retroponggame.R
import com.noor.yasser.ps.retroponggame.model.PongTable
import java.lang.Exception

class GameThread(
    private val mCtx: Context,
    private val mSurfaceHolder: SurfaceHolder,
    private val mPongTable: PongTable,
    private val mGameStatusHandler: Handler,
    private val mScoreHandler: Handler
) :
    Thread() {
    private val mSensorsOn = false
    private var mRun = false
    private var mGameState = 0
    private val mRunLock: Any = Any()
    override fun run() {
        var mNextGameTick = SystemClock.uptimeMillis()
        val skipTicks = 1000 / PHYS_FPS
        while (mRun) {
            var c: Canvas? = null
            try {
                c = mSurfaceHolder.lockCanvas(null)
                if (c != null) {
                    synchronized(mSurfaceHolder) {
                        if (mGameState == STATE_RUNNING) {
                            mPongTable.update(c)
                        }
                        synchronized(mRunLock) {
                            if (mRun) {
                                mPongTable.draw(c)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c)
                }
            }
            mNextGameTick += skipTicks.toLong()
            val sleepTime = mNextGameTick - SystemClock.uptimeMillis()
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setState(state: Int) {
        synchronized(mSurfaceHolder) {
            mGameState = state
            val res = mCtx.resources
            when (mGameState) {
                STATE_READY -> setUpNewRound()
                STATE_RUNNING -> hideStatusText()
                STATE_WIN -> {
                    setStatusText(res.getString(R.string.mode_win))
                    mPongTable.player!!.score++
                    setUpNewRound()
                }
                STATE_LOSE -> {
                    setStatusText(res.getString(R.string.mode_loss))
                    mPongTable.getMOpponent()!!.score++
                    setUpNewRound()
                }
                STATE_PAUSED -> setStatusText(res.getString(R.string.mode_paused))
            }
        }
    }

    fun setUpNewRound() {
        synchronized(mSurfaceHolder) { mPongTable.setupTable() }
    }

    fun setRunning(running: Boolean) {
        synchronized(mRunLock) { mRun = running }
    }

    fun SensorsOn(): Boolean {
        return mSensorsOn
    }

    val isBetweenRounds: Boolean
        get() = mGameState != STATE_RUNNING

    private fun setStatusText(text: String) {
        val msg = mGameStatusHandler.obtainMessage()
        val b = Bundle()
        b.putString("text", text)
        b.putInt("visibility", View.VISIBLE)
        msg.data = b
        mGameStatusHandler.sendMessage(msg)
    }

    private fun hideStatusText() {
        val msg = mGameStatusHandler.obtainMessage()
        val b = Bundle()
        b.putInt("visibility", View.INVISIBLE)
        msg.data = b
        mGameStatusHandler.sendMessage(msg)
    }

    fun setScoreText(playerScore: String?, opponentScore: String?) {
        val msg = mScoreHandler.obtainMessage()
        val b = Bundle()
        b.putString("player", playerScore)
        b.putString("opponent", opponentScore)
        msg.data = b
        mScoreHandler.sendMessage(msg)
    }


}
