package org.fenixedu.spaces.json.adapters;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.spaces.domain.Space;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(Space.class)
public class SpaceJsonAdapter implements JsonViewer<Space> {
    @Override
    public JsonElement view(Space space, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("name", space.getFullName());
        object.addProperty("id", space.getExternalId());
        return object;
    }
}
