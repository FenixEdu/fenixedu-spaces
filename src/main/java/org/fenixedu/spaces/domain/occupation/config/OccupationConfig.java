package org.fenixedu.spaces.domain.occupation.config;

import java.util.List;

import org.joda.time.Interval;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public abstract class OccupationConfig {

    private static final Gson gson = new Gson();

    public abstract List<Interval> getIntervals();

    public static OccupationConfig internalize(JsonElement json) {
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
}
