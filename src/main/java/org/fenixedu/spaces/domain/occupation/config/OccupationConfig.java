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

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

public abstract class OccupationConfig {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().registerTypeAdapter(Interval.class, new JsonSerializer<Interval>() {

            @Override
            public JsonElement serialize(Interval src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject json = new JsonObject();
                json.addProperty("start", src.getStart().toString());
                json.addProperty("end", src.getEnd().toString());
                return json;
            }
        }).registerTypeAdapter(Interval.class, new JsonDeserializer<Interval>() {

            @Override
            public Interval deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                final JsonObject intervalJson = json.getAsJsonObject();
                final DateTime start = new DateTime(intervalJson.get("start").getAsString());
                final DateTime end = new DateTime(intervalJson.get("end").getAsString());
                return new Interval(start, end);
            }
        }).registerTypeAdapter(LocalTime.class, new JsonSerializer<LocalTime>() {

            @Override
            public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString("HH:mm:ss"));
            }
        }).registerTypeAdapter(LocalTime.class, new JsonDeserializer<LocalTime>() {

            @Override
            public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return LocalTime.parse(json.getAsString());
            }
        }).registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {

            @Override
            public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return DateTime.parse(json.getAsString());
            }

        }).registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {

            @Override
            public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }

        }).create();
    }

    public abstract List<Interval> getIntervals();

    public static OccupationConfig internalize(JsonElement json) {
        if (json == null) {
            return null;
        }
        final JsonObject jsonObject = json.getAsJsonObject();
        final String type = jsonObject.get("type").getAsString();
        try {
            return (OccupationConfig) gson.fromJson(json, Class.forName(type));
        } catch (JsonSyntaxException | ClassNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

    public JsonElement externalize() {
        final JsonElement jsonTree = gson.toJsonTree(this, this.getClass());
        jsonTree.getAsJsonObject().addProperty("type", this.getClass().getName());
        return jsonTree;
    }

    public static Gson gson() {
        return gson;
    }

    public String getSummary() {
        return getIntervals().toString();
    }

    public String getExtendedSummary() {
        return getIntervals().toString();
    }

    public DateTime getStart() {
        return getSortedIntervals().isEmpty() ? null : getSortedIntervals().get(0).getStart();
    }

    public DateTime getEnd() {
        return getSortedIntervals().isEmpty() ? null : getSortedIntervals().get(getSortedIntervals().size() - 1).getEnd();
    }

    private List<Interval> getSortedIntervals() {
        return getIntervals().stream().sorted((i1, i2) -> i1.getStart().compareTo(i2.getStart())).collect(Collectors.toList());
    }
}
