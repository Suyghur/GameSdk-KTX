package cn.flyfun.gamesdk.core.impl.iab

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.impl.SdkBridgeImpl
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.internal.ImplCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.gamesdk.core.utils.SPUtils
import cn.flyfun.support.JsonUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.jarvis.Toast
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog
import com.android.billingclient.api.*
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

    fun charge(activity: Activity, chargeInfo: GameChargeInfo, callback: ImplCallback) {
        if (!checkGoogleApiAvailability(activity)) {
            Toast.toastInfo(activity, "Your phone or Google account does not support In-app Billing")
            callback.onFailed("谷歌Iab支付服务不可用")
            return
        }
        this.callback = callback
        this.chargeInfo = chargeInfo
        //获取订单号
        dismissDialog()
        payLoadingDialog = DialogUtils.showCircleProgressLoadingDialog(activity, ResUtils.getResString(activity, "ffg_charge_loading_tips"))
        payLoadingDialog?.show()
        getOrderId(activity)
    }

    /**
     * 获取订单号
     */
    private fun getOrderId(activity: Activity) {
        SdkRequest.getInstance().getOrderId(activity, chargeInfo!!, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    try {
                        val jsonObject = JSONObject(resultInfo.data)
                        chargeInfo!!.orderId = jsonObject.getString("order_id")
                        Logger.d("order_id ---> ${chargeInfo?.orderId}")
                        //获取订单号成功，初始化IAB收银台客户端
                        Logger.d("获取订单号成功，初始化IAB收银台客户端")
                        initializeBillingClient(activity)
                        connectGooglePlay(activity)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        dismissDialog()
                        callback?.onFailed("获取订单异常")
                    }
                } else {
                    dismissDialog()
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(activity, resultInfo.msg)
                    }
                    callback?.onFailed("获取订单失败")
                }
            }
        })
    }

    override fun connectGooglePlayFailed() {
        callback?.onFailed("连接谷歌商店失败")
    }

    override fun chargePurchasesUpdated(activity: Activity, purchase: Purchase) {
        notifyOrder2Backend(activity, chargeInfo!!.orderId + "", purchase.originalJson, purchase.signature, false)
    }

    override fun preRewardPurchasesUpdated(activity: Activity, purchase: Purchase) {
    }

    override fun purchasesUpdatedFailed() {
        callback?.onFailed("支付失败")
    }

    override fun queryRewardInfo(activity: Activity) {
    }

    /**
     * 查询未消耗订单
     */
    override fun queryChargeInfo(activity: Activity) {
        billingClient?.apply {
            val list = queryPurchases(BillingClient.SkuType.INAPP).purchasesList
            if (list == null || list.size == 0) {
                //发起正常支付流程
                querySkuDetails(activity)
            } else {
                //
                if (list.size > 0) {
                    Logger.e("存在未消耗的订单，发起补单流程 , size :${list.size}")
                    for (purchase in list) {
                        Logger.d("$purchase")
                        if (purchase.sku == SdkBridgeImpl.initBean.initReward.rewardId) {
                            Logger.e("存在阻塞的预注册奖励，停止支付流程")
                            if (list.size == 1) {
                                Toast.toastInfo(activity, "In-app Billing has some error , please restart app and try again")
                                dismissDialog()
                                disConnection()
                                callback?.onFailed("存在阻塞的预注册奖励，停止支付流程")
                            }
                        } else {
                            consumeCacheOrder(activity, purchase)
                        }
                    }
                } else {
                    //发起正常支付流程
                    querySkuDetails(activity)
                }
            }
        }
    }

    /**
     * 消耗缓存的订单
     */
    private fun consumeCacheOrder(activity: Activity, purchase: Purchase) {
        //消耗完了再发起支付
        val cache = SPUtils.getCacheOrder(activity)
        var cacheOrderId = ""
        val cacheOriginalJson: String
        if (TextUtils.isEmpty(cache)) {
            Logger.e("本地缓存订单信息为空")
            //缓存信息为空时，谷歌阻塞的订单信息中没有我方订单号，可以不赋值
            cacheOriginalJson = purchase.originalJson
        } else {
            val cacheInfo = JSONObject(cache)
            cacheOrderId = if (JsonUtils.hasJsonKey(cacheInfo, "order_id")) {
                cacheInfo.getString("order_id")
            } else {
                ""
            }
            cacheOriginalJson = if (JsonUtils.hasJsonKey(cacheInfo, "original_json")) {
                cacheInfo.getString("original_json")
            } else {
                purchase.originalJson
            }
        }
        notifyOrder2Backend(activity, cacheOrderId, cacheOriginalJson, purchase.signature, true)
    }


    /**
     * 查询商品信息
     */
    private fun querySkuDetails(activity: Activity) {
        val skus = ArrayList<String>()
//        skus.add("${chargeInfo!!.productId}")
        skus.add("${chargeInfo!!.productId}")
        skus.add("com.ceshiyingyong.1")
        val params = SkuDetailsParams.newBuilder().setType(BillingClient.SkuType.INAPP).setSkusList(skus).build()
        billingClient?.apply {
            querySkuDetailsAsync(params) { billingResult, list ->
                logBillingResult("onSkuDetailsResponse", billingResult)
                dismissDialog()
                if (list == null) {
                    callback?.onFailed("查询商品信息失败")
                } else {
                    if (list.size == 1) {
                        val skuDetails = list[0]
                        Logger.d("product id : ${skuDetails.sku}")
                        launchBillingFlow(activity, skuDetails)
                    } else {
                        callback?.onFailed("查询商品信息异常")
                    }
                }
            }
        }
    }

    /**
     * 启动谷歌收银台
     */
    private fun launchBillingFlow(activity: Activity, skuDetails: SkuDetails) {
        billingClient?.apply {
            val billingResult = launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build())
            logBillingResult("launchBillingFlow", billingResult)
        }
    }

    /**
     * 通知服务端发货
     */
    private fun notifyOrder2Backend(activity: Activity, orderId: String, originalJson: String, signature: String, isCache: Boolean) {
        SdkRequest.getInstance().notifyOrder(activity, orderId, originalJson, signature, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                Logger.d("result : $resultInfo")
                try {
                    saveOrder2Local(activity, orderId, originalJson)
                    val jsonObject = JSONObject(originalJson)
                    if (JsonUtils.hasJsonKey(jsonObject, "purchaseToken")) {
                        consumeAsync(activity, jsonObject.getString("purchaseToken"), isCache)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    /**
     * 消耗订单
     */
    private fun consumeAsync(activity: Activity, purchaseToken: String, isCache: Boolean) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        billingClient?.apply {
            consumeAsync(consumeParams) { billingResult, _ ->
                logBillingResult("consumeAsync", billingResult)
                dismissDialog()
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    //消耗成功
                    if (isCache) {
                        querySkuDetails(activity)
                    } else {
                        callback?.onSuccess("支付成功")
                        disConnection()
                    }
                    saveOrder2Local(activity, "", "")
                } else {
                    Toast.toastInfo(activity, ResUtils.getResString(activity, "ffg_charge_tv_error"))
                    callback?.onFailed("消耗订单异常")
                    disConnection()
                }
            }
        }
    }

    private fun saveOrder2Local(context: Context, orderId: String, originalJson: String) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("order_id", orderId)
            jsonObject.put("original_json", originalJson)
            Logger.d("saveOrder2Local : $jsonObject")
            SPUtils.saveCacheOrder(context, jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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