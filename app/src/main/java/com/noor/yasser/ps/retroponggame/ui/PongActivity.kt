package com.noor.yasser.ps.retroponggame.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.noor.yasser.ps.retroponggame.R
import com.noor.yasser.ps.retroponggame.model.PongTable
import com.noor.yasser.ps.retroponggame.utils.GameThread

class PongActivity : AppCompatActivity() {
    private var mGameThread: GameThread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pong)


        val table: PongTable = findViewById<View>(R.id.pongTable) as PongTable
        table.setScoreOpponent(findViewById<View>(R.id.tvScoreOpponent) as TextView)
        table.setScorePlayer(findViewById<View>(R.id.tvScorePlayer) as TextView)
        table.setStatus(findViewById<View>(R.id.tvStatus) as TextView)


        mGameThread = table.game
    }

}