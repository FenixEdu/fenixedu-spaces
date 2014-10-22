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

package org.fenixedu.spaces.ui;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.occupation.config.ExplicitConfigWithSettings;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpaceOccupantsBean {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private String intervals;
    private final JsonParser jsonParser = new JsonParser();

    private List<Interval> parseIntervals(String events) {
        final JsonArray jsonEvents = jsonParser.parse(events).getAsJsonArray();
        List<Interval> intervals = new ArrayList<>();
        for (JsonElement jsonEvent : jsonEvents) {
            final JsonObject event = jsonEvent.getAsJsonObject();
            final Long start = Long.parseLong(event.get("start").getAsString()) * 1000L;
            final Long end = Long.parseLong(event.get("end").getAsString()) * 1000L;
            intervals.add(new Interval(new DateTime(start), new DateTime(end)));
        }
        return intervals;
    }

    private String user;

    public SpaceOccupantsBean() {
    }

    public OccupationConfig getConfig() {
        ExplicitConfigWithSettings ecws;
        List<Interval> intervalsList = parseIntervals(intervals);
        DateTime endDate = new DateTime(0);
        DateTime startDate = new DateTime(Long.MAX_VALUE);
        for (Interval i : intervalsList) {
            if (startDate.isAfter(i.getStart())) {
                startDate = i.getStart();
            }
            if (endDate.isBefore(i.getEnd())) {
                endDate = i.getEnd();
            }
        }
        ecws = new ExplicitConfigWithSettings(startDate, endDate, true, intervalsList);
        return ecws;
    }

    public User getUserObject() {
        return User.findByUsername(user);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
