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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.spaces.domain.BlueprintFile;
import org.fenixedu.spaces.domain.MetadataSpec;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.domain.submission.SpacePhoto;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class InformationBean {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private Integer allocatableCapacity;
    private String blueprintNumber;
    private BigDecimal area;
    private String name;
    private String identification;
    @DateTimeFormat(pattern = DATE_FORMAT)
    private DateTime validFrom;
    @DateTimeFormat(pattern = DATE_FORMAT)
    private DateTime validUntil;
    private SpaceClassification classification;
    private Map<String, String> metadata;
    private String externalId;
    private BlueprintFile blueprint;
    private MultipartFile blueprintMultipartFile;
    private Set<SpacePhoto> spacePhotoSet;
    private MultipartFile spacePhotoMultipartFile;
    private User user;

    private static Gson gson = new Gson();

    public InformationBean() {
        super();
        this.validFrom = new DateTime();
        this.metadata = new HashMap<>();
        this.user = Authenticate.getUser() != null ? Authenticate.getUser() : null;
    }

    @Deprecated
    public InformationBean(String externalId, Integer allocatableCapacity, String blueprintNumber, BigDecimal area, String name,
            String identification, DateTime validFrom, DateTime validUntil, JsonElement metadata,
            SpaceClassification classification, BlueprintFile blueprint, User user) {
        this(externalId, allocatableCapacity, blueprintNumber, area, name, identification, validFrom, validUntil, metadata,
                classification, blueprint, null, user);
    }

    public InformationBean(String externalId, Integer allocatableCapacity, String blueprintNumber, BigDecimal area, String name,
            String identification, DateTime validFrom, DateTime validUntil, JsonElement metadata,
            SpaceClassification classification, BlueprintFile blueprint, Set<SpacePhoto> spacePhotoSet, User user) {
        super();
        this.externalId = externalId;
        this.allocatableCapacity = allocatableCapacity;
        this.blueprintNumber = blueprintNumber;
        this.area = area;
        this.name = name;
        this.identification = identification;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.classification = classification;
        this.blueprint = blueprint;
        this.spacePhotoSet = spacePhotoSet;
        this.user = user;
        setMetadata(metadata);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getAllocatableCapacity() {
        return allocatableCapacity;
    }

    public void setAllocatableCapacity(Integer allocatableCapacity) {
        this.allocatableCapacity = allocatableCapacity;
    }

    public String getBlueprintNumber() {
        return blueprintNumber;
    }

    public void setBlueprintNumber(String blueprintNumber) {
        this.blueprintNumber = blueprintNumber;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public DateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(DateTime validFrom) {
        this.validFrom = validFrom;
    }

    public DateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(DateTime validUntil) {
        this.validUntil = validUntil;
    }

    public SpaceClassification getClassification() {
        return classification;
    }

    public void setClassification(SpaceClassification classification) {
        this.classification = classification;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonElement metadata) {
        final Map<String, String> meta = new HashMap<>();
        if (metadata != null) {
            JsonObject json = metadata.getAsJsonObject();
            for (Entry<String, JsonElement> entry : json.entrySet()) {
                meta.put(entry.getKey(), entry.getValue().isJsonNull() ? null : entry.getValue().getAsString());
            }
        }
        setMetadata(meta);
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private static JsonElement convert(Class<?> type, String value) {
        if (value == null) {
            return null;
        }

        if (String.class.isAssignableFrom(type)) {
            return new JsonPrimitive(value);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return new JsonPrimitive(Boolean.parseBoolean(value));
        }
        if (Integer.class.isAssignableFrom(type)) {
            return new JsonPrimitive(Integer.parseInt(value));
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return new JsonPrimitive(new BigDecimal(value));
        }

        return new JsonPrimitive(value);
    }

    public JsonElement getRawMetadata() {
        JsonObject json = new JsonObject();
        if (classification != null) {
            for (MetadataSpec spec : classification.getMetadataSpecs()) {
                final String name = spec.getName();
                if (metadata.containsKey(name)) {
                    json.add(name, convert(spec.getType(), getValue(name)));
                } else {
                    if (spec.isRequired()) {
                        json.add(name, convert(spec.getType(), spec.getDefaultValue()));
                    }
                }
            }
        }
        return json;
    }

    private String getValue(final String name) {
        Object value = metadata.get(name);
        return value == null ? null : value.toString();
    }

    public MultipartFile getBlueprintMultipartFile() {
        return blueprintMultipartFile;
    }

    public void setBlueprintMultipartFile(MultipartFile blueprintMultipartFile) {
        this.blueprintMultipartFile = blueprintMultipartFile;
    }

    public void setBlueprint(BlueprintFile file) {
        this.blueprint = file;
    }

    public BlueprintFile getBlueprint() {
        return this.blueprint;
    }

    public byte[] getBlueprintContent() {
        try {
            if (getBlueprintMultipartFile() != null && !getBlueprintMultipartFile().isEmpty()) {
                return getBlueprintMultipartFile().getBytes();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public MultipartFile getSpacePhotoMultipartFile() {
        return spacePhotoMultipartFile;
    }

    public void setSpacePhotoMultipartFile(MultipartFile spacePhotoMultipartFile) {
        this.spacePhotoMultipartFile = spacePhotoMultipartFile;
    }

    public void setSpacePhotoSet(Set<SpacePhoto> files) {
        this.spacePhotoSet = files;
    }

    public Set<SpacePhoto> getSpacePhotoSet() {
        return this.spacePhotoSet;
    }

    public byte[] getSpacePhotoContent() {
        try {
            if (getSpacePhotoMultipartFile() != null && !getSpacePhotoMultipartFile().isEmpty()) {
                return getSpacePhotoMultipartFile().getBytes();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

}
