package org.stonlexx.servercontrol.api.command;

import lombok.NonNull;

public interface BaseCommand {

    /**
     * Главный алиас команды и ее название.
     */
    String getCommandName();

    /**
     * Алиасы команды.
     */
    String[] getCommandAliases();


    /**
     * Действия выполнения команды от имени любого
     * ее возможного отправителя
     *
     * @param commandSender - отправитель команды
     * @param commandArgs   - аргументы команды
     */
    void onExecute(@NonNull CommandSender commandSender, @NonNull String[] commandArgs);
}
