package com.chatapplication.vertx.demo.chat.app.config;


import com.chatapplication.vertx.demo.chat.app.ChatVerticle;
import io.vertx.core.Vertx;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Configurations {

    private final ChatVerticle chatVerticle;

    @PostConstruct
    public void config(){
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(chatVerticle.getClass().getName());
    }

}
