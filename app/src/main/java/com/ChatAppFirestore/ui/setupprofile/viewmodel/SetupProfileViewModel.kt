package com.ChatAppFirestore.ui.setupprofile.viewmodel

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ChatAppFirestore.R
import com.ChatAppFirestore.apputils.Debug
import com.ChatAppFirestore.apputils.FirestoreTable
import com.ChatAppFirestore.apputils.Utils
import com.ChatAppFirestore.base.viewmodel.BaseViewModel
import com.ChatAppFirestore.databinding.ActivitySetupProfileBinding
import com.ChatAppFirestore.interfaces.TopBarClickListener
import com.ChatAppFirestore.model.User
import com.ChatAppFirestore.ui.main.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class SetupProfileViewModel(application: Application) : BaseViewModel(application){

    private lateinit var binder: ActivitySetupProfileBinding
    private lateinit var mContext: Context
    var fauth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null

    fun setBinder(binder: ActivitySetupProfileBinding) {
        this.binder = binder
        this.mContext = binder.root.context
        this.binder.viewModel = this
        this.binder.viewClickHandler = ViewClickHandler()
        init()

    }

    private fun init() {

        dialog = ProgressDialog(mContext)
        dialog!!.setMessage("Updating profile...")
        dialog!!.setCancelable(false)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        fauth = FirebaseAuth.getInstance()

        (mContext as AppCompatActivity).getSupportActionBar()?.hide()


    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            if (data.data != null) {
                binder.imageView.setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }

    lateinit var uploadTask: UploadTask

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
                val uid = fauth!!.uid
                val phone = fauth!!.currentUser.phoneNumber
                val name: String = binder.nameBox.getText().toString()
                val user = User(uid, name, phone, filePath!!)
                addUser(user)
            }
        }.addOnFailureListener {
            dialog!!.dismiss()
            it.printStackTrace()
        }
    }

    fun addUser(user: User) {
        dialog!!.show()
        db!!.collection(FirestoreTable.USERS)
            .document(user.uid!!)
            .set(user)
            .addOnSuccessListener {
                dialog!!.dismiss()
                val intent = Intent(
                    mContext,
                    MainActivity::class.java
                )
                mContext.startActivity(intent)
                (mContext as Activity).finish()
            }
            .addOnFailureListener { exception ->
                dialog!!.dismiss()
                exception.printStackTrace()
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
            }         }

        override fun onBackClicked(view: View?) {
            (mContext as Activity).finish()
        }
    }


    inner class ViewClickHandler {

        fun onProfileImage(view: View) {
            try {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                (mContext as Activity).startActivityForResult(intent, 45)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun onContinue(view: View) {
            try {
                val name: String = binder.nameBox.getText().toString()

                if (name.isEmpty()) {
                    binder.nameBox.setError("Please type a name")
                    return
                }

                dialog!!.show()
                if (selectedImage != null) {
                    uploadPhoto(selectedImage!!)
                } else {
                    val uid = fauth!!.uid
                    val phone = fauth!!.currentUser.phoneNumber
                    val user = User(uid, name, phone, "No Image")
                    addUser(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}



