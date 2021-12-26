package org.stonlexx.servercontrol.connection.event;

import lombok.Getter;
import lombok.NonNull;
import org.stonlexx.servercontrol.api.event.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class SimpleEventManager implements EventManager {

    @Getter
    private final Map<Listener, SimpleEventBus> listenerHandlers = new HashMap<>();

    @Override
    public void registerListener(@NonNull Listener listener) {
        if (listenerHandlers.containsKey(listener)) {
            return;
        }

        listenerHandlers.put(listener, new SimpleEventBus(listener));
        registerEvents(listener);
    }

    @Override
    public EventBus getEventsBus(@NonNull Listener listener) {
        return listenerHandlers.get(listener);
    }

    @Override
    public void unregisterListener(@NonNull Listener listener) {
        listenerHandlers.remove(listener);
    }

    @Override
    public void unregisterEvent(@NonNull Listener listener, @NonNull Class<? extends Event> eventClass) {
        EventBus eventBus = getEventsBus(listener);

        if (eventBus != null) {
            eventBus.removeEvent(eventClass);
        }
    }

    @Override
    public void unregisterEvent(@NonNull Class<? extends Event> eventClass) {
        for (Listener listener : listenerHandlers.keySet()) {
            EventBus eventBus = getEventsBus(listener);

            if (eventBus != null) {
                eventBus.removeEvent(eventClass);
            }
        }
    }

    @Override
    public void registerEvents(@NonNull Listener listener) {
        EventBus eventBus = getEventsBus(listener);

        Arrays.asList(listener.getClass().getMethods()).forEach(method -> {
            if (method.getDeclaredAnnotation(EventHandler.class) == null || method.getParameterCount() != 1) {
                return;
            }

            Class<?> eventClass = method.getParameterTypes()[0];

            if (eventClass.getSuperclass().isAssignableFrom(Event.class) || eventClass.getSuperclass().equals(Event.class)) {
                eventBus.addEventMethod((Class<? extends Event>) eventClass, method);
            }
        });
    }

    @Override
    public void callEvent(Event event) {

        for (SimpleEventBus listenerHandler : listenerHandlers.values()) {
            listenerHandler.fireEvent(event);
        }
    }

}
