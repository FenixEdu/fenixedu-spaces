package org.fenixedu.spaces.domain;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.fenixedu.spaces.ui.InformationBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

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

        public Builder(InformationBean informationBean) {
            this.allocatableCapacity = informationBean.getAllocatableCapacity();
            this.blueprintNumber = informationBean.getBlueprintNumber();
            this.area = informationBean.getArea();
            this.name = informationBean.getName();
            this.identification = informationBean.getIdentification();
            this.metadata = informationBean.getRawMetadata();
            this.validFrom = informationBean.getRawValidFrom();
            this.validUntil = informationBean.getRawValidUntil();
            this.classification = informationBean.getRawClassification();
        }

        Builder(Information information) {
            this.allocatableCapacity = information.getAllocatableCapacity();
            this.blueprintNumber = information.getBlueprintNumber();
            this.area = information.getArea();
            this.name = information.getName();
            this.identification = information.getIdentification();
            this.metadata = information.getMetadata();
            this.validFrom = information.getValidFrom();
            this.validUntil = information.getValidUntil();
        }

        public Builder() {
            this.allocatableCapacity = null;
            this.blueprintNumber = null;
            this.area = null;
            this.name = null;
            this.identification = null;
            this.metadata = null;
            this.validFrom = null;
            this.validUntil = null;
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

        @Atomic(mode = TxMode.WRITE)
        public Information build() {
            return new Information(validFrom, validUntil, allocatableCapacity, blueprintNumber, area, name, identification,
                    metadata, classification);
        }

        public InformationBean bean() {
            return new InformationBean(allocatableCapacity, blueprintNumber, area, name, identification, validFrom, validUntil,
                    metadata, classification);
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

    protected Information(DateTime validFrom, DateTime validUntil, Integer allocatableCapacity, String blueprintNumber,
            BigDecimal area, String name, String identification, JsonElement metadata, SpaceClassification classification) {
        init(validFrom, validUntil, allocatableCapacity, blueprintNumber, area, name, identification, metadata, classification);
    }

    public void init(DateTime validFrom, DateTime validUntil) {
        setValidFrom(validFrom);
        setValidUntil(validUntil);
    }

    private Information init(DateTime validFrom, DateTime validUntil, Integer allocatableCapacity, String blueprintNumber,
            BigDecimal area, String name, String identification, JsonElement metadata, SpaceClassification classification) {
        init(validFrom, validUntil);
        setAllocatableCapacity(allocatableCapacity);
        setBlueprintNumber(blueprintNumber);
        setArea(area);
        setName(name);
        setIdentification(identification);
        setClassification(classification);
        setMetadata(metadata);
        return this;
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
        clone.setPrevious(null);
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
