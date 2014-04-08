package org.fenixedu.spaces.domain.occupation.config;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

public class WeeklyConfig extends RepeatableConfig {

    private final List<Integer> daysOfWeek;

    public WeeklyConfig(Interval interval, LocalTime startTime, LocalTime endTime, Integer repeatsEvery, List<Integer> daysOfWeek) {
        super(interval, startTime, endTime, repeatsEvery);
        this.daysOfWeek = daysOfWeek;
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            throw new IllegalArgumentException("days of week can't be empty or null");
        }
    }

    @Override
    public List<Interval> getIntervals() {
        final List<Interval> intervals = new ArrayList<>();
        DateTime start = getInterval().getStart();
        DateTime end = getInterval().getEnd();

        // adjust start date to correct day of the week
        int firstDayOfWeekIndex = daysOfWeek.indexOf(start.getDayOfWeek());
        if (firstDayOfWeekIndex == -1) {
            firstDayOfWeekIndex = 0;
        }

        DateTime checkpoint = start.withDayOfWeek(daysOfWeek.get(firstDayOfWeekIndex));
        if (checkpoint.isBefore(start)) {
            checkpoint.plusWeeks(1);
        }

        int i = firstDayOfWeekIndex;

        while (checkpoint.isBefore(end) || checkpoint.isEqual(end)) {
            intervals.add(new Interval(checkpoint.withFields(getStartTime()), checkpoint.withFields(getEndTime())));
            if (i == daysOfWeek.size() - 1) {
                i = 0;
                checkpoint = checkpoint.plusWeeks(getRepeatsEvery());
            } else {
                i++;
            }
            checkpoint = checkpoint.withDayOfWeek(daysOfWeek.get(i));
        }
        return intervals;
    }

}
