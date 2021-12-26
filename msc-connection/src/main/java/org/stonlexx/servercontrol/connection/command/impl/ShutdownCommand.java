package org.stonlexx.servercontrol.connection.command.impl;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.CommandSender;
import org.stonlexx.servercontrol.api.utility.ProcessExecutionUtil;
import org.stonlexx.servercontrol.connection.command.MinecraftCommand;

@Log4j2
public class ShutdownCommand
        extends MinecraftCommand {

    public ShutdownCommand() {
        super("msc-shutdown", "msc-stop", "mscstop");

        setOnlyConsole(true);
    }

    @Override
    public void onExecute(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        log.info("\n\n\n" +

                ChatColor.GREEN + "MSC :: Thanks for using MinecraftServerControl by stonlexx!\n" +
                ChatColor.GREEN + "MSC :: VK - §nhttps://vk.com/itzstonlex§r\n\n" +

                ChatColor.GREEN + "MSC :: Exit value - 0" +

                "\n\n");

        ProcessExecutionUtil.destroyAll();
        MinecraftServerControlApi.getInstance().onShutdown();
    }

}
