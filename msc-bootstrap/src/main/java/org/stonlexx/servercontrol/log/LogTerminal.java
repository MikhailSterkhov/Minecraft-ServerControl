package org.stonlexx.servercontrol.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.CommandManager;
import org.stonlexx.servercontrol.connection.command.ConsoleCommandSender;

@RequiredArgsConstructor
@Log4j2
public class LogTerminal extends SimpleTerminalConsole {

    private final MinecraftServerControlApi minecraftServerControlApi;


    @Override
    protected boolean isRunning() {
        return minecraftServerControlApi.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        CommandManager commandManager = minecraftServerControlApi.getServiceManager().getCommandManager();

        if (commandManager.getCurrentResponseSession() != null) {
            commandManager.getCurrentResponseSession().onCallback(command);
            return;
        }

        if (!commandManager.dispatchCommand(ConsoleCommandSender.INSTANCE, command)) {
            log.info(ChatColor.RED + "[!] Unknown command! :c");
        }
    }

    @Override
    protected void shutdown() {
        minecraftServerControlApi.onShutdown();
    }

}
