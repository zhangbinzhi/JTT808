package com.lealone.harbor

import com.lealone.harbor.utils.Config
import io.vertx.core.Vertx


fun main(args: Array<String>) {
    Config()
    val harbor = HarborMain(Vertx.vertx())
    harbor.start()
}