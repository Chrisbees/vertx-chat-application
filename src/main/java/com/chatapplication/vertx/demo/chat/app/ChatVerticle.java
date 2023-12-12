package com.chatapplication.vertx.demo.chat.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(ChatVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        Router router = Router.router(vertx);
        router.get("/chat/:userId").handler(new WebSocketChatHandler());

        vertx.createHttpServer(new HttpServerOptions()
                        .setRegisterWebSocketWriteHandlers(true))
                .requestHandler(router)
                .listen(8000)
                .onSuccess(res -> {
                    LOG.info("Server started on port {}", res.actualPort());
                }).onFailure(res -> {
                    LOG.error("Server failed to start because {0}", res);
                });

    }
}
