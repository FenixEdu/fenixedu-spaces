package org.fenixedu.spaces;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.Partial;
import org.joda.time.Period;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestOccupationSpec {

//    @Test
//    public void testIntervals() {
//        final LocalTime midnight = new LocalTime(0, 0, 0, 0);
//
//        final LocalTime startTime = new LocalTime(15, 0, 0, 0);
//        final LocalTime endTime = new LocalTime(17, 0, 0, 0);
//        final Period frequency = Period.weeks(1);
//
//        final DateTime startDateInterval = new DateTime().withFields(midnight);
//        final DateTime endDateInterval = startDateInterval.plusMonths(4);
//
//        System.out.printf("Start Date Interval : %s\n", startDateInterval.toString());
//        System.out.printf("End Date Interval : %s\n", endDateInterval.toString());
//        System.out.printf("Start Time: %s\n", startTime.toString());
//        System.out.printf("End Time: %s\n", endTime.toString());
//
//        final RepeatableInterval repeatableInterval =
//                new RepeatableInterval(new Interval(startDateInterval, endDateInterval), startTime, endTime, frequency,
//                        DateTimeConstants.WEDNESDAY);
//
//        for (Interval interval : repeatableInterval.getIntervals()) {
//            System.out.println(interval.toString());
//        }
//    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getMethodName());
        }
    };

    @Test
    public void testPartials() {

        DateTimeFieldType[] fields =
                new DateTimeFieldType[] { DateTimeFieldType.dayOfWeek(), DateTimeFieldType.hourOfDay(),
                        DateTimeFieldType.minuteOfHour() };

        int[] values = new int[] { 4, 10, 0 };
        Partial partial = new Partial(fields, values);

        DateTime start = new DateTime().withSecondOfMinute(0).withMillisOfSecond(00);
        DateTime end = start.plusMonths(4);
        Interval occupation = new Interval(start, end);

        Period frequency = Period.weeks(2);
        Period duration = Period.minutes(60);
        DateTime iStart = start.withFields(partial);

        List<Interval> intervals = new ArrayList<>();
        while (occupation.contains(iStart)) {
            intervals.add(new Interval(iStart, iStart.plus(duration)));
            iStart = iStart.plus(frequency);
        }

        for (Interval interval : intervals) {
            System.out.println(interval.toString());
        }

    }

    @Test
    public void testPartialsDays() {

        DateTimeFieldType[] fields =
                new DateTimeFieldType[] { DateTimeFieldType.dayOfMonth(), DateTimeFieldType.hourOfDay(),
                        DateTimeFieldType.minuteOfHour() };

        int[] values = new int[] { 6, 10, 0 };
        Partial partial = new Partial(fields, values);

        DateTime start = new DateTime(2000, 1, 1, 0, 0);
        DateTime end = new DateTime(2014, 1, 1, 0, 0);
        Interval occupation = new Interval(start, end);

        Period frequency = Period.months(2);
        Period duration = Period.minutes(60);
        DateTime iStart = start.withFields(partial);

        List<Interval> intervals = new ArrayList<>();
        while (occupation.contains(iStart)) {
            intervals.add(new Interval(iStart, iStart.plus(duration)));
            iStart = iStart.plus(frequency);
        }

        for (Interval interval : intervals) {
            System.out.println(interval.toString());
        }

    }

    @Test
    public void testPartials1DayWeek() {

        DateTimeFieldType[] fields =
                new DateTimeFieldType[] { DateTimeFieldType.dayOfWeek(), DateTimeFieldType.hourOfDay(),
                        DateTimeFieldType.minuteOfHour() };

        int[] values = new int[] { 3, 8, 0 };
        Partial partial = new Partial(fields, values);

        DateTime start = new DateTime(2014, 1, 1, 0, 0);
        DateTime end = new DateTime(2014, 12, 1, 0, 0);
        Interval occupation = new Interval(start, end);

        Period frequency = Period.months(1);
        Period duration = Period.hours(2);
        DateTime iStart = start;
        Interval i = occupation;
        List<Interval> intervals = new ArrayList<>();
        while (occupation.overlaps(i)) {
            iStart = iStart.withFields(partial);
            i = new Interval(iStart, iStart.plus(duration));
            intervals.add(i);
            iStart = iStart.plus(frequency);
            System.out.println(iStart);
        };

        for (Interval interval : intervals) {
            System.out.println(interval.toString());
        }

    }
}
