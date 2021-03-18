package com.ChatAppFirestore.apputils

import com.google.gson.Gson

object GsonHelper{

    fun getGsonString(obj : Any) = Gson().toJson(obj)
}