package org.fenixedu.spaces.domain.occupation;

import java.util.List;

import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.joda.time.Interval;

public class Occupation extends Occupation_Base {

    public Occupation(String subject, String description, OccupationConfig config) {
        this(null, subject, description, config);
    }

    public Occupation(String emails, String subject, String description, OccupationConfig config) {
        super();
        setConfig(config);
        setDetails(new OccupationDetails(emails, subject, description));
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
}
