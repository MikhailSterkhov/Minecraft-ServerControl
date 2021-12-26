package org.stonlexx.servercontrol.connection;

import lombok.Getter;
import lombok.Setter;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.stonlexx.servercontrol.api.ServiceManager;
import org.stonlexx.servercontrol.api.command.CommandManager;
import org.stonlexx.servercontrol.api.event.EventManager;
import org.stonlexx.servercontrol.api.player.PlayerManager;
import org.stonlexx.servercontrol.api.scheduler.SchedulerManager;
import org.stonlexx.servercontrol.api.server.ServerManager;
import org.stonlexx.servercontrol.connection.command.SimpleCommandManager;
import org.stonlexx.servercontrol.connection.event.SimpleEventManager;
import org.stonlexx.servercontrol.connection.player.SimplePlayerManager;
import org.stonlexx.servercontrol.connection.scheduler.SimpleSchedulerManager;
import org.stonlexx.servercontrol.connection.server.SimpleServerManager;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class SimpleServiceManager
        implements ServiceManager {

    private final ServerManager serverManager           = new SimpleServerManager();
    private final PlayerManager playerManager           = new SimplePlayerManager();
    private final CommandManager commandManager         = new SimpleCommandManager();
    private final SchedulerManager schedulerManager     = new SimpleSchedulerManager();
    private final EventManager eventManager             = new SimpleEventManager();

    @Setter
    private SimpleTerminalConsole terminal;

    private final Path templatesDirectory               = Paths.get("servers");
    private final Path modulesDirectory                 = Paths.get("modules");
    private final Path loggingDirectory                 = Paths.get("logs");
    private final Path runningDirectory                 = Paths.get("run");
    private final Path globalFilesDirectory             = Paths.get("servers", "global");

}
