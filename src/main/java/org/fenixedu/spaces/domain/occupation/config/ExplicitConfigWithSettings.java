/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Spaces.
 *
 * FenixEdu Spaces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Spaces is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.spaces.domain.occupation.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.Partial;

public class ExplicitConfigWithSettings extends OccupationConfig {

    private static final String BUNDLE = "resources/FenixEduSpacesResources";

    public static enum Frequency {
        NEVER, DAILY, WEEKLY, MONTHLY, YEARLY;
    }

    public static enum MonthlyType {
        DAY_OF_MONTH, DAY_OF_WEEK;
    }

    private final DateTime start;
    private final DateTime end;
    private final Boolean allDay;
    private final Frequency frequency;
    private final Integer repeatsevery;

    // For Weekly
    private final List<Integer> weekdays;

    // For monthly
    private MonthlyType monthlyType;

    private final List<Interval> intervals;

    public ExplicitConfigWithSettings(DateTime start, DateTime end, Boolean allDay, List<Interval> intervals) {
        this(start, end, allDay, null, Frequency.NEVER, null, null, intervals);
    }

    public ExplicitConfigWithSettings(DateTime start, DateTime end, Boolean allDay, Frequency frequency, Integer repeatsEvery,
            List<Interval> intervals) {
        this(start, end, allDay, repeatsEvery, frequency, null, null, intervals);
    }

    public ExplicitConfigWithSettings(DateTime start, DateTime end, Boolean allDay, Integer repeatsEvery, Frequency frequency,
            List<Integer> weekdays, MonthlyType monthlyType, List<Interval> intervals) {
        super();
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.repeatsevery = repeatsEvery;
        this.frequency = frequency;
        this.weekdays = weekdays;
        this.monthlyType = monthlyType;
        this.intervals = intervals;
    }

    @Override
    public DateTime getStart() {
        if (start != null) {
            return start;
        }
        return super.getStart();
    }

    @Override
    public DateTime getEnd() {
        if (end != null) {
            return end;
        }
        return super.getEnd();
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public Integer getRepeatsevery() {
        return repeatsevery;
    }

    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public MonthlyType getMonthlyType() {
        return monthlyType;
    }

    @Override
    public List<Interval> getIntervals() {
        return intervals;
    }

    @Override
    public String getSummary() {
        String beginDate = getStart().toString("dd/MM/yyyy");
        String endDate = getEnd().toString("dd/MM/yyyy");

        String beginTime = getStart().toString("HH:mm:ss");
        String endTime = getEnd().toString("HH:mm:ss");

        char frequencySymbol = BundleUtil.getString(BUNDLE, "label.frequency." + getFrequency().name()).charAt(0);

        if (Frequency.NEVER.equals(getFrequency())) {
            return String.format("%s %s - %s %s", beginDate, beginTime, endDate, endTime);
        }

        return String.format("[%s] [%s - %s] (%s - %s)", frequencySymbol, beginDate, endDate, beginTime, endTime);
    }

    @Override
    public String getExtendedSummary() {
        if (getFrequency() != null && !Frequency.NEVER.equals(getFrequency())) {
            String frequency = BundleUtil.getString(BUNDLE, "label.frequency." + getFrequency().name());
            String frequencyUnit = BundleUtil.getString(BUNDLE, "label.frequency.unit." + getFrequency().name());

            String repeats =
                    BundleUtil.getString(BUNDLE, "label.occupation.config.summary", getRepeatsevery().toString(), frequencyUnit);

            if (Frequency.MONTHLY.equals(getFrequency())) {
                repeats = getMonthlySummary();
            }
            if (Frequency.WEEKLY.equals(getFrequency())) {
                String weekdays =
                        getWeekdays().stream().map(w -> new Partial(DateTimeFieldType.dayOfWeek(), w)).map(p -> p.toString("E"))
                        .collect(Collectors.joining(","));
                repeats =
                        Stream.of(repeats, BundleUtil.getString(BUNDLE, "label.occupation.config.summary.weekdays", weekdays))
                        .collect(Collectors.joining(" "));
            }

            return String.format("%s %s", frequency, repeats);
        }
        return BundleUtil.getString(BUNDLE, "label.occupation.config.no.repeat");
    }

    private String getMonthlySummary() {
        switch (getMonthlyType()) {
        case DAY_OF_MONTH:
            return BundleUtil.getString(BUNDLE, "label.occupation.config.summary.DAY_OF_MONTH", new Integer(getStart()
                    .getDayOfMonth()).toString());
        case DAY_OF_WEEK:
            return BundleUtil.getString(BUNDLE, "label.occupation.config.summary.DAY_OF_WEEK",
                    getNthDayOfTheWeekLabel(getStart()), getStart().toString("EEEE"));
        default:
            return null;
        }
    }

    private static int getNthDayOfWeek(DateTime when) {
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

    private static String getNthDayOfTheWeekLabel(DateTime when) {
        int nth = getNthDayOfWeek(when);
        if (nth > 3) {
            return BundleUtil.getString(BUNDLE, "label.ordinal.numbers.last");
        }
        return BundleUtil.getString(BUNDLE, "label.ordinal.numbers." + nth);
    }
}
