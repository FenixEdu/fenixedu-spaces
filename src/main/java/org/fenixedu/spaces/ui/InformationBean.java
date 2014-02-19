package org.fenixedu.spaces.ui;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.fenixedu.spaces.domain.BlueprintFile;
import org.fenixedu.spaces.domain.MetadataSpec;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.io.BaseEncoding;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime validFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime validUntil;
    private SpaceClassification classification;
    private Map<String, String> metadata;
    private String externalId;
    private BlueprintFile blueprint;
    private MultipartFile blueprintMultipartFile;

    private static Gson gson = new Gson();

    public InformationBean() {
        super();
        this.validFrom = new DateTime();
        this.metadata = new HashMap<>();
    }

    public InformationBean(String externalId, Integer allocatableCapacity, String blueprintNumber, BigDecimal area, String name,
            String identification, DateTime validFrom, DateTime validUntil, JsonElement metadata,
            SpaceClassification classification, BlueprintFile blueprint) {
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

    public MultipartFile getBlueprintMultipartFile() {
        return blueprintMultipartFile;
    }

    public void setBlueprintMultipartFile(MultipartFile blueprintMultipartFile) {
        this.blueprintMultipartFile = blueprintMultipartFile;
    }

    public byte[] getBlueprintContent() {
        try {
            return getBlueprintMultipartFile().getBytes();
        } catch (IOException e) {
            return null;
        }
    }

    public String getBlueprintBase64() {
        return BaseEncoding.base64Url().encode(getRawBlueprintContent());
    }

    private byte[] getRawBlueprintContent() {
        return blueprint == null ? new byte[0] : blueprint.getContent();
    }

}
