package cn.flyfun.gamesdk.core.impl.iab

import android.app.Activity
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.GameRewardInfo
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.impl.SdkBridgeImpl
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.support.jarvis.Toast
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase

/**
 * @author #Suyghur.
 * Created on 4/13/2021
 */
class PreRewardImpl : InAppBilling() {

    private var rewardInfo: GameRewardInfo? = null

    fun checkPreReward(activity: Activity, rewardInfo: GameRewardInfo) {
        if (!checkGoogleApiAvailability(activity)) {
            Toast.toastInfo(activity, "Your phone or Google account does not support In-app Billing")
            return
        }
        this.rewardInfo = rewardInfo
        //初始化IAB收银台客户端
        initializeBillingClient(activity)
        //连接谷歌商店
        connectGooglePlay(activity, true)
    }

    override fun chargePurchasesUpdated(activity: Activity, purchase: Purchase) {
    }

    override fun preRewardPurchasesUpdated(activity: Activity, purchase: Purchase) {
        notifyReward2Backend(activity, purchase)
    }

    override fun queryRewardInfo(activity: Activity) {
        billingClient?.apply {
            val list = queryPurchases(BillingClient.SkuType.INAPP).purchasesList
            if (list == null || list.size <= 0) {
                Logger.d("没有预注册奖励")
                disConnection()
                return@apply
            }
            Logger.d("查询预注册奖励")
            for (purchase in list) {
                Logger.d("reward info : $purchase")
                if (SdkBridgeImpl.initBean.initReward.rewardId == purchase.sku) {
                    //通知发货
                    rewardInfo?.purchaseToken = purchase.purchaseToken
                    notifyReward2Backend(activity, purchase)
                } else {
                    if (list.size == 1) {
                        disConnection()
                        return@apply
                    }
                }
            }
        }
    }

    override fun queryChargeInfo(activity: Activity) {
    }

    override fun purchasesUpdatedFailed() {
        Logger.e("purchasesUpdatedFailed")
    }

    override fun connectGooglePlayFailed() {
        Logger.e("connectGooglePlayFailed")
    }

    private fun notifyReward2Backend(activity: Activity, purchase: Purchase) {
        Logger.d("notifyReward2Backend")
        SdkRequest.getInstance().notifyReward(activity, rewardInfo!!, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                consumeAsync(purchase.purchaseToken)
            }
        })
    }

    private fun consumeAsync(purchaseToken: String) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        billingClient?.apply {
            consumeAsync(consumeParams) { billingResult, _ ->
                logBillingResult("onConsumeResponse", billingResult)
                disConnection()
            }
        }
    }


    companion object {

        fun getInstance(): PreRewardImpl {
            return PreRewardImplHolder.INSTANCE
        }

        private object PreRewardImplHolder {
            val INSTANCE = PreRewardImpl()
        }
    }

}