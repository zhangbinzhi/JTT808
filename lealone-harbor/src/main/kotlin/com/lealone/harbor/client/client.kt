package com.lealone.harbor.client

import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.LoggerFactory

class clientVerticle : CoroutineVerticle() {
    private val port = 7788
    private val ip = "127.0.0.1"
    private lateinit var socket: NetSocket
    private val log = LoggerFactory.getLogger(clientVerticle::class.java)
    override suspend fun start() {
        val client = vertx.createNetClient(NetClientOptions().setConnectTimeout(10000))
        try {
            socket = awaitResult { h ->
                client.connect(port, ip, h)
            }
            socket.handler{
                log.info(it.length().toString())
            }
            log.info("connect to server success")
        } catch (e: Exception) {
            log.error("failed to connect", e)
        }

    }
}
