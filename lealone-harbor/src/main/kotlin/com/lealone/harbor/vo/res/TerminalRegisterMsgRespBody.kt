package com.lealone.harbor.vo.res


class TerminalRegisterMsgRespBody {
    val success: Byte = 0
    val car_already_registered: Byte = 1
    val car_not_found: Byte = 2
    val terminal_already_registered: Byte = 3
    val terminal_not_found: Byte = 4
    // byte[0-1] 应答流水号(WORD) 对应的终端注册消息的流水号
    var replyFlowId: Int = 0
    /***
     * byte[2] 结果(BYTE) <br></br>
     * 0：成功<br></br>
     * 1：车辆已被注册<br></br>
     * 2：数据库中无该车辆<br></br>
     */
     var replyCode: Byte = 0

    // byte[3-x] 鉴权码(STRING) 只有在成功后才有该字段
     var replyToken: String? = null

    override fun toString(): String {
        return "TerminalRegisterMsgRespBody(replyFlowId=$replyFlowId, replyCode=$replyCode, replyToken=$replyToken)"
    }


}