package io.github.surajkumar.host.server.actions;

import io.github.surajkumar.host.screen.WatcherPermissions;
import io.vertx.core.buffer.Buffer;

public interface Action {
    Buffer handle(Buffer request, WatcherPermissions permissions);
}
