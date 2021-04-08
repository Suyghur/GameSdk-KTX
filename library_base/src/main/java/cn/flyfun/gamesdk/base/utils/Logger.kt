package cn.flyfun.gamesdk.base.utils

import cn.flyfun.support.jarvis.LogRvds

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
object Logger {

    private const val TAG: String = "flyfun_game"

    @JvmStatic
    fun i(any: Any) {
        LogRvds.i(TAG, any)
    }

    @JvmStatic
    fun e(msg: String) {
        LogRvds.e(TAG, msg)
    }

    @JvmStatic
    fun d(any: Any) {
        d(TAG, any)
    }

    @JvmStatic
    fun d(tag: String, any: Any) {
        if (LogRvds.DEBUG) {
            LogRvds.d(tag, any)
        }
    }

    @JvmStatic
    fun logHandler(msg: String) {
        LogRvds.logHandler(msg)
    }
}