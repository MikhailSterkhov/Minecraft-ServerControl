package org.stonlexx.servercontrol.connection.command.impl;

import com.google.common.base.Joiner;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.CommandSender;
import org.stonlexx.servercontrol.api.server.MinecraftServerType;
import org.stonlexx.servercontrol.api.server.ServerManager;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.server.MinecraftServer;
import org.stonlexx.servercontrol.api.server.type.TemplateMinecraftServer;
import org.stonlexx.servercontrol.api.utility.Directories;
import org.stonlexx.servercontrol.api.utility.NumberUtil;
import org.stonlexx.servercontrol.api.utility.ValidateUtil;
import org.stonlexx.servercontrol.connection.MinecraftServerControlConnection;
import org.stonlexx.servercontrol.connection.command.MinecraftMegaCommand;

import java.nio.file.Files;

@Log4j2
public class ServerCommand
        extends MinecraftMegaCommand {

    public ServerCommand() {
        super(0, "msc-server", "mscserv", "mscs");

        setOnlyConsole(true);
    }

    @Override
    protected void onUsage(@NonNull CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.GOLD + "§lMSC§6 :: Available System commands:");

        commandSender.sendMessage(ChatColor.GOLD + "[>] Reset all servers: §l/msc-server reset§r");
        commandSender.sendMessage(ChatColor.GOLD + "[>] Get a servers list: §l/msc-server list <template>§r");
        commandSender.sendMessage(ChatColor.GOLD + "[>] Create a template server: §l/msc-server create template <name> <server type>§r");
        commandSender.sendMessage(ChatColor.GOLD + "[>] Create a minecraft server: §l/msc-server create server <template> <id(s)> [-v <version>] [-d (download jar file)]§r");
        commandSender.sendMessage(ChatColor.GOLD + "[>] Run the server: §l/msc-server run <server name>§r");
        commandSender.sendMessage(ChatColor.GOLD + "[>] Update property of the server: §l/msc-server property <server name> set <key> <value>§r");
        commandSender.sendMessage(ChatColor.GOLD + "[>] Copy the server: §l/msc-server copy <from server> <to server>§r");
    }

    @CommandArgument
    protected void create(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        if (commandArgs.length == 0) {
            commandSender.sendMessage(ChatColor.GOLD + "MSC :: /msc-server create template <name> <server type>");
            commandSender.sendMessage(ChatColor.GOLD + "MSC :: /msc-server create server <template> <id(s)> [-v <version>] [-d (download jar file)]");
            return;
        }

        switch (commandArgs[0].toLowerCase()) {

            case "template": {
                if (commandArgs.length < 3) {
                    commandSender.sendMessage(ChatColor.RED + "MSC :: Usage - /msc-server create shape <name> <type>");
                    break;
                }

                String templateServer = commandArgs[1];

                if (serverManager.getTemplateServer(templateServer) != null) {
                    commandSender.sendMessage(ChatColor.RED + "MSC :: Template §l" + templateServer + " §calready exists!");

                    break;
                }

                MinecraftServerType minecraftServerType;

                if (ValidateUtil.isNumber(commandArgs[2])) {
                    int shapeTypeLevel = Integer.parseInt(commandArgs[2]);

                    minecraftServerType = MinecraftServerType.getTypeByLevel(shapeTypeLevel);
                    if (minecraftServerType == null) {

                        commandSender.sendMessage(ChatColor.RED + "MSC :: Incorrect type of the server core");
                        commandSender.sendMessage(ChatColor.RED + " [>] Available: §l" + Joiner.on(", ").join(MinecraftServerType.MINECRAFT_SERVER_VALUES));
                        break;
                    }

                }

                minecraftServerType = MinecraftServerType.getTypeByName(commandArgs[2]);
                if (minecraftServerType == null) {

                    commandSender.sendMessage(ChatColor.RED + "MSC :: Incorrect type of the server core");
                    commandSender.sendMessage(ChatColor.RED + " [>] Available: §l" + Joiner.on(", ").join(MinecraftServerType.MINECRAFT_SERVER_VALUES));
                    break;
                }

                commandSender.sendMessage("MSC :: Download template server...");
                TemplateMinecraftServer templateMinecraftServer = serverManager.createTemplateServer(templateServer, minecraftServerType);

                commandSender.sendMessage(ChatColor.GREEN + "MSC :: Template §l" + templateMinecraftServer.getName() + "§a was success created!");
                break;
            }

            case "server": {
                if (commandArgs.length < 3) {
                    commandSender.sendMessage(ChatColor.RED + "MSC :: Usage - /msc-server create server <template> <id(s)> [-v <version>] [-d (download jar file)]");
                    break;
                }

                String shapeServerName = commandArgs[1];
                TemplateMinecraftServer templateMinecraftServer = serverManager.getTemplateServer(shapeServerName);

                if (templateMinecraftServer == null) {
                    commandSender.sendMessage(ChatColor.RED + "MSC :: Template §l" + shapeServerName + " §cis`nt exists!");

                    break;
                }

                String serverIndex = (commandArgs[2]);
                if (MinecraftServer.MULTI_INDEX_PATTERN.matcher(serverIndex).matches()) {

                    int beginIndex = Integer.parseInt(serverIndex.substring(1, serverIndex.indexOf("-")));
                    int endIndex = Integer.parseInt(serverIndex.substring(serverIndex.indexOf("-") + 1, serverIndex.length() - 1));

                    if (beginIndex > endIndex) {
                        commandSender.sendMessage(ChatColor.RED + "MSC :: Incorrect server index: §lbegin-index(" + beginIndex + ") must be < end-index(" + endIndex + ")");
                        break;
                    }

                    for (int serverIndexInt : NumberUtil.toManyArray(beginIndex, endIndex + 1)) {
                        createServer(commandSender, templateMinecraftServer, String.valueOf(serverIndexInt), commandArgs);
                    }

                    break;
                }

                if (!ValidateUtil.isNumber(serverIndex)) {
                    commandSender.sendMessage(ChatColor.RED + "MSC :: Incorrect server index: §l" + serverIndex);
                    break;
                }

                createServer(commandSender, templateMinecraftServer, serverIndex, commandArgs);
                break;
            }

            default:
                commandSender.sendMessage(ChatColor.RED + "MSC :: Incorrect server type: " + commandArgs[0]);
                commandSender.sendMessage(ChatColor.RED + " [>] Available: §lSHAPE, SUB");
        }
    }

    @CommandArgument
    protected void reset(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        MinecraftServerControlConnection.getInstance().resetRunningServers();

        commandSender.sendMessage(ChatColor.RED + "MSC :: All servers has been reset!");
    }

    @CommandArgument
    protected void list(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        if (commandArgs.length == 0) {

            commandSender.sendMessage(ChatColor.RED + "MSC :: Usage - /msc-server list <template>");
            commandSender.sendMessage(ChatColor.RED + "MSC :: Available templates: (" + serverManager.getTemplateServers().size() + ")");

            for (TemplateMinecraftServer template : serverManager.getTemplateServers()) {
                commandSender.sendMessage(ChatColor.YELLOW + " [>] Template " + template.getName() + ": <-> (" + template.getConnectedServers().size() + " Servers, " + template.getActiveServers().size() + " Actived)");
            }

            return;
        }

        String shapeServerName = commandArgs[0];
        TemplateMinecraftServer templateMinecraftServer = serverManager.getTemplateServer(shapeServerName);

        if (templateMinecraftServer == null) {
            commandSender.sendMessage(ChatColor.RED + "MSC :: Template " + shapeServerName + " is`nt exists!");

            return;
        }

        commandSender.sendMessage(ChatColor.YELLOW + "[>] Template " + templateMinecraftServer.getName() + ": <-> (" + templateMinecraftServer.getConnectedServers().size() + " Servers, " + templateMinecraftServer.getActiveServers().size() + " Actived)");

        for (ConnectedMinecraftServer connectedMinecraftServer : templateMinecraftServer.getConnectedServers()) {

            String onlineMode = (connectedMinecraftServer.isRunning() ? ChatColor.GREEN + "[RUNNING]" : ChatColor.RED + "[INACTIVE]");
            commandSender.sendMessage(ChatColor.YELLOW + "    " + connectedMinecraftServer.getName() + " (" + connectedMinecraftServer.getServerType().name().toLowerCase() + "): <-> (" + connectedMinecraftServer.getOnlineCount() + " Online players) " + onlineMode);
        }
    }

    @CommandArgument
    protected void run(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        if (commandArgs.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "MSC :: Usage - /msc-server run <server name>");
            return;
        }

        ConnectedMinecraftServer connectedMinecraftServer = serverManager.getConnectedServer(commandArgs[0]);

        if (connectedMinecraftServer == null) {
            commandSender.sendMessage(ChatColor.RED + "MSC :: Server '" + commandArgs[0] + "' is`nt exists!");
            return;
        }

        if (connectedMinecraftServer.isRunning()) {
            log.error(ChatColor.RED + "MSC :: Server " + connectedMinecraftServer.getName() + " already started!");
            return;
        }

        MinecraftServerControlConnection.getInstance().getServerExecution(connectedMinecraftServer).runServer();
        log.warn(ChatColor.GREEN + "MSC :: Start running server " + connectedMinecraftServer.getName() + "...");
    }

    @CommandArgument(aliases = "settings")
    protected void property(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        if (commandArgs.length < 4) {
            commandSender.sendMessage(ChatColor.RED + "MSC :: Usage - /msc-server property <server name> set <key> <value>");
            return;
        }

        ConnectedMinecraftServer connectedMinecraftServer = serverManager.getConnectedServer(commandArgs[0]);

        if (connectedMinecraftServer == null) {
            commandSender.sendMessage(ChatColor.RED + "MSC :: Server '" + commandArgs[0] + "' is`nt exists!");
            return;
        }

        String propertyKey = (commandArgs[2]);
        String propertyValue = (commandArgs[3]);

        connectedMinecraftServer.setProperty(propertyKey, propertyValue);
        commandSender.sendMessage(ChatColor.GREEN + "MSC :: Property of the server " + connectedMinecraftServer.getName() + " has been updated!");
    }

    private void createServer(@NonNull CommandSender commandSender,

                              @NonNull TemplateMinecraftServer templateMinecraftServer,
                              @NonNull String serverIndex,

                              @NonNull String[] args) {

        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        String serverName = templateMinecraftServer.getName().concat("-").concat(serverIndex);

        if (serverManager.getConnectedServer(serverName) != null) {
            commandSender.sendMessage(ChatColor.RED + "[!] Server " + serverName + " already exists!");
            return;
        }

        commandSender.sendMessage("MSC :: Download " + serverName + " server...");


        String joinArgs = Joiner.on(" ").join(args);

        boolean downloadJar = (joinArgs.contains("-d"));
        String version = (joinArgs.contains("-v") ? joinArgs.substring(joinArgs.indexOf("-v") + 3, joinArgs.indexOf(" ", joinArgs.indexOf("-v ") + 3)) : "1.12.2");

        if (!MinecraftServer.MINECRAFT_VERSION_PATTERN.matcher(version).matches()) {
            commandSender.sendMessage(ChatColor.RED + "[!] Incorrect core version: §l" + version);

            return;
        }

        ConnectedMinecraftServer connectedMinecraftServer
                = serverManager.createConnectedServer(templateMinecraftServer, serverIndex, version, downloadJar);

        if (connectedMinecraftServer != null) {
            commandSender.sendMessage(ChatColor.GREEN + "MSC :: Server " + connectedMinecraftServer.getName() + " was success created!");
        }
    }

    @CommandArgument(aliases = {"move", "copied", "paste"})
    protected void copy(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        if (commandArgs.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "[!] Usage - /msc-server copy <from server> <to server>");
            return;
        }

        ConnectedMinecraftServer serverFrom = serverManager.getConnectedServer(commandArgs[0]);

        if (serverFrom == null) {
            commandSender.sendMessage(ChatColor.RED + "[!] Server " + commandArgs[0] + " is`nt exists!");
            return;
        }

        copyServer(commandSender, serverFrom, commandArgs[1]);
    }

    private void copyServer(@NonNull CommandSender commandSender,
                            @NonNull ConnectedMinecraftServer serverFrom, @NonNull String serverNameTo) {

        commandSender.sendMessage(ChatColor.YELLOW + "MSC :: Start copy " + serverFrom.getName() + " to " + serverNameTo + "...");

        ServerManager serverManager
                = MinecraftServerControlApi.getInstance().getServiceManager().getServerManager();

        // check server template
        String templateNameTo = serverNameTo.split("-", 2)[0];
        TemplateMinecraftServer templateTo = serverManager.getTemplateServer(templateNameTo);

        if (templateTo == null) {
            commandSender.sendMessage(ChatColor.RED + "MSC :: Template " + templateNameTo + " is`nt exists!");
            return;
        }

        // create new server
        ConnectedMinecraftServer serverTo = serverManager.getConnectedServer(serverNameTo);

        if (serverTo == null) {
            serverTo = serverManager.createConnectedServer(templateTo, serverNameTo.split("-", 2)[1], serverFrom.getProperty("server.version"), false);

            Directories.clearDirectory(serverTo.getTemplateDirectory().toFile(), false);
        }

        // coping of the server
        Directories.copyDirectory(serverFrom.getTemplateDirectory(), serverTo.getTemplateDirectory());

        if (Files.exists(serverFrom.getRunningDirectory())) {
            Directories.copyDirectory(serverFrom.getRunningDirectory(), serverTo.getRunningDirectory());
        }

        // update properties
        serverTo.setProperty("server.name", serverTo.getName());


        // log
        commandSender.sendMessage(ChatColor.GREEN + "MSC :: Server " + serverTo.getName() + " was success copied from " + serverFrom.getName() + "!");
    }

}
