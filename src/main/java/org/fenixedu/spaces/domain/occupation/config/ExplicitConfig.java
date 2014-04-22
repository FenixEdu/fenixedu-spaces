package org.fenixedu.spaces.domain.occupation.config;

import java.util.List;

import org.joda.time.Interval;

import com.google.gson.JsonElement;

public class ExplicitConfig extends OccupationConfig {

    private final JsonElement config;
    private final List<Interval> intervals;

    public ExplicitConfig(JsonElement config, List<Interval> intervals) {
        this.config = config;
        this.intervals = intervals;
    }

    @Override
    public List<Interval> getIntervals() {
        return intervals;
    }

    public final JsonElement getConfig() {
        return config;
    }

}
