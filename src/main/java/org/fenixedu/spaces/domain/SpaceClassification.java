package org.fenixedu.spaces.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class SpaceClassification extends SpaceClassification_Base {

    public SpaceClassification(String code, LocalizedString name, SpaceClassification parent, JsonElement metadataSpec) {
        super();
        setCode(code);
        setName(name);
        setParent(parent);
        if (parent == null) {
            setBennu(Bennu.getInstance());
        }
        setMetadataSpec(metadataSpec);
    }

    public SpaceClassification(String code, LocalizedString name, SpaceClassification parent) {
        this(code, name, parent, new JsonArray());
    }

    public SpaceClassification(String code, LocalizedString name) {
        this(code, name, null);
    }

    public static SpaceClassification get(String code) {
        String parentCode = null;
        String childCode = null;
        if (code.indexOf(".") != -1) {
            String[] subCodes = code.split("\\.");
            parentCode = subCodes[0];
            childCode = subCodes[1];
        } else {
            parentCode = code;
        }
        for (SpaceClassification classification : Bennu.getInstance().getRootClassificationSet()) {
            if (classification.getCode().equals(parentCode)) {
                if (childCode != null) {
                    return classification.getChild(childCode);
                }
                return classification;
            }
        }
        return null;
    }

    private SpaceClassification getChild(String code) {
        for (SpaceClassification child : getChildrenSet()) {
            if (child.getCode().equals(code)) {
                return child;
            }
        }
        return null;
    }

    public String getAbsoluteCode() {
        return getPath().stream().map(c -> c.getCode()).collect(Collectors.joining("."));
    }

    private List<SpaceClassification> getPath() {
        List<SpaceClassification> path = new ArrayList<>();
        SpaceClassification parent = this;
        while (parent != null) {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
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

    public Optional<MetadataSpec> getMetadataSpec(String field) {
        for (MetadataSpec spec : getMetadataSpecs()) {
            if (spec.getName().equals(field)) {
                return Optional.of(spec);
            }
        }
        return Optional.empty();
    }

    public Collection<MetadataSpec> getMetadataSpecs() {
        List<MetadataSpec> specs = new ArrayList<>();
        for (JsonElement metadataSpec : getMetadataSpec().getAsJsonArray()) {
            specs.add(new MetadataSpec(metadataSpec));
        }
        return specs;
    }

    @Override
    public void setMetadataSpec(JsonElement metadataSpec) {
        if (metadataSpec == null) {
            super.setMetadataSpec(new JsonArray());
        } else {
            super.setMetadataSpec(metadataSpec);
        }
    }

    public void setMetadataSpecs(Collection<MetadataSpec> specs) {
        JsonArray specsJson = new JsonArray();
        for (MetadataSpec spec : specs) {
            specsJson.add(spec.toJson());
        }
        setMetadataSpec(specsJson);
    }

    public static SpaceClassification getCampusClassification() {
        final SpaceClassification byName = getByName("Campus");
        if (byName == null) {
            throw new UnsupportedOperationException("Campus type not defined.");
        }
        return byName;
    }

    public static SpaceClassification getByName(String needle) {
        for (SpaceClassification classification : all()) {
            final LocalizedString name = classification.getName();
            for (Locale locale : name.getLocales()) {
                if (needle.equals(name.getContent(locale))) {
                    return classification;
                }
            }
        }
        return null;
    }
}
