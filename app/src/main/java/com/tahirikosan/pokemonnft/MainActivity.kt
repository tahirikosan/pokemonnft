package com.tahirikosan.pokemonnft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.tahirikosan.pokemonnft.ui.fight.FightFragment
import com.tahirikosan.pokemonnft.utils.KeyDownListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var keyDownListener: KeyDownListener?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun setKeyDownListener(listener: KeyDownListener) {
        this.keyDownListener = listener
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        keyDownListener?.onKeyDown(keyCode)
        return super.onKeyDown(keyCode, event)
    }
}