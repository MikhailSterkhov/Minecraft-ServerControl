package org.stonlexx.servercontrol.api;

import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.stonlexx.servercontrol.api.command.CommandManager;
import org.stonlexx.servercontrol.api.event.EventManager;
import org.stonlexx.servercontrol.api.player.PlayerManager;
import org.stonlexx.servercontrol.api.scheduler.SchedulerManager;
import org.stonlexx.servercontrol.api.server.ServerManager;

import java.nio.file.Path;

public interface ServiceManager {

// ======================================= // MSC MANAGERS // ======================================= //

    ServerManager getServerManager();

    PlayerManager getPlayerManager();

    CommandManager getCommandManager();

    SchedulerManager getSchedulerManager();

    EventManager getEventManager();

// ====================================== // MSC DIRECTORIES // ===================================== //

    Path getTemplatesDirectory();

    Path getModulesDirectory();

    Path getLoggingDirectory();

    Path getRunningDirectory();

    Path getGlobalFilesDirectory();


// =================================== // LIBRARY net.minecrell // ================================== //

    SimpleTerminalConsole getTerminal();

}
