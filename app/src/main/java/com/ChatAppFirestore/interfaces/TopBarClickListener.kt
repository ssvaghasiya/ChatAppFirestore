package com.ChatAppFirestore.interfaces

import android.view.View

interface TopBarClickListener {

    fun onTopBarClickListener(view: View?, value: String?)
    fun onBackClicked(view: View?)
}