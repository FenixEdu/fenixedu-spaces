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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

public class YearlyConfig extends RepeatableConfig {

    public YearlyConfig(Interval interval, LocalTime startTime, LocalTime endTime, Integer repeatsEvery) {
        super(interval, startTime, endTime, repeatsEvery);
    }

    @Override
    public List<Interval> getIntervals() {
        List<Interval> intervals = new ArrayList<>();
        DateTime startDate = getInterval().getStart();
        DateTime endDate = getInterval().getEnd();
        DateTime start = startDate;
        while (start.isBefore(endDate) || start.isEqual(endDate)) {
            intervals.add(new Interval(start.withFields(getStartTime()), start.withFields(getEndTime())));
            start = start.plusYears(getRepeatsEvery());
        }
        return intervals;
    }

}
