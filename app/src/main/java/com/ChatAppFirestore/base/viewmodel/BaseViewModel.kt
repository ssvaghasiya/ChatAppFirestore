package com.ChatAppFirestore.base.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ChatAppFirestore.apputils.Constant
import com.ChatAppFirestore.apputils.Debug
import com.ChatAppFirestore.apputils.MenuItem
import com.ChatAppFirestore.base.utils.SideMenuAdapter
import com.ChatAppFirestore.databinding.CustomSideMenuBinding
import com.ChatAppFirestore.interfaces.CallbackListener
import com.ChatAppFirestore.ui.home.view.HomeActivity
import com.ChatAppFirestore.ui.login.view.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.ChatAppFirestore.R
import com.ChatAppFirestore.ui.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


open class BaseViewModel(application: Application) : AppViewModel(application) {
    lateinit var result: Drawer
    private lateinit var activity: Activity
    lateinit var customSideMenuBinding: CustomSideMenuBinding


    val db: FirebaseFirestore?
        get() {
            return (getApplication() as MyApplication).db
        }

    val auth: FirebaseAuth?
        get() {
            return (getApplication() as MyApplication).auth
        }

    val storageRef: StorageReference?
        get() {
            return (getApplication() as MyApplication).storageRef
        }

    val firebaseStorage: FirebaseStorage?
        get() {
            return (getApplication() as MyApplication).firebaseStorage
        }

    fun finishActivity(mContext: Context) {
        (mContext as Activity).finish()
    }

    fun initDrawer(mContext: Context) {
        initDrawer((mContext as Activity), mContext)
    }

    private lateinit var mAdapter: SideMenuAdapter
    fun initDrawer(activity: Activity, mContext: Context) {
        this.activity = activity
        customSideMenuBinding = DataBindingUtil.inflate(
            activity.layoutInflater,
            R.layout.custom_side_menu,
            null,
            false
        )
        mAdapter = SideMenuAdapter(activity)
//        val userInfo =
//            Gson().fromJson(Utils.getPref(mContext, Constant.LOGIN_INFO, ""), LoginData::class.java)
//        this.customSideMenuBinding.txtUserName.text = userInfo.data?.employee?.name

        val linearLayoutManager = LinearLayoutManager(activity)
        customSideMenuBinding.rvMenuList.layoutManager = linearLayoutManager
//        loadFromCache(customSideMenuBinding, mContext)
//        getUserProfile(mContext, customSideMenuBinding)

        customSideMenuBinding.navHeader.setOnClickListener {
            try {
//                val i = Intent(activity, ProfileActivity::class.java)
//                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                activity.startActivity(i)
//                finishActivity()
                result.drawerLayout.closeDrawers()
            } catch (e: Exception) {
            }
        }

//        customSideMenuBinding.rvMenuList.setDivider(R.drawable.recycler_view_divider)
        mAdapter.setEventListener(object : SideMenuAdapter.EventListener {

            override fun onMenuItemClick(position: Int, view: View) {
//                mAdapter.changeSelectedItemUi(position)
                onMenuItemClicked(mAdapter.getItem(position).menuId, view)
            }
        })

        customSideMenuBinding.rvMenuList.adapter = mAdapter
        val data = ArrayList<MenuItem>()
//        data.add(MenuItem("1", R.drawable.ic_launcher_background, getLabelText(R.string.home)))
//        data.add(MenuItem("2", R.drawable.ic_launcher_background, getLabelText(R.string.My_Tasks)))
////        data.add(MenuItem("3", R.drawable.ic_icon_ionic_md_notifications_gray, getLabelText(R.string.Notifications)))
//        data.add(MenuItem("4", R.drawable.ic_launcher_background, getLabelText(R.string.Clients)))
//        data.add(MenuItem("5", R.drawable.ic_launcher_background, getLabelText(R.string.profile)))
//        data.add(MenuItem("6", R.drawable.ic_launcher_background, getLabelText(R.string.log_out)))
//
//
        mAdapter.addAll(data)

        result = DrawerBuilder()
            .withActivity(activity)
            .withCloseOnClick(true)
            .withSelectedItemByPosition(-1)
            .withCustomView(customSideMenuBinding.root)
            .withDrawerWidthDp(300)
            .withDisplayBelowStatusBar(false)
            .withTranslucentStatusBar(true)
            .withOnDrawerListener(object : Drawer.OnDrawerListener {
                override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {

                }

                override fun onDrawerClosed(drawerView: View?) {

                }

                override fun onDrawerOpened(drawerView: View?) {
                    try {
//                        val userProfile = Utils.getPref(mContext, Constant.USER_PROFILE, "")
//                        val userProfileData = Gson().fromJson<UserProfileData>(
//                            JSONObject(userProfile!!).toString(),
//                            object : TypeToken<UserProfileData>() {}.type
//                        )
//                        customSideMenuBinding.txtFullName.text = userProfileData.result!!.fullname
//                        customSideMenuBinding.txtUserName.text = userProfileData.result!!.username
//                        Utils.loadImage(
//                            customSideMenuBinding.imgProfile,
//                            userProfileData.result!!.profilePicUrl!!,
//                            mContext,
//                            R.color.lgray
//                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })
            .build()

        result.drawerLayout.setScrimColor(Color.TRANSPARENT)
        result.drawerLayout.fitsSystemWindows = false
    }


    private fun onMenuItemClicked(menuId: String, view: View) {
        when (menuId) {
            "1" -> {
                if (activity is HomeActivity) {
                    hideMenu(true)
                } else {
//                        val intent = Intent(activity, HomeActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                        activity.startActivity(intent)
//                        hideMenu(false)
                }
            }

            "2" -> {
                if (activity is HomeActivity) {
                    hideMenu(true)
                } else {

//                    val intent = Intent(activity, HomeActivity::class.java)
//                    intent.flags =
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    activity.startActivity(intent)
//                    hideMenu(false)
                }
            }
            "3" -> {
                if (activity is HomeActivity) {
                    hideMenu(true)
                } else {

//                    val intent = Intent(activity, HomeActivity::class.java)
//                    intent.flags =
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    activity.startActivity(intent)
//                    hideMenu(false)
                }
            }

            "4" -> {
                if (activity is HomeActivity) {
                    hideMenu(true)
                } else {
//                    val intent = Intent(activity, HomeActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    activity.startActivity(intent)
//                    hideMenu(false)
                }
            }

            "5" -> {
                if (activity is HomeActivity) {
                    hideMenu(true)
                } else {

//                    val intent = Intent(activity, HomeActivity::class.java)
//                    intent.flags =
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    activity.startActivity(intent)
//                    hideMenu(false)
                }

            }
            "6" -> {

                logOut(view.context)
            }
        }

    }

    fun logOut(context: Context) {
        isInternetAvailable(context, object : CallbackListener {
            override fun onSuccess() {
                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(Intent(Constant.FINISH_ACTIVITY))
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
//                logoutDataModel.logOut(context).observeForever { categoryData ->
//                    onCallResult(categoryData, context)
//                }
            }

            override fun onCancel() {
            }

            override fun onRetry() {
                logOut(context)
            }
        })
    }

//    private fun onCallResult(logoutData: LogoutData, context: Context) = try {
//        when (logoutData.statusCode) {
//            Constant.RESPONSE_SUCCESS_CODE -> {
//                showToast(logoutData.message)
//                Utils.clearLoginCredentials(context)
//            }
//            Constant.RESPONSE_FAILURE_CODE -> {
//                showToast(logoutData.message)
//            }
//            else -> {
//                showToast(logoutData.message)
//            }
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }

    fun hideMenu(b: Boolean) {
        try {
            result.closeDrawer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onTopMenuClick() {
        toggleDrawer()
    }

    private fun toggleDrawer() {
        if (result.isDrawerOpen) {
            result.closeDrawer()
        } else {
            result.openDrawer()
        }
    }

    private fun setupFullHeight(
        v: View,
        dialogInterface: DialogInterface,
        linearLayout: LinearLayout,
        mContext: Context
    ) {

        val bottomSheetBehavior = BottomSheetBehavior.from((v.getParent()) as View)
        val layoutParams = linearLayout.layoutParams
        val windowHeight = getWindowHeight(mContext)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        linearLayout.layoutParams = layoutParams
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            R.id.design_bottom_sheet
        )
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(mContext: Context): Int { // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (mContext as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    interface PermissionListener {
        fun onGranted()

        fun onDenied()
    }

//    fun checkPermission(activity: Activity, permissionsListener: PermissionListener) {
//        Dexter.withActivity(activity)
//            .withPermissions(
//                android.Manifest.permission.INTERNET,
//                android.Manifest.permission.READ_SMS
//            ).withListener(object : MultiplePermissionsListener {
//
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    Debug.e("onPermissionsChecked", "" + report.areAllPermissionsGranted())
//                    Debug.e("onPermissionsChecked", "" + report.isAnyPermissionPermanentlyDenied)
//
//                    if (report.areAllPermissionsGranted()) {
//                        if (permissionsListener != null)
//                            permissionsListener.onGranted()
//                    } else {
//                        if (permissionsListener != null)
//                            permissionsListener.onDenied()
////                            showAlertDialog(permissionsListener)
//                    }
//
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest>,
//                    token: PermissionToken
//                ) {
//                    Debug.e("onPermissionRationale", "" + permissions.size)
//                    token.continuePermissionRequest()
//                }
//
//
//            }).check()
//    }

    fun checkPermissionStorageAndCamera(
        activity: Activity,
        permissionsListener: PermissionListener
    ) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    Debug.e("onPermissionsChecked", "" + report.areAllPermissionsGranted())
                    Debug.e("onPermissionsChecked", "" + report.isAnyPermissionPermanentlyDenied)

                    if (report.areAllPermissionsGranted()) {
                        permissionsListener.onGranted()
                    } else {
                        permissionsListener.onDenied()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Debug.e("onPermissionRationale", "" + permissions.size)
                    token.continuePermissionRequest()
                }


            }).check()
    }

    fun parseDate(time: String?, output: String?): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = output
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {


            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }

}
