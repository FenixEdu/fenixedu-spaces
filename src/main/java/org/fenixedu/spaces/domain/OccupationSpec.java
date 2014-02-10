package org.fenixedu.spaces.domain;

import java.util.List;

import org.joda.time.Interval;

public abstract class OccupationSpec {

    public abstract List<Interval> getIntervals();

}
