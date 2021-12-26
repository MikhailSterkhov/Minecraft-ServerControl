package org.stonlexx.servercontrol.api.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.stonlexx.servercontrol.api.MinecraftServerControlApi;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class TaskScheduler implements Runnable {

    @Getter
    private final String identifier;

    public TaskScheduler() {
        this(RandomStringUtils.randomAlphanumeric(32));
    }


    /**
     * Отмена и закрытие потока
     */
    public void cancel() {
        MinecraftServerControlApi.getInstance().getServiceManager().getSchedulerManager().cancelScheduler(identifier);
    }

    /**
     * Запустить асинхронный поток
     */
    public void runAsync() {
        MinecraftServerControlApi.getInstance().getServiceManager().getSchedulerManager().runAsync(this);
    }

    /**
     * Запустить поток через определенное
     * количество времени
     *
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(long delay, TimeUnit timeUnit) {
        MinecraftServerControlApi.getInstance().getServiceManager().getSchedulerManager().runLater(identifier, this, delay, timeUnit);
    }

    /**
     * Запустить цикличный поток через
     * определенное количество времени
     *
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(long delay, long period, TimeUnit timeUnit) {
        MinecraftServerControlApi.getInstance().getServiceManager().getSchedulerManager().runTimer(identifier, this, delay, period, timeUnit);
    }

}
