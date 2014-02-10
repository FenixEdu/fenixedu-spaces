package org.fenixedu.spaces.domain;

import java.math.BigDecimal;
import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.gson.JsonElement;

public class Information extends Information_Base {

    public static final Comparator<Information> CREATION_DATE_COMPARATOR = new Comparator<Information>() {

        @Override
        public int compare(Information o1, Information o2) {
            return o1.getCreated().compareTo(o2.getCreated());
        }

    };

    protected Information() {
        super();
        setCreated(new DateTime());
        setDeleted(null);
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

    public Information copy() {
        Information clone = new Information();
        clone.setAllocatableCapacity(getAllocatableCapacity());
        clone.setBlueprintNumber(getBlueprintNumber());
        clone.setArea(getArea());
        clone.setName(getName());
        clone.setIdentification(getIdentification());
        clone.setValidFrom(getValidFrom());
        clone.setValidUntil(getValidUntil());
        return clone;
    }

    public void delete() {
        setDeleted(new DateTime());
    }

    private Interval getEnabledInterval() {
        return new Interval(getCreated(), getDeleted() != null ? getDeleted() : new DateTime(Long.MAX_VALUE));
    }

    private boolean isEnabled(DateTime when) {
        return getEnabledInterval().contains(when);
    }

    private boolean isEnabled(Interval when) {
        return getEnabledInterval().overlaps(when);
    }

    private boolean isValid(DateTime when) {
        return new Interval(getValidFrom(), getValidUntil()).contains(when);
    }

    private boolean isValid(Interval when) {
        return new Interval(getValidFrom(), getValidUntil()).overlaps(when);
    }

    public boolean exists(DateTime when, DateTime enabledWhen) {
        return isEnabled(enabledWhen) && isValid(when);
    }

    public boolean exists(Interval when, Interval enabledWhen) {
        return isEnabled(enabledWhen) && isValid(when);
    }

}
