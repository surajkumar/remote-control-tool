package io.github.surajkumar.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RemoteClient extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);
    private final String host;
    private final int port;
    private RemoteViewer displayScreen;
    private Buffer buffer;
    private int expectedLength = -1;

    public RemoteClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.buffer = Buffer.buffer();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        NetClient client = vertx.createNetClient();
        client.connect(
                port,
                host,
                res -> {
                    if (res.succeeded()) {
                        NetSocket socket = res.result();

                        HandshakeMessage handshakeMessage =
                                new HandshakeMessage(getComputerName(), "hello");
                        Buffer handshakeBuffer = Buffer.buffer();
                        handshakeBuffer.appendInt(handshakeMessage.name().length());
                        handshakeBuffer.appendInt(handshakeMessage.password().length());
                        handshakeBuffer.appendString(handshakeMessage.name(), "utf-8");
                        handshakeBuffer.appendString(handshakeMessage.password(), "utf-8");
                        socket.write(handshakeBuffer);

                        socket.handler(receivedBuffer -> {
                            if (expectedLength == -1 && buffer.length() == 0) {
                                expectedLength = receivedBuffer.getInt(0);
                                LOGGER.debug(
                                        "(1) Expecting a message of length: {}", expectedLength);
                            } else if(expectedLength == -1 && buffer.length() > 0) {
                                expectedLength = buffer.getInt(0);
                                LOGGER.debug(
                                        "(2) Expecting a message of length: {}", expectedLength);
                            }
                            if(buffer.length() >= expectedLength) {
                                LOGGER.debug(
                                        "Received a complete message. Buffer length: {}\n\n",
                                        buffer.length());

                                if (displayScreen == null) {
                                    displayScreen = new RemoteViewer(socket);
                                }
                                displayScreen.draw(buffer.getBuffer(4, expectedLength));

                                Buffer newBuffer = Buffer.buffer();
                                newBuffer.appendBuffer(buffer.getBuffer(expectedLength, buffer.length()));
                                newBuffer.appendBuffer(receivedBuffer);
                                buffer = newBuffer;
                                expectedLength = -1;
                            } else {
                                LOGGER.debug("Received partial data, current size is {} and expecting {}", buffer.length(), expectedLength);
                                buffer.appendBuffer(receivedBuffer);
                            }
                        });
                    } else {
                        LOGGER.info("Failed to connect to the server: " + res.cause().getMessage());
                    }
                });
    }

    private static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        }
        return env.getOrDefault("HOSTNAME", "Unknown Computer");
    }
}
