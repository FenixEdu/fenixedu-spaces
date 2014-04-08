package org.fenixedu.spaces.domain;

import java.util.List;

import org.joda.time.Interval;

import com.google.common.collect.ImmutableList;

public class OccupationInterval extends OccupationSpec {
    final Interval interval;

    public OccupationInterval(Interval interval) {
        this.interval = interval;
    }

    @Override
    public List<Interval> getIntervals() {
        return ImmutableList.of(interval);
    }

}
