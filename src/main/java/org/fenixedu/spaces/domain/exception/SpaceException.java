package org.fenixedu.spaces.domain.exception;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class SpaceException extends DomainException {

    private static final long serialVersionUID = -59199476461461293L;

    public SpaceException(Status status, String bundle, String key, String... args) {
        super(status, bundle, key, args);
    }

    public SpaceException(String key, String... args) {
        super("resources.FenixSpaceResources", key, args);
    }

    public SpaceException(Throwable cause, Status status, String bundle, String key, String... args) {
        super(cause, status, bundle, key, args);
    }

    public SpaceException(Throwable cause, String bundle, String key, String... args) {
        super(cause, bundle, key, args);
    }

}
