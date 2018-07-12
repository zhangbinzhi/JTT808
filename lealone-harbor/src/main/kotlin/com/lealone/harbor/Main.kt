package com.lealone.harbor

import com.lealone.harbor.tcp.HarborServerVerticle
import com.lealone.harbor.utils.Config
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory


class HarborMain(private val vertx: Vertx) {
    private val log = LoggerFactory.getLogger(HarborMain::class.java)
    fun start() {
        Config()
        launch(vertx.dispatcher()) {
            awaitResult<String> { h -> vertx.deployVerticle(HarborServerVerticle(), h) }
            log.info("HarborServerVerticle success")
        }
    }
}