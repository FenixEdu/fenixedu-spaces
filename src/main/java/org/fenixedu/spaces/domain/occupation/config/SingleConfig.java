package org.fenixedu.spaces.domain.occupation.config;

import org.joda.time.Interval;

import com.google.common.collect.Lists;

public class SingleConfig extends ExplicitConfig {

    public SingleConfig(Interval interval) {
        super(Lists.newArrayList(interval));
    }

}
