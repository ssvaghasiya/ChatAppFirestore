package com.ChatAppFirestore.ui.home.view

import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ChatAppFirestore.R
import com.ChatAppFirestore.base.view.BaseActivity
import com.ChatAppFirestore.databinding.ActivityHomeBinding
import com.ChatAppFirestore.ui.home.viewmodel.HomeViewModel



class HomeActivity : BaseActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var viewModel: HomeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, R.color.white)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(activity).get(HomeViewModel::class.java)
        viewModel.setBinder(binding)
    }

}
