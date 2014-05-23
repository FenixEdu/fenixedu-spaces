package org.fenixedu.spaces.domain.occupation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class Occupation extends Occupation_Base {

    public Occupation() {
        super();
        setBennu(Bennu.getInstance());
    }

    public Occupation(OccupationRequest request, OccupationConfig config) {
        super();
        setBennu(Bennu.getInstance());
        setRequest(request);
        setConfig(config);
    }

    public Occupation(String subject, String description, OccupationConfig config) {
        this(null, subject, description, config);
    }

    public Occupation(String emails, String subject, String description, OccupationConfig config) {
        this();
        setConfig(config);
        setDetails(new OccupationDetails(emails, subject, description));
    }

    @Override
    public void addSpace(Space space) {
        super.addSpace(space);
    }

    @Override
    public void removeSpace(Space space) {
        super.removeSpace(space);
    }

    public Set<Space> getSpaces() {
        return getSpaceSet().stream().filter(space -> space.isActive()).collect(Collectors.toSet());
    }

    public List<Interval> getIntervals() {
        return getConfig().getIntervals();
    }

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

    public String getSubject() {
        if (getRequest() != null) {
            return getRequest().getSubject();
        }
        if (getDetails() != null) {
            return getDetails().getSubject();
        }
        return null;
    }

    public String getDescription() {
        if (getRequest() != null) {
            return getRequest().getDescription();
        }
        if (getDetails() != null) {
            return getDetails().getDescription();
        }
        return null;
    }

    public Boolean isActive() {
        return !getIntervals().get(getIntervals().size() - 1).getEnd().isBeforeNow();
    }

    public String getSummary() {
        return getConfig().getSummary();
    }

    public String getExtendedSummary() {
        return getConfig().getExtendedSummary();
    }

    public DateTime getStart() {
        return getConfig().getStart();
    }

    public DateTime getEnd() {
        return getConfig().getEnd();
    }

}
