package org.stonlexx.servercontrol.api.command;

import lombok.NonNull;

/**
 * Обработчик переадресации команд от
 * имени консоли.
 */
public interface CommandResponseSession {

    /**
     * Обработка переадресованной команды
     * от консоли.
     *
     * @param terminalLine - командная строка
     */
    void onCallback(@NonNull String terminalLine);


    /**
     * Открыть сессию и начать переадресацию
     * внуренних команд о консоли.
     */
    void openSession();

    /**
     * Закрыть сессию переадресации, восстановив
     * работоспособность всех внутренних команд
     * для консоли.
     */
    void closeSession();
}
