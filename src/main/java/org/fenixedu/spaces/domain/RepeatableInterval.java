package org.fenixedu.spaces.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import com.google.common.collect.ImmutableList;

public class RepeatableInterval extends OccupationSpec {
    final Interval interval;
    final LocalTime start;
    final LocalTime end;
    final Period frequency;
    final int dayOfWeek;

    private static final int NO_DAY_OF_WEEK = -1;

    public RepeatableInterval(Interval interval, LocalTime startTime, LocalTime endTime, Period frequency, int dayOfWeek) {
        this.interval = interval;
        this.start = startTime;
        this.end = endTime;
        this.frequency = frequency;
        this.dayOfWeek = dayOfWeek;
    }

    public RepeatableInterval(Interval interval, LocalTime startTime, LocalTime endTime, Period frequency) {
        this.interval = interval;
        this.start = startTime;
        this.end = endTime;
        this.frequency = frequency;
        this.dayOfWeek = NO_DAY_OF_WEEK;
    }

    @Override
    public List<Interval> getIntervals() {
        final List<Interval> intervals = new ArrayList<>();
        for (final DateTime date : getDates()) {
            DateTime intervalStart = date.withFields(start);
            DateTime intervalEnd = date.withFields(end);
            Interval interval = new Interval(intervalStart, intervalEnd);
            intervals.add(interval);
        }
        return ImmutableList.copyOf(intervals);
    }

    private List<DateTime> getDates() {
        final List<DateTime> dates = new ArrayList<DateTime>();
        DateTime start = getNextDayOfWeek(interval.getStart(), dayOfWeek);
        Period current = frequency;
        DateTime nextOccurence = start;
        while (interval.contains(nextOccurence)) {
            dates.add(nextOccurence);
            nextOccurence = nextOccurence.plus(current);
        }
        return ImmutableList.copyOf(dates);
    }

    /**
     * returns the next datetime which is the next dayOfWeek
     * if datetime is the same as dayOfWeek returns the same instance.
     * 
     * @param startDay initial datetime
     * @param dayOfWeek next day of week
     * @return
     */
    private DateTime getNextDayOfWeek(DateTime startDay, int dayOfWeek) {
        int currentDayOfWeek = startDay.getDayOfWeek();
        if (currentDayOfWeek == dayOfWeek) {
            return startDay;
        }
        if (dayOfWeek < currentDayOfWeek) {
            return startDay.plusDays(dayOfWeek - currentDayOfWeek);
        }
        if (currentDayOfWeek > dayOfWeek) {
            return startDay.plusWeeks(1).minusDays(currentDayOfWeek - dayOfWeek);
        }
        throw new Error("something went wrong");
    }
}
