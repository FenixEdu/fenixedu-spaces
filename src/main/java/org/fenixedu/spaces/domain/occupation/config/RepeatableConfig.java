package org.fenixedu.spaces.domain.occupation.config;

import org.joda.time.Interval;
import org.joda.time.LocalTime;

public abstract class RepeatableConfig extends OccupationConfig {

    private final Interval interval;
    private final LocalTime startTime;
    private final LocalTime endTime;

    private final Integer repeatsEvery;

    public RepeatableConfig(Interval interval, LocalTime startTime, LocalTime endTime, Integer repeatsEvery) {
        this.interval = interval;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeatsEvery = repeatsEvery;
    }

    public Interval getInterval() {
        return interval;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getRepeatsEvery() {
        return repeatsEvery;
    }

}
