package com.ChatAppFirestore.ui.chat.view

import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ChatAppFirestore.R
import com.ChatAppFirestore.base.view.BaseActivity
import com.ChatAppFirestore.databinding.ActivityChatBinding
import com.ChatAppFirestore.ui.chat.viewmodel.ChatViewModel

class ChatActivity : BaseActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        viewModel = ViewModelProvider(activity).get(ChatViewModel::class.java)
        viewModel.setBinder(binding)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        viewModel.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        viewModel.onSupportNavigateUp()
        return super.onSupportNavigateUp()
    }
}