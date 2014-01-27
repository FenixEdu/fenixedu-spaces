package net.sourceforge.fenixedu.domain.exception;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class SpaceDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public SpaceDomainException(String key, String... args) {
        super("resources.SpaceResources", key, args);
    }

}
