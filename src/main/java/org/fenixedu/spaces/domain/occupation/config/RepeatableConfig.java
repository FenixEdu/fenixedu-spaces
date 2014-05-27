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
