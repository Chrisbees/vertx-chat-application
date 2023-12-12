package com.chatapplication.vertx.demo.chat.app;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WebSocketChatHandler implements Handler<RoutingContext> {
    private final Logger LOG = LoggerFactory.getLogger(WebSocketChatHandler.class);
    Map<String, ServerWebSocket> connectedUsers = new HashMap<>();

    @Override
    public void handle(RoutingContext context) {
        Future<ServerWebSocket> fut = context.request().toWebSocket();
        fut.onSuccess(socket -> {
            String userId = context.pathParam("userId");
            if (!("/chat/" + userId).equals(socket.path())){
                socket.writeTextMessage("Error, this path is not accepted");
                socket.writeTextMessage("only accepts " + "/chat/"+userId);
                LOG.info("Wrong Path entered");
                socket.close();
                return;
            }
            socket.accept();
            socket.writeTextMessage("user " + userId + " is Connected");
            LOG.info("WebSocket accepted with " + userId);
            connectedUsers.put(userId, socket);
            socket.exceptionHandler(err -> LOG.info("Connection failed {0}", err));
            socket.endHandler(onClose -> {
                LOG.info("Connection closed {}", onClose);
                socket.close();
            });
            socket.textMessageHandler(handleMessage(userId));
        }).onFailure(err -> {
            LOG.error("Failed to establish WebSocket connection: {}", err.getMessage());
        });
    }

    private Handler<String> handleMessage(String userId) {
        return message -> {
            String[] parts = message.split(":", 2);
            if (parts.length == 2) {
                String targertedUserId = parts[0];
                String connectedUserMessage = parts[1];
                ServerWebSocket targetSocket = connectedUsers.get(targertedUserId);
                if (targetSocket != null) {
                    targetSocket.writeTextMessage(userId + ":" + connectedUserMessage);
                }

            }

        };
    }
}
