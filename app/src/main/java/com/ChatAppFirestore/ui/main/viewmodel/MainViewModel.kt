package com.ChatAppFirestore.ui.main.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ChatAppFirestore.R
import com.ChatAppFirestore.apputils.Debug
import com.ChatAppFirestore.apputils.FirestoreTable
import com.ChatAppFirestore.apputils.FirestoreTable.STATUSES
import com.ChatAppFirestore.apputils.Utils
import com.ChatAppFirestore.base.viewmodel.BaseViewModel
import com.ChatAppFirestore.databinding.ActivityMainBinding
import com.ChatAppFirestore.interfaces.TopBarClickListener
import com.ChatAppFirestore.model.Status
import com.ChatAppFirestore.model.User
import com.ChatAppFirestore.model.UserStatus
import com.ChatAppFirestore.ui.chat.view.ChatActivity
import com.ChatAppFirestore.ui.main.utils.TopStatusAdapter
import com.ChatAppFirestore.ui.main.utils.UsersAdapter
import com.ChatAppFirestore.ui.main.view.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.callback.StoryClickListeners
import omari.hamza.storyview.model.MyStory
import java.util.*

class MainViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var binder: ActivityMainBinding
    private lateinit var mContext: Context
    var database: FirebaseDatabase? = null
    var users: ArrayList<User>? = null
    lateinit var usersAdapter: UsersAdapter
    lateinit var statusAdapter: TopStatusAdapter
    var userStatuses: ArrayList<UserStatus>? = null
    var dialog: ProgressDialog? = null

    var user: User? = null

    fun setBinder(binder: ActivityMainBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {
        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        users = ArrayList()
        userStatuses = ArrayList<UserStatus>()

        getUser()
        usersAdapter = UsersAdapter(mContext)
        binder.recyclerView.adapter = usersAdapter
        usersAdapter.setEventListener(object : UsersAdapter.EventListener {
            override fun onItemClick(pos: Int, item: User) {
                val intent = Intent(mContext, ChatActivity::class.java)
                intent.putExtra("name", item.name)
                intent.putExtra("uid", item.uid)
                mContext.startActivity(intent)
            }
        })
        binder.recyclerView.showShimmerAdapter()

        val layoutManager = LinearLayoutManager(mContext)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binder.statusList.setLayoutManager(layoutManager)
        statusAdapter = TopStatusAdapter(mContext)
        binder.statusList.adapter = statusAdapter
        statusAdapter.setEventListener(object : TopStatusAdapter.EventListener {
            override fun onItemClick(pos: Int, item: UserStatus) {
                try {
                    val myStories: ArrayList<MyStory> = ArrayList<MyStory>()
                    for (status in item.statuses!!) {
                        myStories.add(MyStory(status.imageUrl))
                    }

                    StoryView.Builder((mContext as MainActivity).supportFragmentManager)
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(item.name) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(item.profileImage) // Default is Hidden
                        .setStoryClickListeners(object : StoryClickListeners {
                            override fun onDescriptionClickListener(position: Int) {
                                //your action
                            }

                            override fun onTitleIconClickListener(position: Int) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        binder.statusList.showShimmerAdapter()
        getUsersList()
        getStatus()

        binder.bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.status -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    (mContext as Activity).startActivityForResult(intent, 75)
                }
            }
            false
        })
    }

    fun getUser() {
        val docRef =
            db!!.collection(FirestoreTable.USERS).document(FirebaseAuth.getInstance().uid!!)
        docRef.addSnapshotListener { value, error ->
            try {
                if (error != null) {
                    Debug.e("Listen failed.", error.message.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    val item = value.toObject(User::class.java)
                    user = item
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUsersList() {
        val docRef = db!!.collection(FirestoreTable.USERS)
        docRef.addSnapshotListener { value, error ->
            try {
                if (error != null) {
                    Debug.e("Listen failed.", error.message.toString())
                    return@addSnapshotListener
                }
                if (value!!.isEmpty.not() || value != null) {
                    val item = value.toObjects(User::class.java)
                    users!!.clear()
                    for (i in item) {
                        if (!i?.uid.equals(FirebaseAuth.getInstance().uid)) {
                            users!!.add(i!!)
                        }
                    }
                    usersAdapter.addAll(users!!)
                    binder.recyclerView.hideShimmerAdapter()
                    usersAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getStatus() {
        val docRef = db!!.collection(FirestoreTable.STORIES)
        docRef.addSnapshotListener { value, error ->
            try {
                if (error != null) {
                    Debug.e("Listen failed.", error.message.toString())
                    return@addSnapshotListener
                }
                if (value!!.isEmpty.not() || value != null) {
                    userStatuses!!.clear()
                    val itemUserStatus = value.toObjects(UserStatus::class.java)
                    for (i in itemUserStatus) {
                        val status = UserStatus()
                        status.name = i.name
                        status.profileImage = i.profileImage
                        status.lastUpdated = i.lastUpdated
                        val docRef = db!!.collection(FirestoreTable.STORIES).document(i.id!!).collection(STATUSES)
                        docRef.addSnapshotListener { value, error ->
                            try {
                                if (error != null) {
                                    Debug.e("Listen failed.", error.message.toString())
                                    return@addSnapshotListener
                                }
                                if (value!!.isEmpty.not() || value != null) {
                                    val item = value.toObjects(Status::class.java)
                                    status.statuses.addAll(item)
                                    userStatuses!!.add(status)
                                    if(itemUserStatus.size == userStatuses?.size) {
                                        statusAdapter.addAll(userStatuses!!)
                                        binder.statusList.hideShimmerAdapter()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    lateinit var uploadTask: UploadTask
    var selectedImage: Uri? = null

    fun uploadPhoto(file: Uri) {
        dialog!!.show()
        val fileName = "PROFILES" + "_" + System.currentTimeMillis();
        val profileRef =
            storageRef!!.child(FirestoreTable.PROFILES).child(fileName)
        uploadTask = profileRef.putFile(file)
        uploadTask.addOnFailureListener { p0 ->
            dialog!!.dismiss()
            p0.printStackTrace()
        }.addOnSuccessListener {
            var filePath: String? = null
            profileRef.downloadUrl.addOnSuccessListener { uri ->
                dialog!!.dismiss()
                filePath = uri.toString()
                Debug.e("Image Uploaded Successfully", filePath)
                val date = Date()
                val userStatus = UserStatus()
                userStatus.name = (user?.name)
                userStatus.profileImage = (user?.profileImage)
                userStatus.lastUpdated = (date.time)
                val imageUrl = filePath
                val status = Status(imageUrl, userStatus.lastUpdated)
                addStatus(userStatus, status)
            }
        }.addOnFailureListener {
            dialog!!.dismiss()
            it.printStackTrace()
        }
    }

    fun addStatus(userStatus: UserStatus, status: Status) {
        val data = hashMapOf(
            "id" to FirebaseAuth.getInstance().uid!!,
            "name" to userStatus.name,
            "profileImage" to userStatus.profileImage,
            "lastUpdated" to userStatus.lastUpdated,
        )
        dialog!!.show()
        db!!.collection(FirestoreTable.STORIES)
            .document(FirebaseAuth.getInstance().uid!!)
            .set(data)
            .addOnSuccessListener {
                dialog!!.dismiss()
                dialog!!.show()
                db!!.collection(FirestoreTable.STORIES)
                    .document(FirebaseAuth.getInstance().uid!!)
                    .collection(FirestoreTable.STATUSES)
                    .add(status)
                    .addOnSuccessListener {
                        dialog!!.dismiss()
                    }
                    .addOnFailureListener { exception ->
                        dialog!!.dismiss()
                        exception.printStackTrace()
                    }
            }
            .addOnFailureListener { exception ->
                dialog!!.dismiss()
                exception.printStackTrace()
            }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            if (data.data != null) {
                dialog!!.show()
                selectedImage = data.data
                uploadPhoto(selectedImage!!)
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

        fun onSignOut(view: View) {

        }
    }

    fun onOptionsItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.search -> Toast.makeText(mContext, "Search clicked.", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(mContext, "Settings Clicked.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun onCreateOptionsMenu(menu: Menu?) {
        (mContext as Activity).menuInflater.inflate(R.menu.topmenu, menu)
    }
}



