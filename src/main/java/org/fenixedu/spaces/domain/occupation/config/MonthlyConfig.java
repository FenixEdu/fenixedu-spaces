package org.fenixedu.spaces.domain.occupation.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

public class MonthlyConfig extends RepeatableConfig {

    static enum MonthlyType {
        DAY_OF_MONTH, DAY_OF_WEEK;
    }

    private final MonthlyType monthlyType;

    public MonthlyConfig(Interval interval, LocalTime startTime, LocalTime endTime, Integer repeatsEvery, MonthlyType monthlyType) {
        super(interval, startTime, endTime, repeatsEvery);
        this.monthlyType = monthlyType;
    }

    @Override
    public List<Interval> getIntervals() {
        switch (monthlyType) {
        case DAY_OF_MONTH:
            return getDayOfMonthIntervals();
        case DAY_OF_WEEK:
            return getDayOfWeekIntervals();
        }
        return Collections.emptyList();
    }

    private List<Interval> getDayOfWeekIntervals() {
        final List<Interval> intervals = new ArrayList<>();

        DateTime startDate = getInterval().getStart();
        int nthDayOfWeek = getNthDayOfWeek(startDate);
        int dayOfWeek = startDate.getDayOfWeek();

        DateTime endDate = getInterval().getEnd();

        DateTime start = startDate;
        while (start.isBefore(endDate) || start.isEqual(endDate)) {
            intervals.add(new Interval(start.withFields(getStartTime()), start.withFields(getEndTime())));
            start = start.plusMonths(getRepeatsEvery());
            start = getNextNthdayOfWeek(start, nthDayOfWeek, dayOfWeek);
        }

        return intervals;
    }

    private List<Interval> getDayOfMonthIntervals() {
        final List<Interval> intervals = new ArrayList<>();
        DateTime startDate = getInterval().getStart();
        DateTime endDate = getInterval().getEnd();
        DateTime start = startDate;
        while (start.isBefore(endDate) || start.isEqual(endDate)) {
            intervals.add(new Interval(start.withFields(getStartTime()), start.withFields(getEndTime())));
            start = start.plusMonths(getRepeatsEvery());
        }
        return intervals;
    }

    private int getNthDayOfWeek(DateTime when) {
        DateTime checkpoint = when;
        int whenDayOfWeek = checkpoint.getDayOfWeek();
        int month = checkpoint.getMonthOfYear();
        checkpoint = checkpoint.withDayOfMonth(1);
        checkpoint = checkpoint.withDayOfWeek(whenDayOfWeek);
        checkpoint = checkpoint.plusWeeks(month - checkpoint.getDayOfMonth());
        int i = 0;
        while (checkpoint.getMonthOfYear() == month && !checkpoint.isEqual(when)) {
            checkpoint = checkpoint.plusWeeks(1);
            i++;
        }
        return i;
    }

    private DateTime getNextNthdayOfWeek(DateTime when, int nthdayOfTheWeek, int dayOfTheWeek) {
        DateTime checkpoint = when;
        int month = checkpoint.getMonthOfYear();
        checkpoint = checkpoint.withDayOfMonth(1);
        checkpoint = checkpoint.plusWeeks(month - checkpoint.getMonthOfYear());
        int i = nthdayOfTheWeek;
        if (i > 3) {
            DateTime lastDayOfMonth = checkpoint.dayOfMonth().withMaximumValue();
            lastDayOfMonth = lastDayOfMonth.withDayOfWeek(dayOfTheWeek);
            return lastDayOfMonth.plusWeeks(month - lastDayOfMonth.getMonthOfYear());
        } else {
            return checkpoint.plusWeeks(nthdayOfTheWeek);
        }
    }
}