package com.flyfun.demo

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.*
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.*

import cn.flyfun.gamesdk.base.FlyFunGame
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.gamesdk.base.internal.ICallback
import cn.flyfun.support.encryption.Md5Utils
import cn.flyfun.support.jarvis.Toast
import org.json.JSONException
import org.json.JSONObject
import kotlin.system.exitProcess

/**
 * @author #Suyghur
 * Created on 2020/12/7
 */
class DemoActivity : Activity(), View.OnClickListener {

    private val events = arrayListOf(
            Item(0, "00 接口环境切换"),
            Item(1, "01 登录"),
            Item(2, "02 切换账号"),
            Item(3, "03 角色创建上报"),
            Item(4, "04 角色登录上报"),
            Item(5, "05 角色升级上报"),
            Item(6, "06 定额充值"),
            Item(7, "07 绑定平台账号"),
            Item(8, "08 打开客服中心"),
            Item(9, "09 崩溃测试"),
            Item(10, "10 Facebook分享测试")
//            Item(11, "11 截屏")
    )

    private lateinit var layout: LinearLayout
    private lateinit var mTextView: TextView
    private lateinit var bindButton: Button
    private lateinit var gmButton: Button
    private lateinit var imageView: ImageView
    private var cacheRoleInfo: CacheRoleInfo.Companion.RoleInfo? = null
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                111 -> {
                    with(mTextView) {
                        text = text.toString() + msg.obj.toString()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlyFunGame.getInstance().initialize(this, false, object : ICallback {
            override fun onResult(code: Int, result: String) {
                if (code == 0) {
                    FlyFunGame.getInstance().login(this@DemoActivity, true, object : ICallback {
                        override fun onResult(code: Int, result: String) {
                            verifyUserLogin(code, result)
                        }
                    })
                }
            }
        })
        initView()
        FlyFunGame.getInstance().logHandler(handler)

    }

    private fun initView() {
        layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        imageView = ImageView(this)
        layout.addView(imageView)
        initButton()
        mTextView = TextView(this)
        with(mTextView) {
            text = ""
            this@DemoActivity.layout.addView(this)
        }
        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun initButton() {
        for (event in events) {
            when (event.id) {
                7 -> {
                    bindButton = Button(this)
                    with(bindButton) {
                        text = event.name
                        tag = event.id
                        id = event.id
                        setOnClickListener(this@DemoActivity)
                        visibility = View.GONE
                        this@DemoActivity.layout.addView(this)
                    }
                }
                8 -> {
                    gmButton = Button(this)
                    with(gmButton) {
                        text = event.name
                        tag = event.id
                        id = event.id
                        setOnClickListener(this@DemoActivity)
                        visibility = View.GONE
                        this@DemoActivity.layout.addView(this)
                    }
                }
                else -> {
                    with(Button(this)) {
                        text = event.name
                        tag = event.id
                        id = event.id
                        setOnClickListener(this@DemoActivity)
                        this@DemoActivity.layout.addView(this)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        mTextView.text = ""
        v?.apply {
            when (tag as Int) {
                0 -> EnvActivity.start(this@DemoActivity)
                1 -> FlyFunGame.getInstance().login(this@DemoActivity, true, object : ICallback {
                    override fun onResult(code: Int, result: String) {
                        verifyUserLogin(code, result)
                    }
                })
                2 -> FlyFunGame.getInstance().logout(this@DemoActivity, object : ICallback {
                    override fun onResult(code: Int, result: String) {
                        if (code == 0) {
                            hideBindButton()
                            hideGMButton()
                            FlyFunGame.getInstance().login(this@DemoActivity, false, object : ICallback {
                                override fun onResult(code: Int, result: String) {
                                    verifyUserLogin(code, result)
                                }
                            })
                        }
                    }
                })
                3 -> {
                    //创建新角色
                    cacheRoleInfo = null
                    cacheRoleInfo = CacheRoleInfo.setDemoRoleInfo(
                            this@DemoActivity,
                            FlyFunGame.getInstance().getCurrentUserId()
                    )
                    FlyFunGame.getInstance().roleCreate(this@DemoActivity, getGameRoleInfo())
                }
                4 -> FlyFunGame.getInstance().roleLauncher(this@DemoActivity, getGameRoleInfo())
                5 -> FlyFunGame.getInstance().roleUpgrade(this@DemoActivity, getGameRoleInfo())
                6 -> FlyFunGame.getInstance()
                        .charge(this@DemoActivity, getGameChargeInfo(), object : ICallback {
                            override fun onResult(code: Int, result: String) {
                                if (code == 0) {
                                    Toast.toastInfo(this@DemoActivity, "(demo提示)支付流程完成")
                                } else {
                                    Toast.toastInfo(this@DemoActivity, "(demo提示)支付失败，$result")
                                }
                            }

                        })
                7 -> FlyFunGame.getInstance()
                        .openBindAccount(this@DemoActivity, object : ICallback {
                            override fun onResult(code: Int, result: String) {
                                if (code == 0) {
                                    Toast.toastInfo(this@DemoActivity, "(demo提示)绑定账号成功")
                                } else {
                                    Toast.toastInfo(this@DemoActivity, "(demo提示)绑定账号失败")
                                }
                            }
                        })
                8 -> FlyFunGame.getInstance().openGmCenter(this@DemoActivity, object : ICallback {
                    override fun onResult(code: Int, result: String) {
                        if (code != 0) {
                            Toast.toastInfo(this@DemoActivity, "(demo提示)$result")
                        }
                    }
                })
                9 -> createCrash()
            }
        }
    }

    private fun createCrash() {
        throw RuntimeException("test crash")
    }

    private fun getGameRoleInfo(): GameRoleInfo {
        val gameRoleInfo = GameRoleInfo()
        //用户ID
        gameRoleInfo.userId = FlyFunGame.getInstance().getCurrentUserId()
        //角色ID
        gameRoleInfo.roleId = cacheRoleInfo?.roleId
        //角色名称
        gameRoleInfo.roleName = cacheRoleInfo?.roleName
        //角色等级
        gameRoleInfo.roleLevel = cacheRoleInfo?.roleLevel
        //服务器ID
        gameRoleInfo.serverCode = cacheRoleInfo?.serverCode
        //服务器名
        gameRoleInfo.serverName = cacheRoleInfo?.serverName
        //用户VIP等级，无该字段则传空串""
        gameRoleInfo.vipLevel = cacheRoleInfo?.vipLevel
        //当前角色游戏币余额
        gameRoleInfo.balance = cacheRoleInfo?.balance
        return gameRoleInfo
    }

    private fun getGameChargeInfo(): GameChargeInfo {
        val gameChargeInfo = GameChargeInfo()
        val orderId = "order_" + System.currentTimeMillis()
        //用户ID
        gameChargeInfo.userId = FlyFunGame.getInstance().getCurrentUserId()
        //角色ID
        gameChargeInfo.roleId = cacheRoleInfo?.roleId
        //角色名称
        gameChargeInfo.roleName = cacheRoleInfo?.roleName
        //角色等级
        gameChargeInfo.roleLevel = cacheRoleInfo?.roleLevel
        //服务器ID
        gameChargeInfo.serverCode = cacheRoleInfo?.serverCode
        //服务器名
        gameChargeInfo.serverName = cacheRoleInfo?.serverName
        //游戏的订单号
        gameChargeInfo.cpOrderId = orderId
        //游戏的发货地址（支付回调地址）
        gameChargeInfo.cpNotifyUrl = "http://www.flyfungame.com/?ac=order&ct=notify"
        //透传字段，会在回调地址中原样返回
        gameChargeInfo.cpCallbackInfo = "cp_callback_info||$orderId"
        //金额，单位分，币种美金
        gameChargeInfo.price = 0.99f
        //商品ID，计费点
        gameChargeInfo.productId = "com.flyfun.ylj.60"
        //商品名称
        gameChargeInfo.productName = "70元宝"
        //商品描述
        gameChargeInfo.productDesc = "70元宝"
        return gameChargeInfo
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FlyFunGame.getInstance().openExitView(this, object : ICallback {
                override fun onResult(code: Int, result: String) {
                    if (code == 0) {
                        finish()
                    }
                }
            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        FlyFunGame.getInstance().onStart(this)
    }

    override fun onResume() {
        super.onResume()
        FlyFunGame.getInstance().onResume(this)
    }

    override fun onRestart() {
        super.onRestart()
        FlyFunGame.getInstance().onRestart(this)
    }

    override fun onPause() {
        super.onPause()
        FlyFunGame.getInstance().onPause(this)
    }

    override fun onStop() {
        super.onStop()
        FlyFunGame.getInstance().onStop(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        FlyFunGame.getInstance().onDestroy(this)
        exitProcess(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FlyFunGame.getInstance().onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        FlyFunGame.getInstance().onNewIntent(this, intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FlyFunGame.getInstance().onConfigurationChanged(this, newConfig)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FlyFunGame.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }


    private fun verifyUserLogin(code: Int, result: String) {
        //用户登录校验，请尽量放在服务端处理
        if (code == 0 && !TextUtils.isEmpty(result)) {
            try {
                val jsonObject = JSONObject(result)
                val userId = jsonObject.getString("user_id")
                val timestamp = jsonObject.getString("timestamp")
                val cpSign = jsonObject.getString("cp_sign")
                //app_secret+user_id+timestamp('+'只做连接，不参与加密)
                val sign = Md5Utils.encodeByMD5(APP_SECRET + userId + timestamp)
                if (sign == cpSign) {
                    Toast.toastInfo(this, "(demo提示)登录成功，user_id : $userId")
                    cacheRoleInfo = CacheRoleInfo.getDemoRoleInfo(this, userId)
                    //登录校验成功后判断当前用户是否已经绑定平台账号，否则在游戏中显示入口
                    if (FlyFunGame.getInstance().hasBindAccount()) {
                        //隐藏绑定平台账号的入口
                        hideBindButton()
                    } else {
                        //显示绑定平台账号的入口
                        showBindButton()
                    }
                    //登录校验成功后判断SDK客服中心是否开启，否则在游戏中关闭对应的入口
                    if (FlyFunGame.getInstance().isGmCenterEnable()) {
                        showGMButton()
                    } else {
                        hideGMButton()
                    }
                } else {
                    Toast.toastInfo(this, "(demo提示)登录失败，用户信息校验失败")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            Toast.toastInfo(this, "(demo提示)$result")
        }
    }

    private fun hideBindButton() {
        bindButton.apply {
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
            }
        }
    }

    private fun showBindButton() {
        bindButton.apply {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
            }
        }
    }

    private fun hideGMButton() {
        gmButton.apply {
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
            }
        }
    }

    private fun showGMButton() {
        gmButton.apply {
            if (visibility == View.GONE) {
                visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private const val APP_SECRET = "xxxx"
    }
}