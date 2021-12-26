package org.stonlexx.servercontrol.api.command;

import lombok.NonNull;
import org.stonlexx.servercontrol.api.player.BasePlayer;

public interface BasePlayerCommand
        extends BaseCommand {

    /**
     * Действия выполнения команды от имени игрока.
     * <p>
     * Данный метод используется в {@link BaseCommand#onExecute(CommandSender, String[])},
     * реализуя {@link CommandSender} как {@link BasePlayer}.
     * <p>
     * Важно то, что данная команда не будет работать от
     * имени консоли.
     *
     * @param basePlayer  - игрок, который использует команду
     * @param commandArgs - аргументы команды
     */
    void onExecute
    (@NonNull BasePlayer basePlayer, @NonNull String[] commandArgs);
}
