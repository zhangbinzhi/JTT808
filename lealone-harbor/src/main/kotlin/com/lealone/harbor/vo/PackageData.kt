package com.lealone.harbor.vo

import com.lealone.harbor.service.handler.SocketHandler
import java.util.*

open class PackageData(val msgHeader: MsgHeader, val socket: SocketHandler) {
    var msgBodyBytes: ByteArray? = null
    var checkSum = -1
    //    var socket: SocketHandler? = null
    override fun toString(): String {
        return ("PackageData [msgHeader=" + msgHeader + ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) + ", checkSum="
                + checkSum + ", socket=" + socket.socket.remoteAddress() + "]")
    }

    class MsgHeader {
        // 消息ID
        var msgId: Int = 0
        /////// ========消息体属性
        // byte[2-3]
        var msgBodyPropsField: Int = 0
        // 消息体长度
        var msgBodyLength: Int = 0

        // 数据加密方式
        var encryptionType: Int = 0
        // 是否分包,true==>有消息包封装项
        var hasSubPackage: Boolean = false
        // 保留位[14-15]
        var reservedBit: String = ""
        /////// ========消息体属性

        // 终端手机号
        var terminalPhone: String = ""
        // 流水号
        var flowId: Int = 0

        //=====消息包封装项
        // byte[12-15]
        var packageInfoField: Int = 0
        // 消息包总数(word(16))
        var totalSubPackage: Int = 0
        // 包序号(word(16))这次发送的这个消息包是分包中的第几个消息包, 从 1 开始
        var subPackageSeq: Int = 0


        override fun toString(): String {
            return ("MsgHeader [msgId=" + msgId + ", msgBodyPropsField=" + msgBodyPropsField + ", msgBodyLength="
                    + msgBodyLength + ", encryptionType=" + encryptionType + ", hasSubPackage=" + hasSubPackage
                    + ", reservedBit=" + reservedBit + ", terminalPhone=" + terminalPhone + ", flowId=" + flowId
                    + ", packageInfoField=" + packageInfoField + ", totalSubPackage=" + totalSubPackage
                    + ", subPackageSeq=" + subPackageSeq + "]")
        }
    }
}

