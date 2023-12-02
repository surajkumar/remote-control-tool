package io.github.surajkumar;

import io.github.surajkumar.client.RemoteClient;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        LOGGER.info("Client Starting");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RemoteClient("localhost", 8080));
    }
}