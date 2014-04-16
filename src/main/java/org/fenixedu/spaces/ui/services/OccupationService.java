package org.fenixedu.spaces.ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.requests.OccupationComment;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;
import org.fenixedu.spaces.domain.occupation.requests.OccupationStateInstant;
import org.fenixedu.spaces.ui.OccupationBean;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;

@Service
public class OccupationService {

    @Atomic
    public OccupationRequest createRequest(OccupationBean bean) {
        return new OccupationRequest(bean.getRequestor(), bean.getSubject(), bean.getCampus(), bean.getDescription());
    }

    public OccupationRequest search(Integer requestID) {
        return OccupationRequest.getRequestById(requestID);
    }

    public Set<Space> getAllCampus() {
        return Space.getAllCampus();
    }

    public Set<OccupationRequest> all() {
        return Bennu.getInstance().getOccupationRequestSet();
    }

    public Set<OccupationRequest> all(OccupationRequestState state, Space campus) {
        return OccupationRequest.getRequestsByTypeOrderByDate(state, campus);
    }

    public List<OccupationRequest> getRequestsToProcess(User user, Space campus) {
        final List<OccupationRequest> result = new ArrayList<OccupationRequest>();
        for (final OccupationRequest request : user.getOcuppationRequestsToProcessSet()) {
            if (!request.getCurrentState().equals(OccupationRequestState.RESOLVED)
                    && (request.getCampus() == null || request.getCampus().equals(campus))) {
                result.add(request);
            }
        }

        if (!result.isEmpty()) {
            Collections.sort(result, OccupationRequest.COMPARATOR_BY_INSTANT);
        }
        return result;
    }

    @Atomic
    public OccupationComment addComment(OccupationRequest request, String subject, String description,
            OccupationRequestState state) {
        final DateTime now = new DateTime();
        final OccupationComment comment = new OccupationComment(request, subject, description, Authenticate.getUser(), now);
        if (state != null) {
            request.addStateInstants(new OccupationStateInstant(request, state, now));
        }

        return comment;
    }

    public OccupationComment addComment(OccupationRequest request, String description, OccupationRequestState state) {
        return addComment(request, request.getCommentSubject(), description, state);
    }

}
