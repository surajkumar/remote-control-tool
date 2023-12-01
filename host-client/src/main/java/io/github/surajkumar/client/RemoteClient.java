package io.github.surajkumar.client;

import io.github.surajkumar.host.server.HandshakeMessage;
import io.github.surajkumar.utils.HostCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RemoteClient extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClient.class);
    private final String host;
    private final int port;
    private RemoteViewer displayScreen;
    private Buffer buffer;
    private int expectedLen = 0;

    public RemoteClient(String host, int port) {
        this.host = HostCodec.decode(host);
        this.port = port;
        this.buffer = Buffer.buffer();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        NetClient client = vertx.createNetClient();
        client.connect(port, host, res -> {
            if (res.succeeded()) {
                NetSocket socket = res.result();

                HandshakeMessage handshakeMessage = new HandshakeMessage(getComputerName(), "hello");
                Buffer handshakeBuffer = Buffer.buffer();
                handshakeBuffer.appendInt(handshakeMessage.name().length());
                handshakeBuffer.appendInt(handshakeMessage.password().length());
                handshakeBuffer.appendString(handshakeMessage.name(), "utf-8");
                handshakeBuffer.appendString(handshakeMessage.password(), "utf-8");
                socket.write(handshakeBuffer);

                socket.handler(b -> {
                    LOGGER.info("Received data. Buffer length: {}, Expected length: {}", buffer.length(), expectedLen);

                    if (expectedLen == 0) {
                        expectedLen = b.getInt(0);
                        LOGGER.info("Expecting a message of length: {}", expectedLen);
                    }

                    if (buffer.length() < expectedLen) {
                        buffer.appendBuffer(b);
                        LOGGER.info("Appending data to buffer. New buffer length: {}", buffer.length());
                    }

                    if (buffer.length() >= expectedLen) {
                        LOGGER.info("Received a complete message. Buffer length: {}", buffer.length());

                        if (displayScreen == null) {
                            displayScreen = new RemoteViewer(socket);
                        }

                        displayScreen.draw(buffer.getBuffer(0, expectedLen));
                        buffer = Buffer.buffer().appendBuffer(buffer.getBuffer(expectedLen, buffer.length()));
                        if (buffer.length() > 0) {
                            expectedLen = buffer.getInt(0);
                        } else {
                            clearBuffer();
                            expectedLen = 0;
                        }
                        LOGGER.info("Processed message. New expected length: {}", expectedLen);
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

    private void clearBuffer() {
        System.out.println("Buffer cleared");
        buffer = Buffer.buffer();
    }
}
