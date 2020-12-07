package cn.flyfun.ktx.gamesdk.base.entity

/**
 * Author #Suyghur.
 * Created on 10/22/20
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class FunctionName {
    companion object {
        const val ATTACH_BASE_CONTEXT = "attachBaseContext"
        const val INIT_APPLICATION = "initApplication"
        const val INITIALIZE = "initialize"
        const val LOGIN = "login"
        const val LOGOUT = "logout"
        const val CHARGE = "charge"
        const val SHOW_EXIT_VIEW = "showExitView"
        const val BIND_PLATFORM_ACCOUNT = "bindPlatformAccount"
        const val OPEN_GM_CENTER = "openGmCenter"
        const val ROLE_CREATE = "roleCreate"
        const val ROLE_LAUNCHER = "roleLauncher"
        const val ROLE_UPGRADE = "roleUpgrade"
        const val ON_START = "onStart"
        const val ON_RESUME = "onResume"
        const val ON_RESTART = "onRestart"
        const val ON_PAUSE = "onPause"
        const val ON_STOP = "onStop"
        const val ON_DESTROY = "onDestroy"
        const val ON_NEW_INTENT = "onNewIntent"
        const val ON_ACTIVITY_RESULT = "onActivityResult"
        const val ON_CONFIGURATION_CHANGED = "onConfigurationChanged"
        const val ON_REQUEST_PERMISSIONS_RESULT = "onRequestPermissionsResult"
        const val GET_CURRENT_USER_ID = "getCurrentUserId"
        const val GET_CURRENT_SDK_VERSION = "getCurrentSdkVersion"
        const val IS_BIND_PLATFORM_ACCOUNT = "isBindPlatformAccount"
        const val IS_GM_CENTER_ENABLE = "isGmCenterEnable"
    }
}