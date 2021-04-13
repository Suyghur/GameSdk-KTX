package cn.flyfun.gamesdk.core.impl.iab

import android.app.Activity
import android.content.Context
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.support.jarvis.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * @author #Suyghur.
 * Created on 4/13/2021
 */
open class InAppBilling {

    protected var billingClient: BillingClient? = null

    protected fun checkGoogleApiAvailability(context: Context): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    protected fun initializeBillingClient(context: Context) {

        billingClient = BillingClient.newBuilder(context).setListener { billingResult, list ->
            //谷歌支付结果在这里回调
            logBillingResult("onPurchasesUpdated", billingResult)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (list != null && list.size > 0) {
                    for (purchase in list) {
//                        if (isPreReward && rewardInfo != null) {
//                            if (purchase.sku == rewardInfo!!.rewardId)
//                            //预注册
//                                notifyReward2Backend(activity)
//                        } else {
//                            //支付
//                            notifyOrder2Backend(activity, chargeInfo!!.orderId + "", list[0].originalJson, list[0].signature, false)
//                        }
                    }
                } else {
                    //callback?.onFailed("支付失败")
                    disConnection()
                }
            } else {
                //  callback?.onFailed("支付异常")
                disConnection()
            }
        }.enablePendingPurchases().build()
        connectGooglePlay(context)
    }

    protected fun connectGooglePlay(context: Context) {
        billingClient?.apply {
            if (!isReady) {
                Logger.d("start connection Google Play ...")
                startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        logBillingResult("onBillingSetupFinished", billingResult)
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            if (isPreReward) {
//                                queryReward(activity)
//                            } else {
//                                queryPurchases(activity)
//                            }
                        } else {
                            //callback?.onFailed("连接Google Play服务异常")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Logger.e("onBillingServiceDisconnected")
                        connectGooglePlay(context)
                    }
                })
            }
        }
    }

    protected fun disConnection() {
        billingClient?.apply {
            if (isReady) {
                Logger.d("断开谷歌收银台连接，以清空被消耗或者失败的缓存订单")
                endConnection()
            }
        }
    }

    protected fun logBillingResult(callbackFuncName: String, billingResult: BillingResult) {
        val code = billingResult.responseCode
        val msg = billingResult.debugMessage
        Logger.d(callbackFuncName + "code : $code , msg : $msg")
    }

    interface IIabCallback {
        fun onResule(code: Int, result: String)
    }

    enum class IabType {
        CHARGE,
        PREREWARD
    }

    companion object {

        fun getInstance(): InAppBilling {
            return InAppBillingHolder.INSTANCE
        }

        private object InAppBillingHolder {
            val INSTANCE = InAppBilling()
        }
    }
}