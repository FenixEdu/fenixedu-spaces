package org.fenixedu.spaces.core.service;

import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;

public interface NotificationService {

    public boolean notify(OccupationRequest request);

    public boolean sendEmail(String emails, String subject, String body);

}