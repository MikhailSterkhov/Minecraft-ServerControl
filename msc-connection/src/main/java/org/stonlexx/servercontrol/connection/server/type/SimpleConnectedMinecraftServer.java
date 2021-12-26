package org.stonlexx.servercontrol.connection.server.type;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.player.BasePlayer;
import org.stonlexx.servercontrol.api.server.MinecraftServerType;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.server.type.TemplateMinecraftServer;
import org.stonlexx.servercontrol.api.utility.Directories;
import org.stonlexx.servercontrol.api.utility.FileUtil;
import org.stonlexx.servercontrol.protocol.ChannelWrapper;
import org.stonlexx.servercontrol.protocol.MinecraftPacket;
import org.stonlexx.servercontrol.protocol.UnsafeConnection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimpleConnectedMinecraftServer
        implements ConnectedMinecraftServer, UnsafeConnection {

    String name;
    String version;

    TemplateMinecraftServer template;
    Path templateDirectory;

    Properties properties;

    @Setter
    @NonFinal boolean running;

    @Setter
    @NonFinal ChannelWrapper channel;

    ExecutorService serverThread;


    public SimpleConnectedMinecraftServer(@NonNull String serverName,
                                          @NonNull String version,

                                          @NonNull TemplateMinecraftServer templateMinecraftServer,

                                          @NonNull Properties properties,
                                          @NonNull Path serverDirectory) {
        this.name = serverName;
        this.version = version;

        this.properties = properties;

        this.templateDirectory = serverDirectory;
        this.template = templateMinecraftServer;

        this.serverThread = Executors.newFixedThreadPool(2);
    }

    @SneakyThrows
    @Override
    public void onStart() {
        setRunning(true);
    }

    @Override
    public void onShutdown() {
        setRunning(false);

        Properties properties = getProperties();
        FileUtil.read(templateDirectory.resolve("mccontrol.properties").toFile(), properties::load);

        if (properties.getProperty("unload.allow", "false").equals("true")) {
            Directories.clearDirectory(getRunningDirectory().toFile(), true);
        }
    }

    @Override
    public void setProperty(@NonNull String propertyKey, @NonNull Object value) {
        properties.setProperty(propertyKey, value.toString());

        FileUtil.output(getTemplateDirectory().resolve("mccontrol.properties").toFile(),
                fileOutputStream -> properties.store(fileOutputStream, null));
    }

    @Override
    public String getProperty(@NonNull String propertyKey) {
        return getProperty(propertyKey, null);
    }

    @Override
    public String getProperty(@NonNull String propertyKey, String defaultValue) {
        return properties.getProperty(propertyKey, defaultValue);
    }


    @Override
    public String getTotalMemory() {
        return getProperty("server.memory", "512M");
    }

    @Override
    public Collection<BasePlayer> getOnlinePlayers() {
        return MinecraftServerControlApi.getInstance().getServiceManager().getPlayerManager().getOnlinePlayers(
                player -> player.getConnectedServer() != null && player.getConnectedServer().equals(this));
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SimpleConnectedMinecraftServer)) {
            return false;
        }

        return ((SimpleConnectedMinecraftServer) object).getName().equalsIgnoreCase(name);
    }

    @Override
    public MinecraftServerType getServerType() {
        return template.getServerType();
    }

    @Override
    public boolean isInactive() {
        return !running;
    }

    @Override
    public Path getRunningDirectory() {
        return MinecraftServerControlApi.getInstance().getServiceManager().getRunningDirectory().resolve(name);
    }


    @Override
    public void sendPacket(MinecraftPacket<?> packet) {
        channel.write(packet);
    }

}
