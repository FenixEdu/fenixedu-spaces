package org.fenixedu.spaces.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Partial;
import org.joda.time.Period;

import com.google.common.collect.ImmutableList;

public class RepeatableInterval extends OccupationSpec {

    final Interval interval;
    final Partial start;
    final Period frequency;
    final Period duration;

    public RepeatableInterval(Interval interval, Partial start, Period frequency, Period duration) {
        super();
        this.interval = interval;
        this.start = start;
        this.frequency = frequency;
        this.duration = duration;
    }

    @Override
    public List<Interval> getIntervals() {
        final List<Interval> intervals = new ArrayList<>();
//        for (final DateTime date : getDates()) {
//            DateTime intervalStart = date.withFields(start);
//            DateTime intervalEnd = date.withFields(end);
//            Interval interval = new Interval(intervalStart, intervalEnd);
//            intervals.add(interval);
//        }
        return ImmutableList.copyOf(intervals);
    }

    private List<DateTime> getDates() {
        final List<DateTime> dates = new ArrayList<DateTime>();
//        DateTime start = getNextDayOfWeek(interval.getStart(), dayOfWeek);
//        Period current = frequency;
//        DateTime nextOccurence = start;
//        while (interval.contains(nextOccurence)) {
//            dates.add(nextOccurence);
//            nextOccurence = nextOccurence.plus(current);
//        }
        return ImmutableList.copyOf(dates);
    }

}
