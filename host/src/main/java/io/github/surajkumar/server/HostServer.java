package io.github.surajkumar.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

public class HostServer extends AbstractVerticle {
    private final String host;
    private final int port;

    public HostServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        NetServer server =
                vertx.createNetServer(new NetServerOptions().setPort(port).setHost(host));
        server.connectHandler(new HostServerHandler(1));
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
