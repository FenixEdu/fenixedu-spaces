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

import org.joda.time.Interval;

public class SharedOccupationConfig extends OccupationConfig {

    private static final String BUNDLE = "resources/FenixEduSpacesResources";

    public SharedOccupationConfig() {
    }

//    @Override
//    public DateTime getStart() {
//        DateTime start = null;
//        if (OccupationParameters.size() > 0) {
//            for (Map.Entry<User, OccupationConfig> pair : OccupationParameters.entrySet()) {
//                if (start == null || start.isBefore(pair.getValue().getStart())) {
//                    start = pair.getValue().getStart();
//                }
//
//            }
//        }
//        return start;
//    }
//
//    public void addConfig(SpaceOccupantsBean sob) {
//
//    }
//
//    @Override
//    public DateTime getEnd() {
//        DateTime end = null;
//        if (OccupationParameters.size() > 0) {
//            for (Map.Entry<User, OccupationConfig> pair : OccupationParameters.entrySet()) {
//                if (end == null || end.isAfter(pair.getValue().getEnd())) {
//                    end = pair.getValue().getEnd();
//                }
//            }
//        }
//        return end;
//    }
//
    @Override
    public List<Interval> getIntervals() {
        List<Interval> intervals = new ArrayList<Interval>();

        return intervals;
    }
//
//    @Override
//    public String getSummary() {
//        return "";
//    }
//
//    @Override
//    public String getExtendedSummary() {
//        return "";
//    }

}
