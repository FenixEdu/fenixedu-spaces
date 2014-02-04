package net.sourceforge.fenixedu.domain.space;

import java.io.Serializable;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.resource.Resource;
import net.sourceforge.fenixedu.domain.space.Building.SpaceFactoryExecutor;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.YearMonthDay;

public class Campus extends Campus_Base {

    private static final String ALAMEDA_UNIVERSITY_CODE = "1518";
    private static final String TAGUSPARK_UNIVERSITY_CODE = "1519";
    public static final String DEFAULT_UNIVERSITY_CODE = ALAMEDA_UNIVERSITY_CODE;

    public Campus(String name, YearMonthDay begin, YearMonthDay end, String blueprintNumber) {
        super();
        new CampusInformation(this, name, begin, end, blueprintNumber);
    }

    @Override
//    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted campus", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkPermissionsToManageSpace);
        super.delete();
    }

    @Override
    public void setSuroundingSpace(Space suroundingSpace) {
        throw new SpaceDomainException("error.Space.invalid.suroundingSpace");
    }

    @Override
    public CampusInformation getSpaceInformation() {
        return (CampusInformation) super.getSpaceInformation();
    }

    @Override
    public CampusInformation getSpaceInformation(final YearMonthDay when) {
        return (CampusInformation) super.getSpaceInformation(when);
    }

    public String getLocation() {
        return getSpaceInformation().hasLocality() ? getSpaceInformation().getLocality().getName() : null;
    }

    public String getName() {
        return getSpaceInformation().getName();
    }

    public static Campus readActiveCampusByName(String campusName) {
        for (Resource space : Bennu.getInstance().getResourcesSet()) {
            if (space.isCampus() && ((Campus) space).isActive()
                    && ((Campus) space).getSpaceInformation().getName().equals(campusName)) {
                return (Campus) space;
            }
        }
        return null;
    }

    public static Campus readCampusByName(String name) {
        for (Campus campus : Space.getAllCampus()) {
            if (campus.getName().equalsIgnoreCase(name)) {
                return campus;
            }
        }
        return null;
    }

    @Override
    public boolean isCampus() {
        return true;
    }

    @Override
    public Integer getExamCapacity() {
        // Necessary for Renderers
        return null;
    }

    @Override
    public Integer getNormalCapacity() {
        // Necessary for Renderers
        return null;
    }

    public static abstract class CampusFactory implements Serializable, SpaceFactoryExecutor {
        private String name;

        private YearMonthDay begin;

        private YearMonthDay end;

        private String blueprintNumber;

        public YearMonthDay getBegin() {
            return begin;
        }

        public void setBegin(YearMonthDay begin) {
            this.begin = begin;
        }

        public YearMonthDay getEnd() {
            return end;
        }

        public void setEnd(YearMonthDay end) {
            this.end = end;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBlueprintNumber() {
            return blueprintNumber;
        }

        public void setBlueprintNumber(String blueprintNumber) {
            this.blueprintNumber = blueprintNumber;
        }
    }

    public static class CampusFactoryCreator extends CampusFactory {
        @Override
        public Campus execute() {
            return new Campus(getName(), getBegin(), getEnd(), getBlueprintNumber());
        }
    }

    public static class CampusFactoryEditor extends CampusFactory {
        private Campus campusReference;

        public Campus getSpace() {
            return campusReference;
        }

        public void setSpace(Campus campus) {
            if (campus != null) {
                this.campusReference = campus;
            }
        }

        @Override
        public CampusInformation execute() {
            return new CampusInformation(getSpace(), getName(), getBegin(), getEnd(), getBlueprintNumber());
        }
    }

//    @Deprecated
//    public java.util.Set<net.sourceforge.fenixedu.domain.personnelSection.contracts.GiafProfessionalData> getGiafProfessionalDatas() {
//        return getGiafProfessionalDatasSet();
//    }
//
//    @Deprecated
//    public boolean hasAnyGiafProfessionalDatas() {
//        return !getGiafProfessionalDatasSet().isEmpty();
//    }
//
//    @Deprecated
//    public java.util.Set<net.sourceforge.fenixedu.domain.student.importation.DgesStudentImportationProcess> getDgesStudentImportationProcess() {
//        return getDgesStudentImportationProcessSet();
//    }
//
//    @Deprecated
//    public boolean hasAnyDgesStudentImportationProcess() {
//        return !getDgesStudentImportationProcessSet().isEmpty();
//    }
//
//    @Deprecated
//    public java.util.Set<net.sourceforge.fenixedu.domain.ExecutionDegree> getExecutionDegrees() {
//        return getExecutionDegreesSet();
//    }
//
//    @Deprecated
//    public boolean hasAnyExecutionDegrees() {
//        return !getExecutionDegreesSet().isEmpty();
//    }
//
//    @Deprecated
//    public java.util.Set<net.sourceforge.fenixedu.domain.candidacy.MeasurementTest> getMeasurementTests() {
//        return getMeasurementTestsSet();
//    }
//
//    @Deprecated
//    public boolean hasAnyMeasurementTests() {
//        return !getMeasurementTestsSet().isEmpty();
//    }
//
//    @Deprecated
//    public java.util.Set<net.sourceforge.fenixedu.domain.messaging.Announcement> getAnnouncements() {
//        return getAnnouncementsSet();
//    }
//
//    @Deprecated
//    public boolean hasAnyAnnouncements() {
//        return !getAnnouncementsSet().isEmpty();
//    }
//
//    @Deprecated
//    public java.util.Set<net.sourceforge.fenixedu.domain.organizationalStructure.Unit> getUnits() {
//        return getUnitsSet();
//    }
//
//    @Deprecated
//    public boolean hasAnyUnits() {
//        return !getUnitsSet().isEmpty();
//    }

}
