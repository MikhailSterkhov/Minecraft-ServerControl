package org.stonlexx.servercontrol.connection.scheduler;

import org.stonlexx.servercontrol.api.scheduler.SchedulerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class SimpleSchedulerManager implements SchedulerManager {

    private final Map<String, ScheduledFuture<?>> schedulerMap = new HashMap<>();

    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    @Override
    public void runAsync(Runnable command) {
        scheduledExecutor.submit(command);
    }

    @Override
    public void cancelScheduler(String schedulerId) {
        ScheduledFuture<?> scheduledFuture = schedulerMap.get(schedulerId.toLowerCase());

        if ( scheduledFuture == null || scheduledFuture.isCancelled() ) {
            return;
        }

        scheduledFuture.cancel(true);
        schedulerMap.remove(schedulerId.toLowerCase());
    }

    @Override
    public void runLater(String schedulerId,
                         Runnable command, long delay, TimeUnit timeUnit) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(command, delay, timeUnit);

        schedulerMap.put(schedulerId.toLowerCase(), scheduledFuture);
    }

    @Override
    public void runTimer(String schedulerId,
                         Runnable command, long delay, long period, TimeUnit timeUnit) {

        ScheduledFuture<?> scheduledFuture
                = scheduledExecutor.scheduleAtFixedRate(command, delay, period, timeUnit);

        ScheduledFuture<?> previous = schedulerMap.put(schedulerId.toLowerCase(), scheduledFuture);
        if (previous != null) {
            previous.cancel(true);
        }
    }


}
