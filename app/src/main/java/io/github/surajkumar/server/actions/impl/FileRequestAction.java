package io.github.surajkumar.server.actions.impl;

import io.github.surajkumar.server.screen.WatcherPermissions;
import io.github.surajkumar.server.actions.Action;
import io.vertx.core.buffer.Buffer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileRequestAction implements Action {

    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        if (permissions.isDownloadFiles()) {
            System.out.println("File Sending");
            String fileName = request.getString(0, request.length());
            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                try {
                    byte[] data = Files.readAllBytes(file.toPath());
                    return Buffer.buffer()
                            .appendInt(0) // opcode
                            .appendInt(file.getName().length())
                            .appendInt(data.length)
                            .appendString(file.getName())
                            .appendBytes(data);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    // TODO: Notify error
                }
            } else {
                // TODO: Send a no such file response
            }
            return null;
        }
        return null;
    }
}
