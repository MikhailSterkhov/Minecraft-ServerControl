package org.stonlexx.servercontrol.connection.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.BaseCommand;
import org.stonlexx.servercontrol.api.command.CommandManager;
import org.stonlexx.servercontrol.api.command.CommandResponseSession;
import org.stonlexx.servercontrol.api.command.CommandSender;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Log4j2
public final class SimpleCommandManager implements CommandManager {

    private final Map<String, BaseCommand> baseCommands = new HashMap<>();

    @Getter
    private CommandResponseSession currentResponseSession;


    @Override
    public void openResponseSession(BiConsumer<CommandResponseSession, String> responseConsumer) {
        CommandResponseSession commandResponseSession = new CommandResponseSession() {

            @Override
            public void onCallback(@NonNull String terminalLine) {
                if (responseConsumer == null) {
                    return;
                }

                responseConsumer.accept(this, terminalLine);
            }


            @Override
            public void openSession() {
                SimpleCommandManager.this.currentResponseSession = this;
            }

            @Override
            public void closeSession() {
                SimpleCommandManager.this.currentResponseSession = null;
            }
        };

        commandResponseSession.openSession();
    }

    @Override
    public void registerCommand(@NonNull BaseCommand baseCommand) {
        baseCommands.put(baseCommand.getCommandName().toLowerCase(), baseCommand);

        for (String commandAlias : baseCommand.getCommandAliases()) {
            baseCommands.put(commandAlias.toLowerCase(), baseCommand);
        }
    }

    @Override
    public BaseCommand getCommand(@NonNull String commandLine) {
        return baseCommands.get(commandLine.toLowerCase());
    }

    @Override
    public boolean dispatchCommand(@NonNull CommandSender commandSender, @NonNull String commandLine) {
        if (!commandLine.startsWith(String.valueOf(COMMAND_CHAR))) {
            commandLine = (COMMAND_CHAR + commandLine);
        }

        String[] commandLineSplit = commandLine.substring(1).split("\\s+");

        String commandLabel = commandLineSplit[0].toLowerCase();
        String[] commandArgs = Arrays.copyOfRange(commandLineSplit, 1, commandLineSplit.length);

        BaseCommand baseCommand = getCommand(commandLabel);

        if (baseCommand == null) {
            return false;
        }

        log.info("[Command] " + commandSender.getName() + " dispatched command " + commandLine);
        baseCommand.onExecute(commandSender, commandArgs);

        return true;
    }

    @Override
    public Collection<BaseCommand> getRegisteredCommands() {
        return new HashSet<>(baseCommands.values());
    }

}
