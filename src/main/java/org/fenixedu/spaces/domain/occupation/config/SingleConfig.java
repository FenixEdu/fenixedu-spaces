package org.fenixedu.spaces.domain.occupation.config;

import org.joda.time.Interval;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public class SingleConfig extends ExplicitConfig {

    public SingleConfig(JsonElement config, Interval interval) {
        super(config, Lists.newArrayList(interval));
    }

}
