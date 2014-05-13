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
