package cn.flyfun.ktx.gamesdk.core.entity

/**
 * @author #Suyghur.
 * Created on 10/22/20
 */
class ResultInfo {
    /**
     * 0是成功，其他是错误。
     */
    @JvmField
    var code: Int = -1

    /**
     * 返回信息
     */
    @JvmField
    var msg: String = ""

    /**
     * 返回的数据
     */
    @JvmField
    var data: String = ""

    override fun toString(): String {
        return "ResultInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}'
    }

    init {
        code = -1
        msg = ""
        data = ""
    }
}