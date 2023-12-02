package io.github.surajkumar.server.actions;

import io.github.surajkumar.server.screen.WatcherPermissions;
import io.vertx.core.buffer.Buffer;

public interface Action {
    Buffer handle(Buffer request, WatcherPermissions permissions);
}
