package org.stonlexx.servercontrol.api.server.type;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.player.BasePlayer;
import org.stonlexx.servercontrol.api.server.MinecraftServer;
import org.stonlexx.servercontrol.protocol.UnsafeConnection;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

public interface ConnectedMinecraftServer extends MinecraftServer, UnsafeConnection {

    boolean isInactive();

    boolean isRunning();

    void setRunning(boolean running);


    Path getRunningDirectory();

    TemplateMinecraftServer getTemplate();


    Collection<BasePlayer> getOnlinePlayers();


    void onStart();

    void onShutdown();


    void setProperty(@NonNull String propertyKey, @NonNull Object value);

    String getProperty(@NonNull String propertyKey);

    String getProperty(@NonNull String propertyKey, String defaultValue);


    String getTotalMemory();

    ExecutorService getServerThread();


    default int getOnlineCount() {
        return getOnlinePlayers().size();
    }
}
