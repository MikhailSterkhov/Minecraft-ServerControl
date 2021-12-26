package org.stonlexx.servercontrol.api.scheduler;

import java.util.concurrent.TimeUnit;

public interface SchedulerManager {


    /**
     * Запустить асинхронный поток
     *
     * @param command - команда потока
     */
    void runAsync(Runnable command);

    /**
     * Отменить и закрыть поток шедулера
     * по его ID
     *
     * @param schedulerId - ID шедулера
     */
    void cancelScheduler(String schedulerId);

    /**
     * Воспроизвести команду Runnable через
     * определенное количество времени
     *
     * @param schedulerId - ID шедулера
     * @param command - команда
     * @param delay - время
     * @param timeUnit - единица времени
     */
    void runLater(String schedulerId, Runnable command, long delay, TimeUnit timeUnit);

    /**
     * Воспроизвести команду Runnable через
     * определенное количество времени циклично
     *
     * @param schedulerId - ID шедулера
     * @param command - команда
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    void runTimer(String schedulerId, Runnable command, long delay, long period, TimeUnit timeUnit);


}
