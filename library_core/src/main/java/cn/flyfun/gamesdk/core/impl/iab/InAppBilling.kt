package cn.flyfun.gamesdk.core.impl.iab

import android.app.Activity
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.impl.SdkBridgeImpl
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * @author #Suyghur.
 * Created on 4/13/2021
 */
abstract class InAppBilling  {

    protected var billingClient: BillingClient? = null

    protected fun checkGoogleApiAvailability(activity: Activity): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS
    }

    protected fun initializeBillingClient(activity: Activity) {
        billingClient = BillingClient.newBuilder(activity).setListener { billingResult, list ->
            //谷歌支付结果在这里回调
            logBillingResult("onPurchasesUpdated", billingResult)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (list != null && list.size > 0) {
                    for (purchase in list) {
                        if (purchase.sku == SdkBridgeImpl.initBean.initReward.rewardId) {
                            //预注册
                            preRewardPurchasesUpdated(activity, purchase)
                        } else {
                            //支付
                            chargePurchasesUpdated(activity, purchase)
                        }
                    }
                } else {
                    purchasesUpdatedFailed()
                }
            } else {
                purchasesUpdatedFailed()
            }
        }.enablePendingPurchases().build()
    }

    /**
     * charge
     * Purchase{"orderId":"GPA.3325-4558-9050-03633","packageName":"com.flyfun.demo","productId":"com.flyfun.ylj.60","purchaseTime":1618367632652,"purchaseState":4,"purchaseToken":"mpfdmeeoemplddgfknnkgnlf.AO-J1Owpfua8OfxpU_sjOBTY_ZgRoN3Km-mNJdmie9Qpd1w-DDQjtkUfcWOGhScHHeJ9ogWqkP_01w5WHp9Pfnpvgx1_nffDYA","acknowledged":false}
     */
    protected abstract fun chargePurchasesUpdated(activity: Activity, purchase: Purchase)

    /**
     * pre reward
     * Purchase{"packageName":"com.flyfun.demo","productId":"com.flyfun.demo70","purchaseTime":1618367980837,"purchaseState":0,"purchaseToken":"cdphbkooagehckdcmkhmbhmd.AO-J1OzHTtCRDhd8k1Qfp3FU0GgYQUSaCx6I6W6Oi_P2tiS3LWKIdb6jTkc-tTqfcCagPxw2nPqujl9s10KuCunTl3OLSiIN9A"}
     */
    protected abstract fun preRewardPurchasesUpdated(activity: Activity, purchase: Purchase)

    protected abstract fun queryRewardInfo(activity: Activity)

    protected abstract fun queryChargeInfo(activity: Activity)

    protected abstract fun purchasesUpdatedFailed()

    protected abstract fun connectGooglePlayFailed()

    protected fun connectGooglePlay(activity: Activity, isPreReward: Boolean = false) {
        billingClient?.apply {
            if (!isReady) {
                Logger.d("start connection Google Play ...")
                startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        logBillingResult("onBillingSetupFinished", billingResult)
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (isPreReward) {
                                queryRewardInfo(activity)
                            } else {
                                queryChargeInfo(activity)
                            }
                        } else {
                            connectGooglePlayFailed()
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Logger.e("onBillingServiceDisconnected")
                        connectGooglePlay(activity, isPreReward)
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
        Logger.d("$callbackFuncName , code : $code , msg : $msg")
    }
}