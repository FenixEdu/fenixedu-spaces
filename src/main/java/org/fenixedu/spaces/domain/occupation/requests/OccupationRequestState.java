package org.fenixedu.spaces.domain.occupation.requests;

public enum OccupationRequestState {

    NEW, OPEN, RESOLVED;

    public String getName() {
        return name();
    }
}
