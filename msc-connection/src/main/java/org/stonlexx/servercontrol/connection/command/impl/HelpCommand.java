package org.stonlexx.servercontrol.connection.command.impl;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.CommandSender;
import org.stonlexx.servercontrol.connection.command.MinecraftCommand;

public class HelpCommand
        extends MinecraftCommand {

    public HelpCommand() {
        super("msc", "msc-help", "mschelp");
    }

    @Override
    public void onExecute(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        commandSender.sendMessage(ChatColor.YELLOW + "MSC :: Available commands list:");

        commandSender.sendMessage(ChatColor.YELLOW + " [>] Management of servers - /msc-server");
        commandSender.sendMessage(ChatColor.YELLOW + " [>] Check MSC statistics - /msc-stats");
        commandSender.sendMessage(ChatColor.YELLOW + " [>] Stopping the MSC - /msc-stop");
    }

}
