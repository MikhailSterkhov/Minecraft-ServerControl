package org.stonlexx.servercontrol.api.event;

public class EventException extends RuntimeException {

    public EventException(String errorMessage, Object... elements) {
        super(String.format(errorMessage, elements));
    }

    public EventException() {
        super();
    }

}
