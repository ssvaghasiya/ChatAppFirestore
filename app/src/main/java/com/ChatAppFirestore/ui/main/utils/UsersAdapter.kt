package com.ChatAppFirestore.ui.main.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ChatAppFirestore.R
import com.ChatAppFirestore.apputils.Debug
import com.ChatAppFirestore.apputils.FirestoreTable
import com.ChatAppFirestore.databinding.RowConversationBinding
import com.ChatAppFirestore.model.Chats
import com.ChatAppFirestore.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter() : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private lateinit var mEventListener: EventListener

    private var data = mutableListOf<User>()
    lateinit var context: Context


    constructor(context: Context) : this() {
        this.context = context
    }

    fun setEventListener(eventListener: EventListener) {
        mEventListener = eventListener
    }


    interface EventListener {
        fun onItemClick(pos: Int, item: User)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemBinding = DataBindingUtil.inflate<RowConversationBinding>(
            inflater,
            R.layout.row_conversation, parent, false
        )
        return MyViewHolder(itemBinding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(p: Int): User {
        return data[p]

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        try {
            val senderId = FirebaseAuth.getInstance().uid

            val senderRoom = senderId + item.uid
            getLastMsg(senderRoom, holder)

            holder.itemBinding.username.setText(item.name)

            Glide.with(context).load(item.profileImage)
                .placeholder(R.drawable.avatar)
                .into(holder.itemBinding.profile)

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        holder.itemBinding.root.setOnClickListener {
            mEventListener.onItemClick(position, item)
        }

    }

    fun addAll(mData: List<User>) {
        data.clear()
        data.addAll(mData)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun getLastMsg(senderRoom: String, holder: MyViewHolder) {
        var db = FirebaseFirestore.getInstance()
        val docRef =
            db!!.collection(FirestoreTable.CHATS).document(senderRoom)
        docRef.addSnapshotListener { value, error ->
            try {
                if (error != null) {
                    Debug.e("Listen failed.", error.message.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    val item = value.toObject(Chats::class.java)
                    val lastMsg = item?.lastMsg
                    val time = item?.lastMsgTime
                    val dateFormat = SimpleDateFormat("hh:mm a")
                    holder.itemBinding.msgTime.setText(dateFormat.format(Date(time!!)))
                    holder.itemBinding.lastMsg.setText(lastMsg)
                } else {
                    holder.itemBinding.lastMsg.setText("Tap to chat")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class MyViewHolder(internal var itemBinding: RowConversationBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}