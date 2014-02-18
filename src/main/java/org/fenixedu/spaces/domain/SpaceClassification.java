package org.fenixedu.spaces.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.exception.SpaceException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class SpaceClassification extends SpaceClassification_Base {

    public SpaceClassification(String code, LocalizedString name, SpaceClassification parent, JsonElement metadataSpec) {
        super();
        validateCode(code);
        setCode(code);
        setName(name);
        setParent(null);
        if (parent == null) {
            setBennu(Bennu.getInstance());
        }
        setMetadataSpec(metadataSpec);
    }

    public SpaceClassification(String code, LocalizedString name) {
        this(code, name, null, null);
    }

    private void validateChildCode(String code) {
        if (getCode().equals(code)) {
            throw new SpaceException("error.space.classfication.must.be.unique");
        }
        for (SpaceClassification classification : getChildrenSet()) {
            if (classification.getCode().equals(code)) {
                throw new SpaceException("error.space.classfication.must.be.unique");
            }
        }
    }

    private void validateCode(String code) {
        for (SpaceClassification classification : Bennu.getInstance().getRootClassificationSet()) {
            classification.validateChildCode(code);
        }
    }

    private void dump(List<SpaceClassification> classifications) {
        classifications.add(this);
        for (SpaceClassification classification : getChildrenSet()) {
            classification.dump(classifications);
        }
    }

    public static List<SpaceClassification> all() {
        final List<SpaceClassification> classifications = new ArrayList<>();
        for (SpaceClassification classification : Bennu.getInstance().getRootClassificationSet()) {
            classification.dump(classifications);
        }
        return classifications;
    }

    @Override
    public JsonElement getMetadataSpec() {
        JsonElement metadataSpec = super.getMetadataSpec();
        if (metadataSpec == null && getParent() != null) {
            metadataSpec = getParent().getMetadataSpec();
        }
        if (metadataSpec == null) {
            return new JsonArray();
        }
        return metadataSpec;
    }

    public MetadataSpec getMetadataSpec(String field) {
        for (MetadataSpec spec : getMetadataSpecs()) {
            if (spec.getName().equals(field)) {
                return spec;
            }
        }
        return null;
    }

    public Collection<MetadataSpec> getMetadataSpecs() {
        List<MetadataSpec> specs = new ArrayList<>();
        for (JsonElement metadataSpec : getMetadataSpec().getAsJsonArray()) {
            specs.add(new MetadataSpec(metadataSpec));
        }
        return specs;
    }

    public void setMetadataSpecs(Collection<MetadataSpec> specs) {
        JsonArray specsJson = new JsonArray();
        for (MetadataSpec spec : specs) {
            specsJson.add(spec.toJson());
        }
        setMetadataSpec(specsJson);
    }

}
