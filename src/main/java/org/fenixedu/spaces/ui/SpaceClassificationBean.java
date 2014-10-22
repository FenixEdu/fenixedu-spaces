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

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.SpaceClassification;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpaceClassificationBean {

    private final JsonElement metadataSpec;
    private final LocalizedString localizedName;

    private String parent;
    private String code;

    private String warningMessage;

    public SpaceClassificationBean(SpaceClassification classification) {
        this.parent = classification.getParent() == null ? "" : classification.getParent().getExternalId();
        this.code = classification.getCode();
        this.localizedName = classification.getName();
        this.metadataSpec = classification.getMetadataSpec();
    }

    public SpaceClassificationBean(String json) {
        JsonObject classificationJson = new JsonParser().parse(json).getAsJsonObject();
        this.localizedName = LocalizedString.fromJson(classificationJson.get("name"));
        this.metadataSpec = classificationJson.get("metadata");
        this.parent = classificationJson.get("parent").getAsString();
        this.code = classificationJson.get("code").getAsString();
    }

    public SpaceClassificationBean() {
        super();
        this.localizedName = new LocalizedString();
        this.metadataSpec = new JsonArray();
        this.parent = SpaceClassification.getRootClassification().getExternalId();
        this.code = "";
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public JsonElement getMetadataSpec() {
        return metadataSpec;
    }

    public LocalizedString getLocalizedName() {
        return localizedName;
    }

}
