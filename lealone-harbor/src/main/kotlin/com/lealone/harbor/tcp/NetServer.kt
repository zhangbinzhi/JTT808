package com.lealone.harbor.tcp


import com.lealone.harbor.service.codec.MsgDecoder
import com.lealone.harbor.service.handler.SocketHandler
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetServer

import io.vertx.core.net.NetServerOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.LoggerFactory

class HarborServerVerticle : CoroutineVerticle() {
    private lateinit var server: NetServer
    private lateinit var decoder: MsgDecoder
    private val log = LoggerFactory.getLogger(HarborServerVerticle::class.java)
    override suspend fun start() {
        val netOption = NetServerOptions().setPort(7788)
                .setHost("127.0.0.1")
        decoder = MsgDecoder()
        try {
            server = vertx.createNetServer(netOption)
            server.connectHandler { socket ->
                SocketHandler(vertx, socket, decoder)
            }
            awaitResult<NetServer> { h ->
                server.listen(h)
            }
            log.info("netServer start success at :" + server.actualPort())
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}