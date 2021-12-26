package org.stonlexx.servercontrol.connection.command.impl;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.ServiceManager;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.CommandSender;
import org.stonlexx.servercontrol.api.server.type.ConnectedMinecraftServer;
import org.stonlexx.servercontrol.api.utility.DateUtil;
import org.stonlexx.servercontrol.api.utility.NumberUtil;
import org.stonlexx.servercontrol.connection.command.MinecraftCommand;
import org.stonlexx.servercontrol.connection.command.MinecraftMegaCommand;
import org.stonlexx.servercontrol.connection.command.MinecraftPlayerCommand;

public class StatisticCommand extends MinecraftCommand {

    public StatisticCommand() {
        super("msc-stats", "msc-stat", "msc-statistic", "mscstat", "mscstats", "mscstatistic");
    }

    @Override
    public void onExecute(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        MinecraftServerControlApi minecraftServerControlApi = MinecraftServerControlApi.getInstance();
        ServiceManager serviceManager = minecraftServerControlApi.getServiceManager();


        commandSender.sendMessage(ChatColor.YELLOW + "MSC :: System statistic:");
        commandSender.sendMessage(ChatColor.YELLOW + " [>] System running on " + DateUtil.formatPattern(DateUtil.DEFAULT_DATETIME_PATTERN));

        commandSender.sendMessage(ChatColor.YELLOW + " [>] Servers:");
        commandSender.sendMessage(ChatColor.WHITE + "  - §eTemplates loaded: §f" + formatNumber(serviceManager.getServerManager().getTemplateServers().size()));
        commandSender.sendMessage(ChatColor.WHITE + "  - §eServers loaded: §f" + formatNumber(serviceManager.getServerManager().getMinecraftServers().size()));
        commandSender.sendMessage(ChatColor.GREEN + "   - §eRunning: §f" + formatNumber((int) serviceManager.getServerManager().getMinecraftServers().stream().filter(ConnectedMinecraftServer::isRunning).count()));
        commandSender.sendMessage(ChatColor.GREEN + "   - §eInactive: §f" + formatNumber((int) serviceManager.getServerManager().getMinecraftServers().stream().filter(ConnectedMinecraftServer::isInactive).count()));

        commandSender.sendMessage(ChatColor.YELLOW + " [>] Commands:");
        commandSender.sendMessage(ChatColor.WHITE + "  - §eRegistered all commands: §f" + formatNumber(serviceManager.getCommandManager().getRegisteredCommands().size()));
        commandSender.sendMessage(ChatColor.GREEN + "   - §eMega commands: §f" + formatNumber((int) serviceManager.getCommandManager().getRegisteredCommands().stream().filter(command -> command.getClass().isAssignableFrom(MinecraftMegaCommand.class)).count()));
        commandSender.sendMessage(ChatColor.GREEN + "   - §eMinecraft commands: §f" + formatNumber((int) serviceManager.getCommandManager().getRegisteredCommands().stream().filter(command -> command.getClass().isAssignableFrom(MinecraftCommand.class)).count()));
        commandSender.sendMessage(ChatColor.GREEN + "   - §ePlayer commands: §f" + formatNumber((int) serviceManager.getCommandManager().getRegisteredCommands().stream().filter(command -> command.getClass().isAssignableFrom(MinecraftPlayerCommand.class)).count()));
        commandSender.sendMessage(ChatColor.GREEN + "   - §eAnother commands: §f" + formatNumber((int) serviceManager.getCommandManager().getRegisteredCommands().stream().filter(command -> !command.getClass().isAssignableFrom(MinecraftCommand.class)).count()));

        commandSender.sendMessage(ChatColor.YELLOW + " [>] Protocol:");
        commandSender.sendMessage(ChatColor.WHITE + "  - §ePackets sending: §f0");
        commandSender.sendMessage(ChatColor.WHITE + "  - §ePackets handled: §f0");
    }

    protected String formatNumber(int number) {
        return NumberUtil.spaced(number);
    }

}
