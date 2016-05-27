package org.fenixedu.spaces.ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.FenixEduSpaceConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.ui.InformationBean;
import org.fenixedu.spaces.ui.SpaceClassificationBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
            super(Status.INTERNAL_SERVER_ERROR, FenixEduSpaceConfiguration.BUNDLE, label, message);
            this.kind = kind;
            this.message = message;
        }

        @Override
        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            String finalMessage = getLocalizedMessage();
            if (!message.isEmpty()) {
                finalMessage += " : " + message;
            }
            json.addProperty(kind, finalMessage);
            return json;
        }
    }

    public void verifyClassification(SpaceClassificationBean bean) throws SpaceClassificationException {
        JsonArray jsarray = bean.getMetadataSpec().getAsJsonArray();
        Map<String, Boolean> hasName = new HashMap<String, Boolean>();
        for (JsonElement jsonElement : jsarray) {
            JsonObject jo = jsonElement.getAsJsonObject();
            String name = jo.get("name").getAsString();
            String type = jo.get("type").getAsString();

            Class<?> typeClass;
            try {
                typeClass = Class.forName(type);
            } catch (ClassNotFoundException e1) {
                throw new SpaceClassificationException("error", "label.spaceClassification.noSuchClass", type);
            }
            String defaultValue = jo.get("defaultValue").getAsString();
            if (hasName.containsKey(name) == true) {
                throw new SpaceClassificationException("error", "label.spaceClassification.duplicatedKey", name);
            } else {
                hasName.put(name, true);
            }
            if (defaultValue.isEmpty()) {
                continue;
            }
            try {
                if (Integer.class.isAssignableFrom(typeClass) && !defaultValue.isEmpty()) {
                    Integer.parseInt(defaultValue);
                    continue;
                }
            } catch (Exception e) {
                throw new SpaceClassificationException("error", "label.spaceClassification.typeMismatch", type + " ( "
                        + defaultValue + " --> Number )");
            }
            if (Boolean.class.isAssignableFrom(typeClass) && !defaultValue.isEmpty()) {
                if (Boolean.parseBoolean(defaultValue.trim()) != true && !defaultValue.trim().equalsIgnoreCase("false")) {
                    throw new SpaceClassificationException("error", "label.spaceClassification.typeMismatch", type + " ( "
                            + defaultValue + " --> Boolean )");
                }
                continue;
            }
            if (DateTime.class.isAssignableFrom(typeClass) && !defaultValue.isEmpty()) {
                try {

                    DateTime.parse(defaultValue.trim(), DateTimeFormat.forPattern(InformationBean.DATE_FORMAT));
                } catch (Exception e) {
                    throw new SpaceClassificationException("error", "label.spaceClassification.typeMismatch", type + " ( "
                            + defaultValue + " --> Date )");
                }
                continue;
            }
            if (String.class.isAssignableFrom(typeClass)) {
                continue;
            }
            throw new SpaceClassificationException("error", "label.spaceClassification.noSuchClass", type);

        }
    }

    public void updateClassification(SpaceClassification classification, SpaceClassificationBean bean) {
        setMetadataSpec(classification, bean.getMetadataSpec());
        setName(classification, bean.getLocalizedName());
        setCode(classification, bean.getCode());
        setParentClassification(classification, bean.getParent());
        setIsAllocatable(classification, bean.getIsAllocatable());
    }

    @Atomic
    public void setMetadataSpec(SpaceClassification classification, JsonElement metadata) {
        classification.setMetadataSpec(metadata);
    }

    @Atomic
    public void setIsAllocatable(SpaceClassification classification, boolean isAllocatable) {
        classification.setIsAllocatable(isAllocatable);
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
        } else {
            if (!classification.isRootClassification()) {
                throw new SpaceClassificationException("error", "label.spaceClassification.mustSelectParent", "");
            }
            classification.setParent(parentClassification);
            return;
        }
        // verify that this classification is not in its parent chain
        SpaceClassification loopParents = parentClassification;
        List<SpaceClassification> listParentClassifications = new ArrayList<SpaceClassification>();
        while (loopParents != null) {
            listParentClassifications.add(loopParents);
            if (loopParents.equals(classification)) {
                throw new SpaceClassificationException("error", "label.spaceClassification.isInParentChain", "");
            }
            loopParents = loopParents.getParent();
        }
        // verify that no children of this classification are in its parent chain
        List<SpaceClassification> listChildClassifications = classification.getAllChildren();

        if (Collections.disjoint(listChildClassifications, listParentClassifications) == false) {
            throw new SpaceClassificationException("error", "label.spaceClassification.isInParentChain", "");
        }
        classification.setParent(parentClassification);
    }
}
