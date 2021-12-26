package org.stonlexx.servercontrol.api.event;

import lombok.NonNull;

public interface EventManager {

    /**
     * Регистрация листенера с ивентами под
     * уникальным ID
     *
     * @param listener - листенер
     */
    void registerListener(@NonNull Listener listener);

    EventBus getEventsBus(@NonNull Listener listener);

    void unregisterListener(@NonNull Listener listener);

    void unregisterEvent(@NonNull Listener listener, @NonNull Class<? extends Event> eventClass);

    void unregisterEvent(@NonNull Class<? extends Event> eventClass);


    /**
     * Регистрация ивентов, их кеширование в мапу
     *
     * @param listener - листенер
     */
    void registerEvents(@NonNull Listener listener);

    /**
     * Вызывать ивент
     *
     * @param event - ивент
     */
    void callEvent(Event event);

}
