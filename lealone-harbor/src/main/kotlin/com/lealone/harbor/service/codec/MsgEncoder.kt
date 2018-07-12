package com.lealone.harbor.service.codec

import com.lealone.harbor.common.TPMSConsts
import com.lealone.harbor.utils.*
import com.lealone.harbor.vo.PackageData
import com.lealone.harbor.vo.req.TerminalRegisterMsg
import com.lealone.harbor.vo.res.ServerCommonRespMsgBody
import com.lealone.harbor.vo.res.TerminalRegisterMsgRespBody
import java.util.*

fun encode4TerminalRegisterResp(req: TerminalRegisterMsg, respMsgBody: TerminalRegisterMsgRespBody, flowId: Int): ByteArray {
    val msgBody = if (respMsgBody.replyCode == respMsgBody.success) {
        concatAll(
                Arrays.asList(
                        integerTo2Bytes(respMsgBody.replyFlowId),
                        byteArrayOf(respMsgBody.replyCode),
                        respMsgBody.replyToken!!.toByteArray(TPMSConsts.string_charset)
                )
        )
    } else {
        concatAll(Arrays.asList(//
                integerTo2Bytes(respMsgBody.replyFlowId), // 流水号(2)
                byteArrayOf(respMsgBody.replyCode)// 错误代码
        ))
    }
    val msgBodyProps = generateMsgBodyProps(msgBody.size, 0b000, false, 0)
    val msgHeader = generateMsgHeader(req.msgHeader.terminalPhone, TPMSConsts.cmd_terminal_register_resp, msgBody, msgBodyProps, flowId)
    val headerAndBody = concatAll(msgHeader, msgBody)
    val checkSum = getCheckSum4JT808(headerAndBody, 0, headerAndBody.size)
    return doEncode(headerAndBody, checkSum)

}

fun encode4ServerCommonRespMsg(req: PackageData, respMsgBody: ServerCommonRespMsgBody, flowId: Int): ByteArray {
    val msgBody = concatAll(Arrays.asList(//
            integerTo2Bytes(respMsgBody.replyFlowId), // 应答流水号
            integerTo2Bytes(respMsgBody.replyId), // 应答ID,对应的终端消息的ID
            byteArrayOf(respMsgBody.replyCode)// 结果
    ))

    // 消息头
    val msgBodyProps = generateMsgBodyProps(msgBody.size, 0, false, 0)
    val msgHeader = generateMsgHeader(req.msgHeader!!.terminalPhone!!,
            TPMSConsts.cmd_common_resp, msgBody, msgBodyProps, flowId)
    val headerAndBody = concatAll(msgHeader, msgBody)
    // 校验码
    val checkSum = getCheckSum4JT808(headerAndBody, 0, headerAndBody.size - 1)
    // 连接并且转义
    return doEncode(headerAndBody, checkSum)
}

@Throws(Exception::class)
fun doEncode(headerAndBody: ByteArray, checkSum: Int): ByteArray {
    val noEscapedBytes = concatAll(Arrays.asList(//
            byteArrayOf(TPMSConsts.pkg_delimiter), // 0x7e
            headerAndBody, // 消息头+ 消息体
            integerTo1Bytes(checkSum), // 校验码
            byteArrayOf(TPMSConsts.pkg_delimiter)// 0x7e
    ))
    // 转义
    return doEscape4Send(noEscapedBytes, 1, noEscapedBytes.size - 2)
}
