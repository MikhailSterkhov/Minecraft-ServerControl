package org.stonlexx.servercontrol.connection.event;

public class EventException extends RuntimeException {

    public EventException(String errorMessage, Object... elements) {
        super(String.format(errorMessage, elements));
    }

    public EventException() {
        super();
    }

}
