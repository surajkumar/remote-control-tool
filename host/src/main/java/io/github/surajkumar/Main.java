package io.github.surajkumar;

import io.github.surajkumar.screen.Monitor;
import io.github.surajkumar.server.AddressInfo;
import io.github.surajkumar.server.HostCodec;
import io.github.surajkumar.server.HostServer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String HOST = System.getenv().getOrDefault("host", "localhost");
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("port", "8080"));

    public static void main(String[] args) {
        LOGGER.info(
                "Your public identifier is: " + HostCodec.encode(AddressInfo.getAddress(), PORT));

        VertxOptions options = new VertxOptions().setPreferNativeTransport(true);
        Vertx vertx = Vertx.vertx(options);

        int monitor;
        if (args.length > 0) {
            try {
                monitor = Integer.parseInt(args[0]);
                if (Monitor.getNumberOfMonitors() > monitor) {
                    LOGGER.error(
                            "Invalid monitor {} the number must be between 0 and {}",
                            monitor,
                            Monitor.getNumberOfMonitors());
                    return;
                }
            } catch (Exception e) {
                monitor = 0;
                LOGGER.warn("Unknown arg {}, defaulting to monitor 0", Arrays.toString(args));
            }
        } else {
            monitor = 0;
        }

        vertx.deployVerticle(
                new HostServer(HOST, PORT, monitor),
                res -> {
                    if (res.succeeded()) {
                        LOGGER.info("Server is online");
                    } else {
                        LOGGER.error("Error: " + res.cause().getMessage());
                    }
                });
    }
}
