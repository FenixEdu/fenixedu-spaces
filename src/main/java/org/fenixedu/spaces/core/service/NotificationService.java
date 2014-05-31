package org.fenixedu.spaces.core.service;

import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;

public interface NotificationService {

    public boolean notify(OccupationRequest request, OccupationRequestState state);

    public boolean emails(String emails, String subject, String body);

}