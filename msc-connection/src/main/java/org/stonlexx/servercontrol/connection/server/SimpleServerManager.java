package org.stonlexx.servercontrol.connection.server;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.server.MinecraftServerType;
import org.stonlexx.servercontrol.api.server.ServerManager;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.server.type.TemplateMinecraftServer;
import org.stonlexx.servercontrol.api.utility.FileUtil;
import org.stonlexx.servercontrol.api.utility.HttpDownloadUtil;
import org.stonlexx.servercontrol.connection.server.type.SimpleConnectedMinecraftServer;
import org.stonlexx.servercontrol.connection.server.type.SimpleTemplateMinecraftServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SimpleServerManager implements ServerManager {

    Map<String, TemplateMinecraftServer> templateMinecraftServers = new HashMap<>();
    Map<String, ConnectedMinecraftServer> minecraftServers = new HashMap<>();


    @Override
    public void addConnectedServer(@NonNull ConnectedMinecraftServer connectedMinecraftServer) {
        minecraftServers.put(connectedMinecraftServer.getName().toLowerCase(), connectedMinecraftServer);
    }

    @Override
    public void addTemplateServer(@NonNull TemplateMinecraftServer templateMinecraftServer) {
        templateMinecraftServers.put(templateMinecraftServer.getName().toLowerCase(), templateMinecraftServer);

        for (ConnectedMinecraftServer connectedMinecraftServer : templateMinecraftServer.getConnectedServers()) {
            addConnectedServer(connectedMinecraftServer);
        }
    }


    @Override
    public Collection<ConnectedMinecraftServer> getMinecraftServers() {
        return minecraftServers.values();
    }

    @Override
    public Collection<TemplateMinecraftServer> getTemplateServers() {
        return templateMinecraftServers.values();
    }


    @Override
    public ConnectedMinecraftServer getConnectedServer(@NonNull String serverName) {
        return minecraftServers.get(serverName.toLowerCase());
    }

    @Override
    public TemplateMinecraftServer getTemplateServer(@NonNull String serverName) {
        return templateMinecraftServers.get(serverName.toLowerCase());
    }


    @SneakyThrows
    @Override
    public TemplateMinecraftServer createTemplateServer(@NonNull String serverIndex, @NonNull MinecraftServerType minecraftServerType) {

        // shape server directory
        Path serverDirectory = MinecraftServerControlApi.getInstance().getServiceManager()
                .getTemplatesDirectory().resolve(serverIndex);

        if (!Files.exists(serverDirectory)) {
            Files.createDirectory(serverDirectory);

            // shape server property file
            Path propertyPath       = serverDirectory.resolve("template.properties");
            Properties properties   = new Properties();

            if (!Files.exists(propertyPath)) {
                Files.createFile(propertyPath);
            }

            properties.load(new FileReader(propertyPath.toFile()));

            properties.setProperty("type", String.valueOf(minecraftServerType.getServerLevel()));
            properties.save(new FileOutputStream(propertyPath.toFile()), " Property configuration of the shape " + serverIndex);

            // initialize the shape server
            SimpleTemplateMinecraftServer simpleTemplateMinecraftServer = new SimpleTemplateMinecraftServer(serverIndex, serverDirectory, properties, minecraftServerType);
            addTemplateServer(simpleTemplateMinecraftServer);

            return (simpleTemplateMinecraftServer);
        }

        return getTemplateServer(serverIndex);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    @Override
    public ConnectedMinecraftServer createConnectedServer(@NonNull TemplateMinecraftServer templateMinecraftServer,
                                                          @NonNull String serverIndex,

                                                          String serverVersion,
                                                          boolean downloadJar) {

        String serverName = templateMinecraftServer.getName().concat("-").concat(serverIndex);

        // sub server directory
        Path serverDirectory = templateMinecraftServer.getTemplateDirectory().resolve(serverIndex);

        if (!Files.exists(serverDirectory)) {
            Files.createDirectory(serverDirectory);
            Files.createDirectory(serverDirectory.resolve("plugins"));

            // sub server property file
            Path propertyPath = serverDirectory.resolve("mccontrol.properties");
            Properties properties = new Properties();

            if (!Files.exists(propertyPath)) {
                Files.createFile(propertyPath);
            }

            FileUtil.input(propertyPath.toFile(), properties::load);

            properties.setProperty("server.name", serverName);
            properties.setProperty("server.memory", "512M");
            properties.setProperty("server.version", serverVersion);
            properties.setProperty("unload.allow", "false");

            FileUtil.output(propertyPath.toFile(), fileOutputStream ->
                    properties.save(fileOutputStream, " Property configuration of the sub server " + serverName));

            // eula file
            File eulaFile = serverDirectory.resolve("eula.txt").toFile();
            eulaFile.createNewFile();

            FileUtil.write(eulaFile, fileWriter -> fileWriter.write("eula=true"));

            // sub server jar core
            if (downloadJar) {
                boolean isDownloaded = HttpDownloadUtil.downloadMinecraftServer(
                        serverDirectory.resolve(templateMinecraftServer.getServerType().name().toLowerCase().concat("-").concat(serverVersion).concat(".jar")),
                        templateMinecraftServer.getServerType(), serverVersion);

                if (!isDownloaded) {
                    return getConnectedServer(serverName);
                }
            }

            // initialize the sub server
            ConnectedMinecraftServer connectedMinecraftServer = new SimpleConnectedMinecraftServer(serverName, serverVersion, templateMinecraftServer, properties, serverDirectory);
            addConnectedServer(connectedMinecraftServer);

            templateMinecraftServer.addConnectedServer(connectedMinecraftServer);
            return (connectedMinecraftServer);
        }

        return getConnectedServer(serverName);
    }

}
