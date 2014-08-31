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

    private boolean hasMatchByName(final String needle) {
        final LocalizedString name = getName();
        for (final Locale locale : name.getLocales()) {
            if (needle.equals(name.getContent(locale))) {
                return true;
            }
        }
        return false;
    }

    private SpaceClassification findByName(final String needle) {
        return hasMatchByName(needle) ? this : findByName(needle, getChildrenSet());
    }

    private static SpaceClassification findByName(final String needle, Collection<SpaceClassification> classifications) {
        for (final SpaceClassification classification : classifications) {
            final SpaceClassification result = classification.findByName(needle);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static SpaceClassification getByName(String needle) {
        return findByName(needle, Bennu.getInstance().getRootClassificationSet());
    }

}
