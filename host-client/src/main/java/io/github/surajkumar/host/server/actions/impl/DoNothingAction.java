package io.github.surajkumar.host.server.actions.impl;

import io.github.surajkumar.host.screen.WatcherPermissions;
import io.github.surajkumar.host.server.actions.Action;
import io.vertx.core.buffer.Buffer;

public class DoNothingAction implements Action {
    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        return null;
    }
}
