package org.fenixedu.spaces.migration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.ui.InformationBean;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.CallableWithoutException;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

@SuppressWarnings("unused")
public class ImportSpacesTask extends CustomTask {

    private static final Logger logger = LoggerFactory.getLogger(ImportSpacesTask.class);

    private static final Map<String, String> codes = new HashMap<String, String>() {
        {
            put("Campus", "10");
            put("Room", "11");
            put("RoomSubdivision", "12");
            put("Building", "13");
            put("Floor", "14");
        }
    };

    @Override
    public TxMode getTxMode() {
        return TxMode.READ;
    };

    private static String dealWithDates(YearMonthDay yearMonthDay) {
        return yearMonthDay == null ? null : yearMonthDay.toString("dd/MM/yyyy");
    }

    private static class IntervalBean {
        public String start;
        public String end;

        public IntervalBean(String start, String end) {
            super();
            this.start = start;
            this.end = end;
        }

    }

    private static class ImportOccupationBean {

        public String description;
        public String title;
        public String frequency;
        public String beginDate;
        public String endDate;
        public String beginTime;
        public String endTime;
        public Boolean saturday;
        public Boolean sunday;
        public Set<IntervalBean> intervals;
        public Set<String> spaces;

        public ImportOccupationBean(String description, String title, String frequency, String beginDate, String endDate,
                String beginTime, String endTime, Boolean saturday, Boolean sunday, Set<String> spaces,
                Set<IntervalBean> intervals) {
            super();
            this.description = description;
            this.title = title;
            this.frequency = frequency;
            this.beginDate = beginDate;
            this.endDate = endDate;
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.saturday = saturday;
            this.sunday = sunday;
            this.spaces = spaces;
            this.intervals = intervals;
        }

    }

    public class SpaceBean {
        public String parentExternalId;
        public String externalId;
        public String createdOn;
        public Integer examCapacity;
        public Integer normalCapacity;
        public String type;
        public Set<SpaceInformationBean> informations;
        public Set<BlueprintBean> blueprints;

        public class BlueprintBean {
            public String validFrom;
            public String validUntil;
            public String creationPerson;
            public String raw;
        }

        public class SpaceInformationBean {
            public Integer capacity;
            public String blueprintNumber;
            public String validFrom;
            public String validUntil;
            public String emails;
            public Boolean ageQuality;
            public BigDecimal area;
            public String description;
            public Boolean distanceFromSanitaryInstalationsQuality;
            public String doorNumber;
            public Boolean heightQuality;
            public String identification;
            public Boolean illuminationQuality;
            public String observations;
            public Boolean securityQuality;
            public String name;
        }

        private DateTime dealWithDates(String datetime) {
            if (datetime == null) {
                return null;
            }
            return DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(datetime);
        }

        public Set<InformationBean> beans() {
            return FluentIterable.from(informations).transform(new Function<SpaceInformationBean, InformationBean>() {

                @Override
                public InformationBean apply(SpaceInformationBean input) {
                    InformationBean bean = new InformationBean();
                    bean.setAllocatableCapacity(input.capacity);
                    bean.setBlueprintNumber(input.blueprintNumber);
                    bean.setValidFrom(dealWithDates(input.validFrom));
                    bean.setValidUntil(dealWithDates(input.validUntil));
                    bean.setArea(input.area);
                    bean.setIdentification(input.identification);
                    bean.setClassification(getCode(type));
                    bean.setName(input.name);
                    if (type == "11") { //room 
                        bean.setMetadata(createMetadata(input));
                    }
                    return bean;
                }

                private Map<String, String> createMetadata(SpaceInformationBean bean) {
                    Map<String, String> metadata = new HashMap<>();

                    metadata.put("ageQualitity", dealWithBooleans(bean.ageQuality));
                    metadata.put("heightQuality", dealWithBooleans(bean.heightQuality));
                    metadata.put("illuminationQuality", dealWithBooleans(bean.illuminationQuality));
                    metadata.put("securityQuality", dealWithBooleans(bean.securityQuality));
                    metadata.put("distanceFromSanitaryInstalationsQuality",
                            dealWithBooleans(bean.distanceFromSanitaryInstalationsQuality));
                    metadata.put("doorNumber", bean.doorNumber);
                    return metadata;
                }

                public String dealWithBooleans(Boolean bool) {
                    return bool == null ? "false" : bool.toString();
                }

                private SpaceClassification getCode(String type) {
                    final SpaceClassification spaceClassification = SpaceClassification.get(codes.get(type));
                    if (spaceClassification == null) {
                        throw new RuntimeException("code doesnt exist.");
                    }
                    return spaceClassification;
                }

            }).toSet();
        }
    }

    Map<SpaceBean, Space> beanToSpaceMap = new HashMap<>();
    Map<String, SpaceBean> idToBeansMap = new HashMap<>();
    List<SpaceBean> fromJson;

    @Override
    public void runTask() throws Exception {
        Gson gson = new Gson();
        processSpaces(gson);
    }

    public void processOccupations(Gson gson) throws FileNotFoundException {
        File file = new File("/home/sfbs/Downloads/occupations.json");
        final List<ImportOccupationBean> fromJson =
                gson.fromJson(new JsonReader(new FileReader(file)), new TypeToken<List<ImportOccupationBean>>() {
                }.getType());

        for (ImportOccupationBean importOccupationBean : fromJson) {
        }

    }

    public void processSpaces(Gson gson) throws FileNotFoundException {
        File file = new File("/home/sfbs/Downloads/spaces2.json");
        final List<SpaceBean> fromJson = gson.fromJson(new JsonReader(new FileReader(file)), new TypeToken<List<SpaceBean>>() {
        }.getType());

        for (SpaceBean spaceBean : fromJson) {
            idToBeansMap.put(spaceBean.externalId, spaceBean);
        }

        for (SpaceBean spaceBean : fromJson) {
            process(spaceBean);
        }
    }

    private Space process(final SpaceBean spaceBean) {
        if (spaceBean == null) {
            taskLog("processing null");
            return null;
        }
        if (!beanToSpaceMap.containsKey(spaceBean)) {
            taskLog("add to map [%s] %s\n", spaceBean.parentExternalId, spaceBean.externalId);
            beanToSpaceMap.put(spaceBean, create(process(idToBeansMap.get(spaceBean.parentExternalId)), spaceBean));
        }
        taskLog("retrieve from map %s\n", spaceBean.externalId);
        return beanToSpaceMap.get(spaceBean);
    }

    private Space create(final Space parent, final SpaceBean spaceBean) {
        return FenixFramework.getTransactionManager().withTransaction(new CallableWithoutException<Space>() {

            @Override
            public Space call() {
                return innerCreate(parent, spaceBean);
            }

        });
    }

    private Space innerCreate(Space parent, SpaceBean spaceBean) {
        Space space = new Space(parent, null);
        for (InformationBean infoBean : spaceBean.beans()) {
            infoBean.getMetadata().put("examCapacity", spaceBean.examCapacity == null ? null : spaceBean.examCapacity.toString());
            infoBean.getMetadata().put("normalCapacity",
                    spaceBean.normalCapacity == null ? null : spaceBean.normalCapacity.toString());
            space.bean(infoBean);
        }
        return space;
    }
}