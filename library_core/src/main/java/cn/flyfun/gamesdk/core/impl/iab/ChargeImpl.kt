package cn.flyfun.gamesdk.core.impl.iab

import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.internal.ImplCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.jarvis.Toast
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog
import org.json.JSONException
import org.json.JSONObject

/**
 * @author #Suyghur.
 * Created on 4/13/2021
 */
class ChargeImpl : InAppBilling() {
    //初始化IAB收银台客户端
    //连接谷歌商店
    private var payLoadingDialog: CircleProgressLoadingDialog? = null
    private var callback: ImplCallback? = null
    private var chargeInfo: GameChargeInfo? = null

    fun charge(context: Context, chargeInfo: GameChargeInfo, callback: ImplCallback) {
        if (!checkGoogleApiAvailability(context)) {
            Toast.toastInfo(context, "Your phone or Google account does not support In-app Billing")
            callback.onFailed("谷歌Iab支付服务不可用")
            return
        }
        this.callback = callback
        this.chargeInfo = chargeInfo
        //获取订单号
        dismissDialog()
        payLoadingDialog = DialogUtils.showCircleProgressLoadingDialog(context, ResUtils.getResString(context, "ffg_charge_loading_tips"))
        payLoadingDialog?.show()
        getOrderId(context)
    }

    /**
     * 获取订单号
     */
    private fun getOrderId(context: Context) {
        SdkRequest.getInstance().getOrderId(context, chargeInfo!!, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    try {
                        val jsonObject = JSONObject(resultInfo.data)
                        chargeInfo!!.orderId = jsonObject.getString("order_id")
                        Logger.d("order_id ---> ${chargeInfo?.orderId}")
                        //获取订单号成功，初始化IAB收银台客户端
                        Logger.d("获取订单号成功，初始化IAB收银台客户端")
                        initializeBillingClient(context)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        dismissDialog()
                        callback?.onFailed("获取订单异常")
                    }
                } else {
                    dismissDialog()
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(context, resultInfo.msg)
                    }
                    callback?.onFailed("获取订单失败")
                }
            }
        })
    }

    private fun dismissDialog() {
        payLoadingDialog?.apply {
            if (isShowing) {
                dismiss()
                payLoadingDialog = null
            }
        }
    }

    companion object {

        fun getInstance(): ChargeImpl {
            return ChargeImplHolder.INSTANCE
        }

        private object ChargeImplHolder {
            val INSTANCE = ChargeImpl()
        }
    }
}