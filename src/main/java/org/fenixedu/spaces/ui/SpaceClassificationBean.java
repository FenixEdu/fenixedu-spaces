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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SpaceClassificationBean {

    private String name;
    private String metadata;
    private JsonElement metadataSpec;
    private JsonElement LocalizedName;

    private String parent;
    private String code;

//JsonElement = new JsonParser.parse(jsonString); 
    public SpaceClassificationBean(String name, String metadata) {
        this.name = name;
        this.metadata = metadata;
        setMetadataSpec(metadata);
        setLocalizezdName(name);
    }

    public SpaceClassificationBean() {
        super();
        this.metadata = "[]";
        setMetadataSpec(metadata);
        LocalizedName = new LocalizedString().json();
        this.parent = null;
        this.name = LocalizedName.toString();
    }

    public String getName() {
        return this.name;
    }

    public LocalizedString getLocalizedName() {
        return new LocalizedString().fromJson(this.LocalizedName);
    }

    public void setName(String name) {
        setLocalizezdName(name);
        this.name = name;
        return;
    }

    public void setMetadata(String metadata) {
        setMetadataSpec(metadata);
        this.metadata = metadata;
        return;
    }

    public String getMetadata() {
        return this.metadata;
    }

    private void setLocalizezdName(String name) {
        this.LocalizedName = new JsonParser().parse(name);
    }

    private void setMetadataSpec(String metadata) {
        this.metadataSpec = new JsonParser().parse(metadata);
        return;
    }

    public JsonElement getMetadataSpec() {
        return this.metadataSpec;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parentClassificationOID) {
        this.parent = parentClassificationOID;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
