package cn.flyfun.gamesdk.core.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ClickType
import cn.flyfun.gamesdk.core.entity.LoginType
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.utils.ScreenShotUtils
import cn.flyfun.support.jarvis.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.share.Sharer
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.MessageDialog
import com.facebook.share.widget.ShareDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * @author #Suyghur.
 * Created on 3/25/21
 */
class ShareImpl {

    private var fbCallback: CallbackManager? = null
    private var shareDialog: ShareDialog? = null

    init {
        this.fbCallback = CallbackManager.Factory.create()
    }

    fun invokeShare2Fb(activity: Activity) {
        Logger.d("invokeShare2Fb")
        if (SdkBackLoginInfo.instance.loginType != ClickType.ACTION_FACEBOOK_MODE) {
            startLogin(activity)
        } else {
            startShare(activity)
        }
    }

    private fun startShare(activity: Activity) {
        val bitmap = ScreenShotUtils.joinQRCode(activity, ScreenShotUtils.getScreenShot(activity))
        val photo = SharePhoto.Builder()
                .setBitmap(bitmap)
                .build()
        val content = SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(ShareHashtag.Builder().setHashtag("#测试话题").build())
                .build()

        shareDialog = ShareDialog(activity)
        shareDialog?.apply {
            registerCallback(fbCallback, object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result?) {
                    Logger.d("facebook share onSuccess")
                    Toast.toastInfo(activity, "Facebook sharing success")
                }

                override fun onCancel() {
                    Logger.e("facebook share onCancel")
                    Toast.toastInfo(activity, "You have canceled sharing to Facebook")
                }

                override fun onError(error: FacebookException?) {
                    Logger.e("facebook share onError ${error?.message}")
                    Toast.toastInfo(activity, "Facebook sharing has some error")
                }
            })
            show(content)
        }
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    private fun startLogin(activity: Activity) {
        if (!FacebookSdk.isInitialized()) {
            return
        }
        //先注销账号，防止同一台设备有多个应用登录后直接回调onCancel
        LoginManager.getInstance().logOut()
        LoginManager.getInstance().registerCallback(fbCallback, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.apply {

                    Logger.d("Facebook授权成功")
                    startShare(activity)
                }
            }

            override fun onCancel() {
                Logger.d("Facebook取消授权")
            }

            override fun onError(error: FacebookException?) {
                error?.apply {
                    Logger.e("Facebook授权失败 : " + this.message)
                }
            }
        })
        LoginManager.getInstance().logInWithReadPermissions(activity, Collections.singletonList("public_profile"))
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        fbCallback?.apply {
            onActivityResult(requestCode, resultCode, data)
        }
    }


    companion object {

        fun getInstance(): ShareImpl {
            return ShareImplHolder.INSTANCE
        }

        private object ShareImplHolder {
            val INSTANCE = ShareImpl()
        }
    }
}