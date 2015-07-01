package org.fenixedu.spaces.domain.occupation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.FenixEduSpaceConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.config.ExplicitConfigWithSettings;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.ui.SpaceOccupantsBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.JsonObject;

public class SharedOccupation extends SharedOccupation_Base {

    public SharedOccupation() {
        super();
        setBennu(Bennu.getInstance());
    }

    public SharedOccupation(String emails, String subject, String description, OccupationConfig config) {
        this(emails, subject, description, config, null);
    }

    public SharedOccupation(String emails, String subject, String description, OccupationConfig config, OccupationRequest request) {
        this();
        setConfig(config);
        setEmails(emails);
        setSubject(subject);
        setDescription(description);
        setRequest(request);
    }

    public boolean doConfig(SpaceOccupantsBean sob) {
        return doAction(sob);
    }

    @Override
    public void addSpace(Space space) {
        if (getSpaces().size() >= 1) {
            return;
        }
        super.addSpace(space);
    }

    @Override
    public void removeSpace(Space space) {
        super.removeSpace(space);
    }

    @Override
    public Set<Space> getSpaces() {
        return getSpaceSet().stream().filter(space -> space.isActive()).collect(Collectors.toSet());
    }

    public List<Interval> getActiveIntervals() {
        return getConfig().getIntervals().stream().filter(i -> i.contains(new DateTime())).collect(Collectors.toList());
    }

    public List<Interval> getInactiveIntervals() {
        return getConfig().getIntervals().stream().filter(i -> !i.contains(new DateTime())).collect(Collectors.toList());
    }

    @Override
    public Boolean overlaps(List<Interval> intervals) {
        for (final Interval interval : intervals) {
            for (final Interval occupationInterval : getIntervals()) {
                if (occupationInterval.overlaps(interval)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean overlaps(Interval... intervals) {
        for (final Interval interval : intervals) {
            for (final Interval occupationInterval : getIntervals()) {
                if (occupationInterval.overlaps(interval)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean isActive() {
        return getIntervals().stream().anyMatch(interval -> interval.contains(DateTime.now()));
    }

    @Override
    public String getSummary() {
        return getConfig().getSummary();
    }

    @Override
    public String getExtendedSummary() {
        return getConfig().getExtendedSummary();
    }

    @Override
    public DateTime getStart() {
        return getConfig().getStart();
    }

    @Override
    public DateTime getEnd() {
        return getConfig().getEnd();
    }

    @Override
    public void delete() {
        if (getRequest() != null) {
            setRequest(null);
        }
        setBennu(null);
        getSpaceSet().clear();
        super.deleteDomainObject();
    }

    @Override
    public boolean canManageOccupation(User user) {
        for (Space space : getSpaces()) {
            if (!space.isOccupationMember(user)) {
                return false;
            }
        }
        return true;
    }

    private List<Interval> removeIntervals(List<Interval> li, SpaceOccupantsBean config) {
        List<Interval> linterval = new ArrayList<Interval>(li);
        if (config.getAction().equals("add") || config.getOldIntervalList() == null) {
            return linterval;
        }
        int pos = -1;
        for (int i = 0; i < linterval.size(); i++) {
            if (linterval.get(i).equals(config.getOldIntervalList().get(0))) {
                pos = i;
                break;
            }
        }
        if (pos == -1) {
            throw new SpaceOccupationException("error", "label.invalidOldInterval", "");
        }
        if (pos != -1) {
            linterval.remove(pos);
        }
        return linterval;
    }

    private List<Interval> addIntervals(List<Interval> li, SpaceOccupantsBean config) {
        List<Interval> linterval = new ArrayList<Interval>(li);
        if (config.getAction().equals("remove") || config.getNewIntervalList() == null) {
            return linterval;
        }
        linterval.add(config.getNewIntervalList().get(0));
        return linterval;
    }

    private static class SpaceOccupationException extends DomainException {

        String kind;

        protected SpaceOccupationException(String kind, String label, String message) {
            super(Status.INTERNAL_SERVER_ERROR, FenixEduSpaceConfiguration.BUNDLE, label, message);
            this.kind = kind;
        }

        @Override
        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            json.addProperty(kind, getLocalizedMessage());
            return json;
        }

    }

    public boolean doAction(SpaceOccupantsBean config) {
        User user = config.getUserObject();
        if (user == null) {
            throw new SpaceOccupationException("error", "label.nosuchuser", config.getUser());
        }
        // we should make sure that this sharedOccupation is either empty or belongs to this user...
        if (getUser() != null && !getUser().equals(user)) {
            throw new SpaceOccupationException("error", "label.usermismatch", config.getUser());
        }
        if (getUser() == null) {
            setUser(user);
        }

        OccupationConfig old = getConfig();
        if (old == null) {
            List<Interval> in = config.getNewIntervalList();
            DateTime start = in.iterator().next().getStart();
            DateTime end = in.iterator().next().getEnd();
            OccupationConfig oc = new ExplicitConfigWithSettings(start, end, true, in);
            setConfig(oc);
            return true;
        }
        List<Interval> li = old.getIntervals();
        li = removeIntervals(li, config);
        li = addIntervals(li, config);
        DateTime start = new DateTime().year().withMaximumValue();
        DateTime end = new DateTime(0);
        for (Interval i : li) {
            if (start.isAfter(i.getStart())) {
                start = i.getStart();
            }
            if (end.isBefore(i.getEnd())) {
                end = i.getEnd();
            }
        }
        OccupationConfig oc = new ExplicitConfigWithSettings(start, end, true, li);
        setConfig(oc);
        return true;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
