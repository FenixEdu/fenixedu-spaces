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
package org.fenixedu.spaces.domain;

import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MetadataSpec {

    private String name;
    private LocalizedString description;
    private Class<?> type;
    private boolean required;
    private String defaultValue;

    public MetadataSpec(String name, LocalizedString description, Class<?> type, boolean required, String defaultValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public MetadataSpec(JsonElement json) {
        super();
        JsonObject obj = json.getAsJsonObject();
        setName(obj.get("name").getAsString());
        setDescription(LocalizedString.fromJson(obj.get("description")));
        try {
            setType(Class.forName(obj.get("type").getAsString()));
        } catch (ClassNotFoundException e) {
            setType(Object.class);
        }
        setRequired(obj.get("required").getAsBoolean());
        setDefaultValue(obj.get("defaultValue").getAsString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", getName());
        json.add("description", getDescription().json());
        json.addProperty("required", isRequired());
        json.addProperty("defaultValue", getDefaultValue());
        json.addProperty("type", getType().getName());
        return json;
    }
}
