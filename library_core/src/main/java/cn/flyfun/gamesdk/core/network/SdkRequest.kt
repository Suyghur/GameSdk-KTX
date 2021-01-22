package cn.flyfun.gamesdk.core.network

import android.content.Context
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.inter.IFileRequestCallback
import cn.flyfun.gamesdk.core.inter.IRequestCallback
import cn.flyfun.support.volley.VolleyError
import org.json.JSONException
import org.json.JSONObject
import java.io.File


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkRequest {

    companion object {
        fun getInstance(): SdkRequest {
            return RequestHolder.INSTANCE
        }
    }

    private object RequestHolder {
        val INSTANCE = SdkRequest()
    }

    fun initSdk(context: Context, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        VolleyRequest.post(context, Host.BASIC_URL_INIT_SDK, jsonObject, callback)
    }

    fun userLoginVerify(context: Context, loginParams: JSONObject, callback: IRequestCallback) {
        VolleyRequest.post(context, Host.BASIC_URL_USER_VERIFY, loginParams, callback)
    }

    fun userRegister(context: Context, userName: String, pwd: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_name", userName)
            jsonObject.put("pwd", pwd)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_USER_REGISTER, jsonObject, callback)
    }

    fun userBind(context: Context, bindParams: JSONObject, callback: IRequestCallback) {
        VolleyRequest.post(context, Host.BASIC_URL_USER_BIND, bindParams, callback)
    }

    fun getCaptcha(context: Context, userName: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_name", userName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_GET_CAPTCHA, jsonObject, callback)
    }

    fun forgetPassword(context: Context, userName: String, pwd: String, code: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_name", userName)
            jsonObject.put("pwd", pwd)
            jsonObject.put("sms_code", code)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_FORGET_PASSWORD, jsonObject, callback)
    }

    fun getOrderId(context: Context, chargeInfo: GameChargeInfo, callback: IRequestCallback) {
        val jsonObject = assembleChargeParams(chargeInfo)
        VolleyRequest.post(context, Host.BASIC_URL_GET_ORDER_ID, jsonObject, callback)
    }

    fun notifyOrder(context: Context, orderId: String, originalJson: String, signature: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("order_id", orderId)
            jsonObject.put("third_plat_content", originalJson)
            jsonObject.put("third_plat_sign", signature)
            //iOS交易编号，置空
            jsonObject.put("transaction_id", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_NOTIFY_ORDER, jsonObject, callback)
    }

    fun submitRoleData(context: Context, roleInfo: GameRoleInfo, callback: IRequestCallback) {
        val jsonObject = assembleRoleParams(roleInfo)
        VolleyRequest.post(context, Host.BASIC_URL_SUBMIT_ROLE, jsonObject, callback)
    }

    fun uploadLogFile(context: Context) {
        val zipFile = File(context.getExternalFilesDir("zap")?.absolutePath + "/tmp/log.zip")
        try {
//            uploadParams.put("log_report_id", "123")
//            uploadParams.put("file_name", "log.zip")
//            uploadParams.put("current_file_md5", "123")
//            uploadParams.put("user_id", "12345")
            val params = HashMap<String, Any>()
            params["log_report_id"] = "123"
            params["file_name"] = "log.zip"
            params["current_file_md5"] = "123"
            params["user_id"] = "12345"
            VolleyRequest.postFile(context, url = "http://192.168.20.125:9876/v1/upload/LogUpload", zipFile, params, object : IFileRequestCallback {
                override fun onResponse(result: String) {
                    Logger.d("result : $result")
                }

                override fun onErrorResponse(error: VolleyError) {
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun assembleChargeParams(chargeInfo: GameChargeInfo): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", chargeInfo.userId)
            jsonObject.put("server_code", chargeInfo.serverCode)
            jsonObject.put("server_name", chargeInfo.serverName)
            jsonObject.put("role_name", chargeInfo.roleName)
            jsonObject.put("role_id", chargeInfo.roleId)
            jsonObject.put("role_level", chargeInfo.roleLevel)
            jsonObject.put("price", chargeInfo.price.toString())
            jsonObject.put("product_id", chargeInfo.productId)
            jsonObject.put("product_name", chargeInfo.productName)
            jsonObject.put("product_desc", chargeInfo.productDesc)
            jsonObject.put("cp_order_id", chargeInfo.cpOrderId)
            jsonObject.put("cp_notify_url", chargeInfo.cpNotifyUrl)
            jsonObject.put("cp_callback_info", chargeInfo.cpCallbackInfo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun assembleRoleParams(roleInfo: GameRoleInfo): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", roleInfo.userId)
            jsonObject.put("server_code", roleInfo.serverCode)
            jsonObject.put("server_name", roleInfo.serverName)
            jsonObject.put("role_name", roleInfo.roleName)
            jsonObject.put("role_id", roleInfo.roleId)
            jsonObject.put("role_level", roleInfo.roleLevel)
            jsonObject.put("vip_level", roleInfo.vipLevel)
            jsonObject.put("balance", roleInfo.balance)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

}