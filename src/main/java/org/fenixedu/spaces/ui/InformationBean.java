package org.fenixedu.spaces.ui;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.fenixedu.spaces.domain.MetadataSpec;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class InformationBean {

    private Integer allocatableCapacity;
    private String blueprintNumber;
    private BigDecimal area;
    private String name;
    private String identification;
    private DateTime validFrom;
    private DateTime validUntil;
    private SpaceClassification classification;
    private Map<String, String> metadata;
    private String externalId;

    private static String FORMAT = "yyyy-MM-dd";

    private static Gson gson = new Gson();

    public InformationBean() {
        super();
        this.validFrom = new DateTime();
        this.metadata = new HashMap<>();
    }

    public InformationBean(String externalId, Integer allocatableCapacity, String blueprintNumber, BigDecimal area, String name,
            String identification, DateTime validFrom, DateTime validUntil, JsonElement metadata,
            SpaceClassification classification) {
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

    public String getValidFrom() {
        return validFrom == null ? null : validFrom.toString(FORMAT);
    }

    public DateTime getRawValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = Strings.isNullOrEmpty(validFrom) ? null : new DateTime(validFrom);
    }

    public String getValidUntil() {
        return validUntil == null ? null : validUntil.toString(FORMAT);
    }

    public DateTime getRawValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = Strings.isNullOrEmpty(validUntil) ? null : new DateTime(validUntil);
    }

    public String getClassification() {
        return classification != null ? classification.getExternalId() : null;
    }

    public SpaceClassification getRawClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = FenixFramework.getDomainObject(classification);
    }

    public void setClassification(SpaceClassification classification) {
        this.classification = classification;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @SuppressWarnings("unchecked")
    public void setMetadata(JsonElement metadata) {
        this.metadata = gson.fromJson(metadata, HashMap.class);
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    private static JsonElement convert(Class<?> type, String value) {
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

        throw new UnsupportedOperationException(String.format("Can't convert value %s to type %s", value, type.getName()));
    }

    public JsonElement getRawMetadata() {
        JsonObject json = new JsonObject();
        for (MetadataSpec spec : classification.getMetadataSpecs()) {
            final String name = spec.getName();
            if (metadata.containsKey(name)) {
                json.add(name, convert(spec.getType(), metadata.get(name)));
            } else {
                if (spec.isRequired()) {
                    json.add(name, convert(spec.getType(), spec.getDefaultValue()));
                }
            }
        }
        return json;
    }

}
