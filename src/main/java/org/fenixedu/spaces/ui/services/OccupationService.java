package org.fenixedu.spaces.ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.domain.occupation.config.ExplicitConfig;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;
import org.fenixedu.spaces.ui.OccupationRequestBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.collect.Ordering;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class OccupationService {

    private JsonParser jsonParser;

    public OccupationService() {
        jsonParser = new JsonParser();
    }

    @Atomic
    public OccupationRequest createRequest(OccupationRequestBean bean) {
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
    public void addComment(OccupationRequest request, String description, OccupationRequestState newState) {
        final OccupationRequestState oldState = request.getCurrentState();
        Boolean reOpenRequest = oldState != OccupationRequestState.OPEN && newState == OccupationRequestState.OPEN;
        Boolean resolveRequest = oldState != OccupationRequestState.RESOLVED && newState == OccupationRequestState.RESOLVED;
        addComment(request, description, reOpenRequest, resolveRequest);
    }

    private void addComment(OccupationRequest request, String description, Boolean reOpenRequest, Boolean resolveRequest) {
        final DateTime now = new DateTime();

        final User requestor = Authenticate.getUser();

        if (reOpenRequest) {
            request.createNewTeacherCommentAndOpenRequest(description, requestor, now);
        } else if (resolveRequest) {
            request.createNewEmployeeCommentAndCloseRequest(description, requestor, now);
        } else {
            request.createNewTeacherOrEmployeeComment(description, requestor, now);
        }
    }

    @Atomic
    public void openRequest(OccupationRequest request, User owner) {
        request.openRequestAndAssociateOwnerOnlyForEmployess(new DateTime(), owner);
    }

    @Atomic
    public void closeRequest(OccupationRequest request, User owner) {
        request.closeRequestAndAssociateOwnerOnlyForEmployees(new DateTime(), owner);
    }

    //TODO: Access Control
    public List<Space> searchFreeSpaces(List<Interval> intervals, User user) {
        final Set<Space> freeSpaces = new HashSet<>();
        for (Space space : Bennu.getInstance().getSpaceSet()) {
            if (space.isActive() && space.isFree(intervals)) {
                freeSpaces.add(space);
            }
        }
        return Ordering.from(new Comparator<Space>() {

            @Override
            public int compare(Space o1, Space o2) {
                return o1.getNameWithParents().compareTo(o2.getNameWithParents());
            }

        }).immutableSortedCopy(freeSpaces);
    }

    @Atomic
    public void createOccupation(String emails, String subject, String description, String selectedSpaces, String config,
            String events) {
        final Set<Space> selectedSpaceSet = selectSpaces(selectedSpaces);
        final List<Interval> intervals = selectEvents(events);
        final Occupation occupation =
                new Occupation(emails, subject, description, new ExplicitConfig(jsonParser.parse(config), intervals));
        for (Space space : selectedSpaceSet) {
            occupation.addSpace(space);
        }
    }

    private DateTime selectDate(JsonElement jsonElement, String memberName) {
        final String timestamp = jsonElement.getAsJsonObject().get(memberName).getAsString();
        return new DateTime(Long.parseLong(timestamp) * 1000L);
    }

    private List<Interval> selectEvents(String events) {
        final List<Interval> intervals = new ArrayList<>();
        final JsonArray eventsJson = jsonParser.parse(events).getAsJsonArray();
        for (JsonElement eventElement : eventsJson) {
            final DateTime start = selectDate(eventElement, "start");
            final DateTime end = selectDate(eventElement, "end");
            intervals.add(new Interval(start, end));
        }
        return intervals;
    }

    private Set<Space> selectSpaces(String selectedSpaces) {
        final Set<Space> selectedSpaceSet = new HashSet<>();
        final JsonArray spacesJson = jsonParser.parse(selectedSpaces).getAsJsonArray();
        for (JsonElement spaceJson : spacesJson) {
            String spaceId = spaceJson.getAsString();
            final Space space = FenixFramework.getDomainObject(spaceId);
            if (FenixFramework.isDomainObjectValid(space)) {
                selectedSpaceSet.add(space);
            }
        }
        return selectedSpaceSet;
    }
}
