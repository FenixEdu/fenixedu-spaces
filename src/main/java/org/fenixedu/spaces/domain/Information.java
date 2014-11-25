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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.ui.InformationBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Information extends Information_Base {

    public static final Comparator<Information> CREATION_DATE_COMPARATOR = new Comparator<Information>() {

        @Override
        public int compare(Information o1, Information o2) {
            return o1.getCreated().compareTo(o2.getCreated());
        }

    };

    public static class Builder {
        private Integer allocatableCapacity;
        private String blueprintNumber;
        private BigDecimal area;
        private String name;
        private String identification;
        private JsonElement metadata;
        private DateTime validFrom;
        private DateTime validUntil;
        private SpaceClassification classification;
        private String externalId;
        private BlueprintFile blueprint;
        private byte[] blueprintContent;
        private User user;

        //create information from the info in the bean
        public Builder(InformationBean informationBean) {
            this.allocatableCapacity = informationBean.getAllocatableCapacity();
            this.blueprintNumber = informationBean.getBlueprintNumber();
            this.area = informationBean.getArea();
            this.name = informationBean.getName();
            this.identification = informationBean.getIdentification();
            this.metadata = informationBean.getRawMetadata();
            this.validFrom = informationBean.getValidFrom();
            this.validUntil = informationBean.getValidUntil();
            this.classification = informationBean.getClassification();
            this.blueprintContent = informationBean.getBlueprintContent();
            this.blueprint = informationBean.getBlueprint();
            this.user = informationBean.getUser();
        }

        //create information based on existing information
        Builder(Information information) {
            this.allocatableCapacity = information.getAllocatableCapacity();
            this.blueprintNumber = information.getBlueprintNumber();
            this.area = information.getArea();
            this.name = information.getName();
            this.identification = information.getIdentification();
            this.metadata = information.getMetadata();
            this.validFrom = information.getValidFrom();
            this.validUntil = information.getValidUntil();
            this.classification = information.getClassification();
            this.externalId = information.getExternalId();
            this.blueprint = information.getBlueprint();
            this.user = information.getUser();
        }

        public Builder() {
        }

        public Builder allocatableCapacity(Integer allocatableCapacity) {
            this.allocatableCapacity = allocatableCapacity;
            return this;
        }

        public Builder blueprintNumber(String blueprintNumber) {
            this.blueprintNumber = blueprintNumber;
            return this;
        }

        public Builder area(BigDecimal area) {
            this.area = area;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder identification(String identification) {
            this.identification = identification;
            return this;
        }

        public Builder metadata(JsonElement metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder validFrom(DateTime validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public Builder validUntil(DateTime validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public Builder classification(SpaceClassification classification) {
            this.classification = classification;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        @Atomic(mode = TxMode.WRITE)
        public Information build() {
            if (blueprintContent == null) {
                return new Information(validFrom, validUntil, allocatableCapacity, blueprintNumber, area, name, identification,
                        metadata, classification, blueprint, user);
            }
            return new Information(validFrom, validUntil, allocatableCapacity, blueprintNumber, area, name, identification,
                    metadata, classification, blueprintContent, user);
        }

        public InformationBean bean() {
            return new InformationBean(externalId, allocatableCapacity, blueprintNumber, area, name, identification, validFrom,
                    validUntil, metadata, classification, blueprint, user);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Information information) {
        return new Builder(information);
    }

    public static Builder builder(InformationBean bean) {
        return new Builder(bean);
    }

    protected Information() {
        this(null);
    }

    protected Information(Information previous) {
        super();
        setCreated(new DateTime());
        setPrevious(previous);
    }

    private Information(DateTime validFrom, DateTime validUntil, Integer allocatableCapacity, String blueprintNumber,
            BigDecimal area, String name, String identification, JsonElement metadata, SpaceClassification classification,
            byte[] blueprint, BlueprintFile blueprintFile, User user) {
        setValidFrom(validFrom);
        setValidUntil(validUntil);
        setAllocatableCapacity(allocatableCapacity);
        setBlueprintNumber(blueprintNumber);
        setArea(area);
        setName(name);
        setIdentification(identification);
        setClassification(classification);
        setMetadata(metadata);
        if (blueprintFile != null) {
            setBlueprint(blueprintFile);
        }
        if (blueprint != null) {
            setBlueprint(new BlueprintFile(name, blueprint));
        }
        setUser(user);
    }

    protected Information(DateTime validFrom, DateTime validUntil, Integer allocatableCapacity, String blueprintNumber,
            BigDecimal area, String name, String identification, JsonElement metadata, SpaceClassification classification,
            byte[] blueprint, User user) {
        this(validFrom, validUntil, allocatableCapacity, blueprintNumber, area, name, identification, metadata, classification,
                blueprint, null, user);
    }

    protected Information(DateTime validFrom, DateTime validUntil, Integer allocatableCapacity, String blueprintNumber,
            BigDecimal area, String name, String identification, JsonElement metadata, SpaceClassification classification,
            BlueprintFile blueprintFile, User user) {
        this(validFrom, validUntil, allocatableCapacity, blueprintNumber, area, name, identification, metadata, classification,
                null, blueprintFile, user);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> Optional<T> getMetadata(String field) {
        Optional<MetadataSpec> spec = getClassification().getMetadataSpec(field);
        if (spec.isPresent()) {
            final Class<?> type = spec.get().getType();
            final JsonObject metadata = getMetadata().getAsJsonObject();

            JsonElement fieldValue = metadata.get(field);

            if (fieldValue == null || fieldValue.isJsonNull()) {
                return Optional.empty();
            }

            if (Boolean.class.isAssignableFrom(type)) {
                return (Optional<T>) Optional.of(new Boolean(fieldValue.getAsBoolean()));
            }
            if (Integer.class.isAssignableFrom(type)) {
                return (Optional<T>) Optional.of(new Integer(fieldValue.getAsInt()));
            }
            if (BigDecimal.class.isAssignableFrom(type)) {
                return (Optional<T>) Optional.of(fieldValue.getAsBigDecimal());
            }

            return (Optional<T>) Optional.of(new String(fieldValue.getAsString()));
        }
        return Optional.empty();
    }

    protected Information copy() {
        Information clone = new Information();
        clone.setAllocatableCapacity(getAllocatableCapacity());
        clone.setBlueprintNumber(getBlueprintNumber());
        clone.setArea(getArea());
        clone.setName(getName());
        clone.setIdentification(getIdentification());
        clone.setValidFrom(getValidFrom());
        clone.setValidUntil(getValidUntil());
        clone.setMetadata(getMetadata());
        clone.setClassification(getClassification());
        clone.setPrevious(null);
        clone.setBlueprint(getBlueprint());
        return clone;
    }

    protected Information keepLeft(DateTime checkpoint) {
        Information copy = copy();
        copy.setValidUntil(checkpoint);
        return copy;
    }

    protected Information keepRight(DateTime checkpoint) {
        Information copy = copy();
        copy.setValidFrom(checkpoint);
        return copy;
    }

    protected List<Information> split(DateTime checkpoint) {
        return Lists.newArrayList(keepLeft(checkpoint), keepRight(checkpoint));
    }

    protected boolean contains(DateTime checkpoint) {
        return getValidity().contains(checkpoint);
    }

    protected boolean contains(Interval checkpoint) {
        return getValidity().contains(checkpoint);
    }

    /**
     * Does requested checkpoint is after current validity period
     *
     * @param checkpoint
     * @return
     */
    protected boolean isAfter(DateTime checkpoint) {
        if (checkpoint == null) {
            return true;
        }
        return checkpoint.isEqual(getValidUntil()) || checkpoint.isAfter(getValidUntil());
    }

    protected Interval getValidity() {
        return new Interval(getValidFrom(), getValidUntil() == null ? new DateTime(Long.MAX_VALUE) : getValidUntil());
    }

    @Override
    public void setPrevious(Information previous) {
        if (previous == this) {
            throw new UnsupportedOperationException();
        }
        super.setPrevious(previous);
    }

}
