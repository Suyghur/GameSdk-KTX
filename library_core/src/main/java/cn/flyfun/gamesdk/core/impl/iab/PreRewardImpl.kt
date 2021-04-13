package cn.flyfun.gamesdk.core.impl.iab

import android.app.Activity
import android.content.Context
import cn.flyfun.gamesdk.core.entity.GameRewardInfo

/**
 * @author #Suyghur.
 * Created on 4/13/2021
 */
class PreRewardImpl : InAppBilling() {

    private var rewardInfo: GameRewardInfo? = null

    fun checkPreReward(context: Context, rewardInfo: GameRewardInfo) {
        this.rewardInfo = rewardInfo
        //初始化IAB收银台客户端
        initializeBillingClient(context)
        //连接谷歌商店
        connectGooglePlay(context)
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