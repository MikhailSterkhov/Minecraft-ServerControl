package org.stonlexx.servercontrol.api.event;

import lombok.NonNull;

import java.lang.reflect.Method;

public interface EventBus {

    void addEventMethod(Class<? extends Event> event, Method method);

    void removeEventMethod(Class<? extends Event> event, Method method);

    void removeEvent(Class<? extends Event> event);

    void fireEvent(@NonNull Event event);

}
