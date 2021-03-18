package com.ChatAppFirestore.base.datamodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ChatAppFirestore.datasource.UserRepository
import com.ChatAppFirestore.network.APIClient
import com.ChatAppFirestore.network.APIinterface

class UserDataModel {
    fun getArea(context: Context): MutableLiveData<AllArea> {
        val apInterface: APIinterface =
            APIClient.newRequestRetrofit(context).create(APIinterface::class.java)
        val userRepository = UserRepository(apInterface)
        return userRepository.getArea("")
    }
}