package org.stonlexx.servercontrol.api.command;

import lombok.NonNull;

import java.util.Collection;
import java.util.function.BiConsumer;

public interface CommandManager {

    // Символ-префикс, который обозначает строку как внутреннюю команду.
    char COMMAND_CHAR = '/';


    /**
     * Открытие сессии, временно блокирующую консольные команды,
     * переадресовывая все в {@link CommandResponseSession}.
     *
     * @param responseConsumer - обработчик консольных команд.
     */
    void openResponseSession(BiConsumer<CommandResponseSession, String> responseConsumer);

    /**
     * Получить текущую сессию переадресации консольных
     * команд.
     */
    CommandResponseSession getCurrentResponseSession();


    /**
     * Зарегистрировать внутреннюю команду системы.
     *
     * @param baseCommand - реализация команды.
     */
    void registerCommand(@NonNull BaseCommand baseCommand);

    /**
     * Получить реализацию внутренней команды по
     * ее названию или алиасу.
     *
     * @param commandLine - название или алиас команды
     */
    BaseCommand getCommand(@NonNull String commandLine);


    /**
     * Выполнить команду от имени указанного отправителя.
     *
     * @param commandSender - отправитель команды.
     * @param commandLine   - командная строка.
     */
    boolean dispatchCommand(@NonNull CommandSender commandSender, @NonNull String commandLine);


    Collection<BaseCommand> getRegisteredCommands();
}
