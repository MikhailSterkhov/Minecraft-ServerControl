package org.stonlexx.servercontrol.connection.server.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.stonlexx.servercontrol.api.server.MinecraftServerType;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.server.type.TemplateMinecraftServer;
import org.stonlexx.servercontrol.api.utility.FileUtil;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SimpleTemplateMinecraftServer
        implements TemplateMinecraftServer {

    String name;
    Path templateDirectory;

    Properties properties;
    MinecraftServerType serverType;

    Map<String, ConnectedMinecraftServer> connectedServers = new HashMap<>();


    @Override
    public int getStartPort() {
        return Integer.parseInt(getProperty("start_port", "-1"));
    }

    @Override
    public Collection<ConnectedMinecraftServer> getConnectedServers() {
        return connectedServers.values();
    }

    @Override
    public Collection<ConnectedMinecraftServer> getActiveServers() {
        return getConnectedServers().stream().filter(ConnectedMinecraftServer::isRunning).collect(Collectors.toSet());
    }

    @Override
    public ConnectedMinecraftServer getConnectedServer(@NonNull String serverName) {
        return connectedServers.get(serverName.toLowerCase());
    }

    @Override
    public void addConnectedServer(@NonNull ConnectedMinecraftServer connectedMinecraftServer) {
        connectedServers.put(connectedMinecraftServer.getName().toLowerCase(), connectedMinecraftServer);
    }

    @Override
    public void setProperty(@NonNull String propertyKey, @NonNull Object value) {
        properties.setProperty(propertyKey, value.toString());

        FileUtil.output(templateDirectory.resolve("template.properties").toFile(),
                fileOutputStream -> properties.store(fileOutputStream, null));
    }

    @Override
    public String getProperty(@NonNull String propertyKey) {
        Properties properties = getProperties();
        FileUtil.read(templateDirectory.resolve("template.properties").toFile(), properties::load);

        return getProperty(propertyKey, null);
    }

    @Override
    public String getProperty(@NonNull String propertyKey, String defaultValue) {
        return properties.getProperty(propertyKey, defaultValue);
    }

}
