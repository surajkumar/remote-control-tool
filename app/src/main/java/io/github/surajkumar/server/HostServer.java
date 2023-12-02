package io.github.surajkumar.server;

import io.github.surajkumar.Main;
import io.github.surajkumar.server.screen.Monitor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class HostServer extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostServer.class);
    private final String host;
    private final int port;
    private final int monitor;

    public HostServer(String host, int port, int monitor) {
        this.host = host;
        this.port = port;
        this.monitor = monitor;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        NetServer server =
                vertx.createNetServer(new NetServerOptions().setPort(port).setHost(host));
        server.connectHandler(new HostServerHandler(monitor));
        server.listen(
                res -> {
                    if (res.failed()) {
                        startPromise.fail(res.cause());
                    } else {
                        startPromise.complete();
                    }
                });
    }
}
