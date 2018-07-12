package com.lealone.harbor.service.handler

import com.lealone.harbor.common.TPMSConsts
import com.lealone.harbor.service.*
import com.lealone.harbor.service.codec.MsgDecoder
import com.lealone.harbor.vo.req.TerminalAuthenticationMsg
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory

class SocketHandler(private val vertx: Vertx, val socket: NetSocket, private val decoder: MsgDecoder) {
    private val log = LoggerFactory.getLogger(SocketHandler::class.java)
    private var currentFlowId: Int = 0

    init {
        log.info("connection arrive")
        socket.closeHandler {
            log.info("socket close")
        }
        socket.handler {
            parseDelimited(it)
        }
    }

    private var msgByte = ByteArray(1024)
    private var msgByteCnt = 0
    private var msgStart = false
    private val maxSize = 1024
    private val delim: Byte = TPMSConsts.pkg_delimiter

    private fun parseDelimited(buffer: Buffer) {
        val len = buffer.length()
        var pos = 0
        while (pos < len) {
            //检查是否有消息头尾标志
            val currentByte = buffer.getByte(pos)
            pos++
            if (currentByte == this.delim) {
                //若消息尚未开始，则开始记录消息
                if (!msgStart) {
                    msgStart = true
                    log.info("msg start")
                } else {
                    //若消息已经开始，则表示消息结束
                    if (msgByteCnt < 12) {
                        if (msgByteCnt == 0) {
                            //如果连续连个标志，则无视第一个标记
                            continue
                        }
                        msgStart = false
                        //最短的消息头也得有12字节
                        log.warn("too less bytes")
                        msgByteCnt = 0
                        continue
                    }
                    log.info("msg end")
                    val msgContent = msgByte.copyOfRange(0, msgByteCnt)
                    msgByteCnt = 0
                    launch(vertx.dispatcher()) {
                        processPackageData(msgContent)
                    }
                }
            } else {
                if (msgStart) {
                    msgByte[msgByteCnt] = currentByte
                    msgByteCnt++
                    if (msgByteCnt >= maxSize) {
                        //若超过最大长度后仍未检测到消息结束标志，则丢弃当前接收到的数据
                        log.warn("too much byte!")
                        msgStart = false
                        msgByteCnt = 0
                    }
                }
            }
        }
    }

    private fun processPackageData(msgContent: ByteArray) {
        val pkg = decoder.bytes2PackageData(msgContent, this)
        val header = pkg.msgHeader
        log.info(pkg.msgHeader.toString())
        when (header.msgId) {
        // 1. 终端心跳-消息体为空 ==> 平台通用应答
            TPMSConsts.msg_id_terminal_heart_beat -> {
                log.info(">>>>>[终端心跳],phone={},flowid={}", header.terminalPhone, header.flowId)
                processTerminalHeartBeatMsg(pkg)
                log.info("<<<<<[终端心跳],phone={},flowid={}", header.terminalPhone, header.flowId)
            }
        // 3. 位置信息汇报 ==> 平台通用应答
            TPMSConsts.msg_id_terminal_location_info_upload -> {
                log.info(">>>>>[位置信息],phone={},flowid={}", header.terminalPhone, header.flowId)
                val locationInfoUploadMsg = decoder.toLocationInfoUploadMsg(pkg)
                //TODO 后续需在在这里插入数据库
                processLocationInfoUploadMsg(locationInfoUploadMsg)
                log.info("<<<<<[位置信息],phone={},flowid={}", header.terminalPhone, header.flowId)
            }
        // 5. 终端鉴权 ==> 平台通用应答
            TPMSConsts.msg_id_terminal_authentication -> {
                log.info(">>>>>[终端鉴权],phone={},flowid={}", header.terminalPhone, header.flowId)
                val authenticationMsg = TerminalAuthenticationMsg(pkg)
                processAuthMsg(authenticationMsg)
                log.info("<<<<<[终端鉴权],phone={},flowid={}", header.terminalPhone, header.flowId)
            }
        // 6. 终端注册 ==> 终端注册应答
            TPMSConsts.msg_id_terminal_register -> {
                log.info(">>>>>[终端注册],phone={},flowid={}", header.terminalPhone, header.flowId)
//                var msg = decoder.toTerminalRegisterMsg(pkg)
                processRegisterMsg(decoder.toTerminalRegisterMsg(pkg))
                log.info("<<<<<[终端注册],phone={},flowid={}", header.terminalPhone, header.flowId)
            }
        // 7. 终端注销(终端注销数据消息体为空) ==> 平台通用应答
            TPMSConsts.msg_id_terminal_log_out -> {
                log.info(">>>>>[终端注销],phone={},flowid={}", header.terminalPhone, header.flowId)
                processTerminalLogoutMsg(pkg)
                log.info("<<<<<[终端注销],phone={},flowid={}", header.terminalPhone, header.flowId)
            }
        // 其他情况
            else -> {
                log.error(">>>>>>[未知消息类型],phone={},msgId={},package={}", header.terminalPhone, header.msgId,
                        pkg)
            }
        }
        socket.write(Buffer.buffer())
    }

    fun send2Client(bs: ByteArray) {
        socket.write(Buffer.buffer(bs))
    }

    @Synchronized
    fun currentFlowId(): Int {
        if (currentFlowId >= 0xffff)
            currentFlowId = 0
        return currentFlowId++
    }
}