package cn.flyfun.gamesdk.core.network

import android.content.Context
import cn.flyfun.support.HostModelUtils

/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
object Host {

    private const val DEFAULT_ONLINE_GAME_HOST = "https://game.feiyougames.com"
    private const val DEFAULT_ONLINE_LOGIN_HOST = "https://login.feiyougames.com"
    private const val DEFAULT_ONLINE_PAY_HOST = "https://pay.feiyougames.com"
    private const val DEFAULT_ONLINE_ADV_HOST = "https://adv.feiyougames.com"
    private const val DEFAULT_TEST_GAME_HOST = "http://testgame.feiyougames.com"
    private const val DEFAULT_TEST_LOGIN_HOST = "http://testlogin.feiyougames.com"
    private const val DEFAULT_TEST_PAY_HOST = "http://testpay.feiyougames.com"
    private const val DEFAULT_TEST_ADV_HOST = "http://testadv.feiyougames.com"

    var BASIC_URL_INIT_SDK = "/game-api/v1/game/api/find-info"
    var BASIC_URL_USER_VERIFY = "/login-api/v1/user/login"
    var BASIC_URL_USER_REGISTER = "/login-api/v1/user/registered"
    var BASIC_URL_USER_BIND = "/login-api/v1/user/bind"
    var BASIC_URL_GET_CAPTCHA = "/login-api/v1/user/send-email-code"
    var BASIC_URL_FORGET_PASSWORD = "/login-api/v1/user/reset-pwd"
    var BASIC_URL_GET_ORDER_ID = "/pay-api/v1/api/pay/init-order"
    var BASIC_URL_NOTIFY_ORDER = "/pay-api/v1/api/pay/check-order"
    var BASIC_URL_NOTIFY_REWARD = "/pay-api/v1/api/pay/check-pre-registration-order"
    var BASIC_URL_SUBMIT_ROLE = "/adv-api/v1/new-role/save"

    private var GAME_HOST = ""
    private var LOGIN_HOST = ""
    private var PAY_HOST = ""
    private var ADV_HOST = ""

    /**
     * 默认线上环境
     */
    var IP_MODEL = 3

    fun initHostModel(context: Context) {
        IP_MODEL = HostModelUtils.getHostModel(context)
        setDefaultHost()
    }

    private fun setDefaultHost() {
        when (IP_MODEL) {
            HostModelUtils.ENV_TEST -> {
                GAME_HOST = DEFAULT_TEST_GAME_HOST
                LOGIN_HOST = DEFAULT_TEST_LOGIN_HOST
                PAY_HOST = DEFAULT_TEST_PAY_HOST
                ADV_HOST = DEFAULT_TEST_ADV_HOST
            }
            HostModelUtils.ENV_ONLINE -> {
                GAME_HOST = DEFAULT_ONLINE_GAME_HOST
                LOGIN_HOST = DEFAULT_ONLINE_LOGIN_HOST
                PAY_HOST = DEFAULT_ONLINE_PAY_HOST
                ADV_HOST = DEFAULT_ONLINE_ADV_HOST
            }
        }

        BASIC_URL_INIT_SDK = GAME_HOST + BASIC_URL_INIT_SDK
        BASIC_URL_USER_VERIFY = LOGIN_HOST + BASIC_URL_USER_VERIFY
        BASIC_URL_USER_REGISTER = LOGIN_HOST + BASIC_URL_USER_REGISTER
        BASIC_URL_USER_BIND = LOGIN_HOST + BASIC_URL_USER_BIND
        BASIC_URL_GET_CAPTCHA = LOGIN_HOST + BASIC_URL_GET_CAPTCHA
        BASIC_URL_FORGET_PASSWORD = LOGIN_HOST + BASIC_URL_FORGET_PASSWORD
        BASIC_URL_GET_ORDER_ID = PAY_HOST + BASIC_URL_GET_ORDER_ID
        BASIC_URL_NOTIFY_ORDER = PAY_HOST + BASIC_URL_NOTIFY_ORDER
        BASIC_URL_NOTIFY_REWARD = PAY_HOST + BASIC_URL_NOTIFY_REWARD
        BASIC_URL_SUBMIT_ROLE = ADV_HOST + BASIC_URL_SUBMIT_ROLE
    }

}