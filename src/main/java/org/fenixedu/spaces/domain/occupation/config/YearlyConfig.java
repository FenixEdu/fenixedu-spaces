package org.fenixedu.spaces.domain.occupation.config;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

public class YearlyConfig extends RepeatableConfig {

    public YearlyConfig(Interval interval, LocalTime startTime, LocalTime endTime, Integer repeatsEvery) {
        super(interval, startTime, endTime, repeatsEvery);
    }

    @Override
    public List<Interval> getIntervals() {
        List<Interval> intervals = new ArrayList<>();
        DateTime startDate = getInterval().getStart();
        DateTime endDate = getInterval().getEnd();
        DateTime start = startDate;
        while (start.isBefore(endDate) || start.isEqual(endDate)) {
            intervals.add(new Interval(start.withFields(getStartTime()), start.withFields(getEndTime())));
            start = start.plusYears(getRepeatsEvery());
        }
        return intervals;
    }

}
