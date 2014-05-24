package org.fenixedu.spaces.ui.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.domain.occupation.config.ExplicitConfigWithSettings;
import org.fenixedu.spaces.domain.occupation.config.ExplicitConfigWithSettings.Frequency;
import org.fenixedu.spaces.domain.occupation.config.ExplicitConfigWithSettings.MonthlyType;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;
import org.fenixedu.spaces.ui.OccupationRequestBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class OccupationService {

    private JsonParser jsonParser;

    private DateTimeFormatter datetimeFormatter;

    @Autowired
    MessageSource messageSource;

    public OccupationService() {
        jsonParser = new JsonParser();
        datetimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");;
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

    public List<OccupationRequest> all() {
        return Bennu.getInstance().getOccupationRequestSet().stream().sorted(OccupationRequest.COMPARATOR_BY_INSTANT.reversed())
                .collect(Collectors.toList());
    }

    public List<OccupationRequest> all(OccupationRequestState state, Space campus) {
        return OccupationRequest.getRequestsByTypeOrderByDate(state, campus);
    }

    public List<OccupationRequest> getRequestsToProcess(User user, Space campus) {

        return user
                .getOcuppationRequestsToProcessSet()
                .stream()
                .filter(r -> !r.getCurrentState().equals(OccupationRequestState.RESOLVED)
                        && (r.getCampus() == null || r.getCampus().equals(campus)))
                .sorted(OccupationRequest.COMPARATOR_BY_INSTANT.reversed()).collect(Collectors.toList());
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

    public List<Space> searchFreeSpaces(List<Interval> intervals, User user) {
        return Space.getSpaces().filter(space -> space.isFree(intervals) && space.isOccupationMember(user))
                .sorted((o1, o2) -> o1.getPresentationName().compareTo(o2.getPresentationName())).collect(Collectors.toList());
    }

    @Atomic
    public void createOccupation(String emails, String subject, String description, String selectedSpaces, String config,
            String events) throws Exception {
        final Set<Space> selectedSpaceSet = selectSpaces(selectedSpaces);
        final List<Interval> intervals = selectEvents(events);
        final Occupation occupation = new Occupation(emails, subject, description, parseConfig(config, intervals));
        for (Space space : selectedSpaceSet) {
            if (!space.isFree(intervals)) {
                throw new Exception(messageSource.getMessage("error.occupations.rooms.is.not.free", new Object[0],
                        I18N.getLocale()));
            }
            occupation.addSpace(space);
        }
    }

    private OccupationConfig parseConfig(String config, List<Interval> intervals) {
        JsonObject json = jsonParser.parse(config).getAsJsonObject();

        DateTime start = DateTime.parse(json.get("start").getAsString(), datetimeFormatter);
        DateTime end = DateTime.parse(json.get("end").getAsString(), datetimeFormatter);
        String jsonFrequency = json.get("frequency").getAsString();
        Boolean allDay = json.get("isAllDay").getAsBoolean();
        Integer repeatsEvery = null;
        List<Integer> weekdays = null;
        MonthlyType monthlyType = null;
        Frequency frequency = null;

        switch (jsonFrequency) {
        case "n":
            frequency = Frequency.NEVER;
            break;
        case "d":
            frequency = Frequency.DAILY;
            repeatsEvery = json.get("repeatsevery").getAsInt();
            break;
        case "w":
            frequency = Frequency.WEEKLY;
            repeatsEvery = json.get("repeatsevery").getAsInt();
            weekdays = new ArrayList<>();
            for (JsonElement day : json.get("weekdays").getAsJsonArray()) {
                weekdays.add(day.getAsInt());
            }
            break;
        case "m":
            frequency = Frequency.MONTHLY;
            repeatsEvery = json.get("repeatsevery").getAsInt();
            monthlyType =
                    json.get("monthlyType").getAsString().equals("dayofmonth") ? MonthlyType.DAY_OF_MONTH : MonthlyType.DAY_OF_WEEK;
            break;
        case "y":
            frequency = Frequency.YEARLY;
            repeatsEvery = json.get("repeatsevery").getAsInt();
            break;
        }

        return new ExplicitConfigWithSettings(start, end, allDay, repeatsEvery, frequency, weekdays, monthlyType, intervals);
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

    public List<Occupation> getOccupations(Integer month, Integer year) {
        DateTime start = new DateTime(year, month, 1, 0, 0);
        Interval interval = new Interval(start, start.plusMonths(1));
        return Bennu.getInstance().getOccupationSet().stream().filter(o -> o.getClass().equals(Occupation.class))
                .filter(o -> o.overlaps(interval)).sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .collect(Collectors.toList());
    }

    public String exportConfig(Occupation occupation) {
        ExplicitConfigWithSettings config = (ExplicitConfigWithSettings) occupation.getConfig();
        JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty("start", config.getStart().toString(datetimeFormatter));
        jsonConfig.addProperty("end", config.getEnd().toString(datetimeFormatter));
        String jsonFrequency = Character.toString(config.getFrequency().name().toLowerCase().charAt(0));
        jsonConfig.addProperty("frequency", jsonFrequency);
        jsonConfig.addProperty("isAllDay", config.getAllDay() != null && config.getAllDay());

        switch (jsonFrequency) {
        case "d":
            jsonConfig.addProperty("repeatsevery", config.getRepeatsevery());
            break;
        case "w":
            jsonConfig.addProperty("repeatsevery", config.getRepeatsevery());
            jsonConfig.add("weekdays", new Gson().toJsonTree(config.getWeekdays()));
            break;
        case "m":
            jsonConfig.addProperty("repeatsevery", config.getRepeatsevery());
            jsonConfig.addProperty("monthlyType",
                    config.getMonthlyType().equals(MonthlyType.DAY_OF_MONTH) ? "dayofmonth" : "dayofweek");
            break;
        case "y":
            jsonConfig.addProperty("repeatsevery", config.getRepeatsevery());
            break;
        }

        return jsonConfig.toString();
    }

    public String exportEvents(Occupation occupation) {
        JsonArray events = new JsonArray();
        for (final Interval i : occupation.getIntervals()) {
            JsonObject event = new JsonObject();
            event.addProperty("start", i.getStartMillis() / 1000);
            event.addProperty("end", i.getEndMillis() / 1000);
            events.add(event);
        }
        return events.toString();
    }

    public List<Space> getFreeAndSelectedSpaces(Occupation occupation, User user) {
        List<Space> spaces = searchFreeSpaces(occupation.getIntervals(), user);
        spaces.addAll(occupation.getSpaces());
        return spaces;
    }

    @Atomic
    public void editOccupation(Occupation occupation, String emails, String subject, String description, String selectedSpaces)
            throws Exception {
        occupation.setEmails(emails);
        occupation.setSubject(subject);
        occupation.setDescription(description);
        final Set<Space> selectedSpaceSet = selectSpaces(selectedSpaces);
        occupation.getSpaces().stream().forEach(s -> occupation.removeSpace(s));
        for (Space space : selectedSpaceSet) {
            if (!space.isFree(occupation.getIntervals())) {
                throw new Exception(messageSource.getMessage("error.occupations.rooms.is.not.free", new Object[0],
                        I18N.getLocale()));
            }
            occupation.addSpace(space);
        }
    }
}
