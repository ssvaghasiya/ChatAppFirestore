package com.ChatAppFirestore.ui.chat.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ChatAppFirestore.R
import com.ChatAppFirestore.apputils.Debug
import com.ChatAppFirestore.apputils.FirestoreTable
import com.ChatAppFirestore.apputils.Utils
import com.ChatAppFirestore.base.viewmodel.BaseViewModel
import com.ChatAppFirestore.databinding.ActivityChatBinding
import com.ChatAppFirestore.interfaces.TopBarClickListener
import com.ChatAppFirestore.model.Message
import com.ChatAppFirestore.ui.chat.utils.MessagesAdapter
import com.ChatAppFirestore.ui.chat.view.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class ChatViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityChatBinding
    private lateinit var mContext: Context
    lateinit var messagesAdapter: MessagesAdapter
    var messages: ArrayList<Message>? = null

    var senderRoom: String? = null
    var receiverRoom: String? = null
    var senderUid: String? = null

    fun setBinder(binder: ActivityChatBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        messages = ArrayList()

        val name: String = (mContext as Activity).intent.getStringExtra("name")!!
        val receiverUid: String = (mContext as Activity).intent.getStringExtra("uid")!!
        senderUid = FirebaseAuth.getInstance().uid

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        var layoutManager = LinearLayoutManager(mContext)
        binder.recyclerView.layoutManager = layoutManager
        messagesAdapter = MessagesAdapter(mContext, senderRoom!!, receiverRoom!!)
        binder.recyclerView.adapter = messagesAdapter
        messagesAdapter.setEventListener(object : MessagesAdapter.EventListener {

            override fun onItemClick(pos: Int, item: Message) {
            }
        })
        getChats(senderRoom!!)
        (mContext as ChatActivity).supportActionBar?.title = name
        (mContext as ChatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    fun getChats(senderRoom: String) {
        val docRef = db!!.collection(FirestoreTable.CHATS).document(senderRoom)
            .collection(FirestoreTable.MESSAGES).orderBy("timestamp")
        docRef.addSnapshotListener { value, error ->
            try {
                if (error != null) {
                    Debug.e("Listen failed.", error.message.toString())
                    return@addSnapshotListener
                }
                if (value!!.isEmpty.not() || value != null) {
                    val item = value.toObjects(Message::class.java)
                    messages!!.clear()
                    messages!!.addAll(item)
                    messagesAdapter.addAll(messages!!)
                    binder.recyclerView.scrollToPosition(messages?.size!! - 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    inner class SlideMenuClickListener : TopBarClickListener {
        override fun onTopBarClickListener(view: View?, value: String?) {
            Utils.hideKeyBoard(getContext(), view!!)
            if (value.equals(getLabelText(R.string.menu))) {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onBackClicked(view: View?) {
            (mContext as Activity).finish()
        }
    }


    inner class ViewClickHandler {

        fun onSendMessage(view: View) {
            val messageTxt: String = binder.messageBox.getText().toString()
            val randomKey = db!!.collection(FirestoreTable.CHATS).document()
                .collection(FirestoreTable.MESSAGES).document().id
            val date = Date()
            val message = Message(messageTxt, senderUid!!, date.time, randomKey)
            binder.messageBox.setText("")

            val lastMsgObj = HashMap<String, Any>()
            lastMsgObj["lastMsg"] = message.message!!
            lastMsgObj["lastMsgTime"] = date.time
            addToSenderRoom(senderRoom!!, lastMsgObj)
            addToReceiverRoom(receiverRoom!!, lastMsgObj)
            addMessage(senderRoom!!, receiverRoom!!, randomKey, message)
        }
    }

    fun addToSenderRoom(senderRoom: String, lastMsgObj: HashMap<String, Any>) {
        db!!.collection(FirestoreTable.CHATS).document(senderRoom)
            .set(lastMsgObj)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    fun addToReceiverRoom(receiverRoom: String, lastMsgObj: HashMap<String, Any>) {
        db!!.collection(FirestoreTable.CHATS).document(receiverRoom)
            .set(lastMsgObj)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    fun addMessage(senderRoom: String, receiverRoom: String, randomKey: String, message: Message) {
        db!!.collection(FirestoreTable.CHATS).document(senderRoom).collection(FirestoreTable.MESSAGES).document(
            randomKey
        )
            .set(message)
            .addOnSuccessListener {
                db!!.collection(FirestoreTable.CHATS).document(receiverRoom).collection(
                    FirestoreTable.MESSAGES
                ).document(randomKey)
                    .set(message)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    fun onCreateOptionsMenu(menu: Menu?) {
        (mContext as Activity).menuInflater.inflate(R.menu.chat_menu, menu)
    }

    fun onSupportNavigateUp() {
        (mContext as Activity).finish()
    }

}



