package org.fenixedu.spaces.domain;

import java.math.BigDecimal;
import java.util.SortedSet;

import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.gson.JsonElement;

public class Space extends Space_Base {

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

        Builder() {
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

    }

    /**
     * get the most recent space information
     * 
     * @return
     */
    public Information getInformation() {
        return getInformation(new DateTime());
    }

    /**
     * get the most recent space information valid at the specified datetime.
     * 
     * @param when
     * @return
     */

    public Information getInformation(DateTime when) {
        return getInformation(when, new DateTime());
    }

    /**
     * get the space information valid at the specified when date, created on atWhatDate.
     * 
     * @param when
     * @param atWhatDate
     * @return
     */

    public Information getInformation(final DateTime when, final DateTime creationDate) {
        final SortedSet<Information> information = getInternalInformation(when, creationDate);
        return information.isEmpty() ? null : information.last();
    }

    private SortedSet<Information> getInternalInformation(final DateTime when, final DateTime creationDate) {

        return FluentIterable.from(getInformationSet()).filter(new Predicate<Information>() {

            @Override
            public boolean apply(Information info) {
                return info.exists(when, creationDate);
            }
        }).toSortedSet(Information.CREATION_DATE_COMPARATOR);
    }

}
