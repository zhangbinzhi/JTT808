package com.lealone.harbor.vo.res

val ServerCommonSuccess: Byte = 0
val ServerCommonFailure: Byte = 1
val ServerCommonMsg_error: Byte = 2
val ServerCommonUnsupported: Byte = 3
val ServerCommonWarnning_msg_ack: Byte = 4

class ServerCommonRespMsgBody(val replyFlowId: Int, val replyId: Int, val replyCode: Byte) {
    override fun toString(): String {
        return "ServerCommonRespMsgBody(replyFlowId=$replyFlowId, replyId=$replyId, replyCode=$replyCode)"
    }


}
