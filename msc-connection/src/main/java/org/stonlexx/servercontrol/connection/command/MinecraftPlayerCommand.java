package org.stonlexx.servercontrol.connection.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.stonlexx.servercontrol.api.chat.ChatColor;
import org.stonlexx.servercontrol.api.command.BasePlayerCommand;
import org.stonlexx.servercontrol.api.command.CommandSender;
import org.stonlexx.servercontrol.api.player.BasePlayer;

public abstract class MinecraftPlayerCommand
        extends MinecraftCommand
        implements BasePlayerCommand {

    @Setter
    @Getter
    private String noPlayerMessage
            = (ChatColor.RED + "This command only for players!");


    public MinecraftPlayerCommand(@NonNull String commandName, @NonNull String... commandAliases) {
        super(commandName, commandAliases);
    }

    @Override
    public void onExecute(@NonNull CommandSender commandSender, @NonNull String[] commandArgs) {
        if (!(commandSender instanceof BasePlayer)) {

            commandSender.sendMessage(getNoPlayerMessage());
            return;
        }

        onExecute((BasePlayer) commandSender, commandArgs);
    }

}
