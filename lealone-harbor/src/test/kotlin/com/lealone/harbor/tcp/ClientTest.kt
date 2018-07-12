package com.lealone.harbor.tcp

import com.lealone.harbor.utils.Config
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.launch

fun main(args: Array<String>) {
    Config()
    val vertx = Vertx.vertx()
    launch(vertx.dispatcher()) {
        val a = awaitResult<String> { vertx.deployVerticle("com.lealone.harbor.client.clientVerticle", it) }
        println(a)
    }

}