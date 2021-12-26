package org.stonlexx.servercontrol.api.server;

import lombok.NonNull;

public interface OSExecutionServer {

    void runServer();

    void shutdownServer();

    void execute(@NonNull String jarFile);
}
