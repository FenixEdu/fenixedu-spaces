package org.fenixedu.spaces.domain.occupation;

import java.util.List;

import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.joda.time.Interval;

public class Occupation extends Occupation_Base {

    public Occupation(OccupationConfig config) {
        super();
        setConfig(config);
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

}
