package org.fenixedu.spaces;

import org.fenixedu.spaces.domain.RepeatableInterval;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Test;

public class TestOccupationSpec {

    @Test
    public void testIntervals() {
        final LocalTime midnight = new LocalTime(0, 0, 0, 0);

        final LocalTime startTime = new LocalTime(15, 0, 0, 0);
        final LocalTime endTime = new LocalTime(17, 0, 0, 0);
        final Period frequency = Period.weeks(1);

        final DateTime startDateInterval = new DateTime().withFields(midnight);
        final DateTime endDateInterval = startDateInterval.plusMonths(4);

        System.out.printf("Start Date Interval : %s\n", startDateInterval.toString());
        System.out.printf("End Date Interval : %s\n", endDateInterval.toString());
        System.out.printf("Start Time: %s\n", startTime.toString());
        System.out.printf("End Time: %s\n", endTime.toString());

        final RepeatableInterval repeatableInterval =
                new RepeatableInterval(new Interval(startDateInterval, endDateInterval), startTime, endTime, frequency,
                        DateTimeConstants.WEDNESDAY);

        for (Interval interval : repeatableInterval.getIntervals()) {
            System.out.println(interval.toString());
        }
    }
}
