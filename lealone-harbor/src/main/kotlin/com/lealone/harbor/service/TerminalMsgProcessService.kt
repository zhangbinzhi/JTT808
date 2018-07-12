package com.lealone.harbor.service

import com.lealone.harbor.service.codec.encode4ServerCommonRespMsg
import com.lealone.harbor.service.codec.encode4TerminalRegisterResp
import com.lealone.harbor.vo.PackageData
import com.lealone.harbor.vo.req.LocationInfoUploadMsg
import com.lealone.harbor.vo.req.TerminalAuthenticationMsg
import com.lealone.harbor.vo.req.TerminalRegisterMsg
import com.lealone.harbor.vo.res.ServerCommonRespMsgBody
import com.lealone.harbor.vo.res.ServerCommonSuccess
import com.lealone.harbor.vo.res.TerminalRegisterMsgRespBody

fun processTerminalHeartBeatMsg(msg: PackageData) {
    val reqHeader = msg.msgHeader
    val respMsgBody = ServerCommonRespMsgBody(reqHeader.flowId, reqHeader.msgId, ServerCommonSuccess)
    val flowId = msg.socket.currentFlowId()
//    val bs = encode4ServerCommonRespMsg(msg, respMsgBody, flowId)
    msg.socket.send2Client(encode4ServerCommonRespMsg(msg, respMsgBody, flowId))
}

fun processRegisterMsg(msg: TerminalRegisterMsg?) {
    if (msg == null) return
    val respMsgBody = TerminalRegisterMsgRespBody()
    // TODO 先不做注册校验，每次注册都通过
    respMsgBody.replyCode = respMsgBody.success
    respMsgBody.replyFlowId = msg.msgHeader.flowId
    // TODO 鉴权码暂时写死
    respMsgBody.replyToken = "123"
    var flowId = msg.socket.currentFlowId()
    msg.socket.send2Client(encode4TerminalRegisterResp(msg, respMsgBody, flowId))
}

fun processLocationInfoUploadMsg(location: LocationInfoUploadMsg?) {
    if (location == null) {
        return
    }
    val reqHeader = location.msgHeader
    val respMsgBody = ServerCommonRespMsgBody(reqHeader.flowId, reqHeader.msgId, ServerCommonSuccess)
    val flowId = location.socket.currentFlowId()
    location.socket.send2Client(encode4ServerCommonRespMsg(location, respMsgBody, flowId))
}

fun processTerminalLogoutMsg(req: PackageData) {
    val reqHeader = req.msgHeader
    val respMsgBody = ServerCommonRespMsgBody(reqHeader.flowId, reqHeader.msgId, ServerCommonSuccess)
    req.socket.send2Client(encode4ServerCommonRespMsg(req, respMsgBody, req.socket.currentFlowId()))
}

fun processAuthMsg(req: TerminalAuthenticationMsg) {
    // TODO 暂时每次鉴权都成功,后续需要增加鉴权步骤
    val header = req.msgHeader
    val respMsgBody = ServerCommonRespMsgBody(header.flowId, header.msgId, ServerCommonSuccess)
    req.socket.send2Client(encode4ServerCommonRespMsg(req, respMsgBody, req.socket.currentFlowId()))

}