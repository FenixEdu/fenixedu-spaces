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
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpaceOccupantsBean {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private String oldInterval;
    private String newInterval;
    private String action;
    private final JsonParser jsonParser = new JsonParser();
    private String warningMessage;

    private List<Interval> parseIntervals(String events) {
        final JsonArray jsonEvents = jsonParser.parse(events).getAsJsonArray();
        List<Interval> intervals = new ArrayList<>();
        for (JsonElement jsonEvent : jsonEvents) {
            final JsonObject event = jsonEvent.getAsJsonObject();
            final Long start = Long.parseLong(event.get("start").getAsString());
            final Long end = Long.parseLong(event.get("end").getAsString());
            intervals.add(new Interval(new DateTime(start), new DateTime(end)));
        }
        return intervals;
    }

    private String user;

    public SpaceOccupantsBean() {
        warningMessage = "";
    }

    public User getUserObject() {
        return User.findByUsername(user);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<Interval> getOldIntervalList() {
        return parseIntervals(oldInterval);
    }

    public List<Interval> getNewIntervalList() {
        return parseIntervals(newInterval);
    }

    public String getOldInterval() {
        return oldInterval;
    }

    public void setOldInterval(String oldInterval) {
        this.oldInterval = oldInterval;
    }

    public String getNewInterval() {
        return newInterval;
    }

    public void setNewInterval(String newInterval) {
        this.newInterval = newInterval;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public boolean hasWarningMessage() {
        return !warningMessage.isEmpty();
    }

    /***
     * 
     * This method is used in occupants.jsp due to SPel limitations when choosing overridden isAfter methods (long, DateTime)
     *
     */
    public static boolean isAfter(DateTime d1, DateTime d2) {
        return d1.isAfter(d2);
    }

}
