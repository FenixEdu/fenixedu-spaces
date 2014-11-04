package org.fenixedu.spaces.ui.services;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.ui.SpaceClassificationBean;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class SpaceClassificationService {

    public SpaceClassificationService() {

    }

    private static class SpaceClassificationException extends DomainException {

        String kind;
        String message;

        protected SpaceClassificationException(String kind, String label, String message) {
            super(Status.INTERNAL_SERVER_ERROR, "resources/FenixEduSpacesResources", label, message);
            this.kind = kind;
            this.message = message;
        }

        @Override
        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            json.addProperty(kind, getLocalizedMessage() + " : " + message);
            return json;
        }
    }

    public void verifyClassification(SpaceClassificationBean bean) {
        JsonArray jsarray = bean.getMetadataSpec().getAsJsonArray();
        Map<String, Boolean> hasName = new HashMap<String, Boolean>();
        for (JsonElement jsonElement : jsarray) {
            JsonObject jo = jsonElement.getAsJsonObject();
            String name = jo.get("name").getAsString();
            String type = jo.get("type").getAsString();
            String defaultValue = jo.get("defaultValue").getAsString();
            if (hasName.containsKey(name) == true) {
                throw new SpaceClassificationException("error", "label.spaceClassification.duplicatedKey", name);
            } else {
                hasName.put(name, true);
            }
            try {
                if (type.equals("java.lang.Integer") && !defaultValue.isEmpty()) {
                    Integer.parseInt(defaultValue);
                }
            } catch (Exception e) {
                throw new SpaceClassificationException("error", "label.spaceClassification.typeMismatch", name + " ( "
                        + defaultValue + " --> Number )");
            }
            if (type.equals("java.lang.Boolean") && !defaultValue.isEmpty()) {
                if (Boolean.parseBoolean(defaultValue.trim()) != true && !defaultValue.trim().equalsIgnoreCase("false")) {
                    throw new SpaceClassificationException("error", "label.spaceClassification.typeMismatch", name + " ( "
                            + defaultValue + " --> Boolean )");
                }
            }
        }
    }

    public void updateClassification(SpaceClassification classification, SpaceClassificationBean bean) {
        setMetadataSpec(classification, bean.getMetadataSpec());
        setName(classification, bean.getLocalizedName());
        setCode(classification, bean.getCode());
        setParentClassification(classification, bean.getParent());
    }

    @Atomic
    public void setMetadataSpec(SpaceClassification classification, JsonElement metadata) {
        classification.setMetadataSpec(metadata);
    }

    @Atomic
    public void setName(SpaceClassification classification, LocalizedString name) {
        classification.setName(name);
    }

    @Atomic
    public void setCode(SpaceClassification classification, String code) {
        classification.setCode(code);
    }

    @Atomic
    public void setParentClassification(SpaceClassification classification, String parent) {
        SpaceClassification parentClassification = null;
        if (parent.length() > 0) {
            parentClassification = FenixFramework.getDomainObject(parent);
        }

        classification.setParent(parentClassification);
    }
}
