package org.stonlexx.servercontrol.api.utility.query;

public interface ThrowableResponseHandler<R, O, T extends Throwable> {

    R handleResponse(O o) throws T;
}
