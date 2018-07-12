package com.lealone.harbor.http


import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult

class HarborHttpVerticle : CoroutineVerticle() {
    override suspend fun start() {
        val router = Router.router(vertx)
        router.route("/hello/:name").handler { it -> sayHello(it) }
        // Start the server
        awaitResult<HttpServer> {
            vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(config.getInteger("http.port", 8080), it)
        }

    }

    private fun sayHello(ctx: RoutingContext) {
        val id = ctx.pathParam("name")
        ctx.response().end("hello $id")
    }
}