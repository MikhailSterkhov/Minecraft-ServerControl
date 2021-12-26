package org.stonlexx.servercontrol.api.utility.query;

public interface ResponseHandler<R, O> {

    R handleResponse(O o);
}
