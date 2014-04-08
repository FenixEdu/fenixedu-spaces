package org.fenixedu.spaces.domain.occupation.config;

import java.util.List;

import org.joda.time.Interval;

public class ExplicitConfig extends OccupationConfig {

    private final List<Interval> intervals;

    public ExplicitConfig(List<Interval> intervals) {
        this.intervals = intervals;
    }

    @Override
    public List<Interval> getIntervals() {
        return intervals;
    }

}
