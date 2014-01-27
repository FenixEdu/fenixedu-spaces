package net.sourceforge.fenixedu.domain.space;

import java.io.Serializable;
import java.util.Comparator;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.space.Building.SpaceFactoryExecutor;
import net.sourceforge.fenixedu.util.DomainObjectUtil;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.joda.time.YearMonthDay;

public class Floor extends Floor_Base {

    public final static Comparator<Floor> FLOOR_COMPARATOR_BY_LEVEL = new ComparatorChain();
    static {
        ((ComparatorChain) FLOOR_COMPARATOR_BY_LEVEL).addComparator(new ReverseComparator(new BeanComparator(
                "spaceInformation.level")));
        ((ComparatorChain) FLOOR_COMPARATOR_BY_LEVEL).addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
    }

    public Floor(Space suroundingSpace, Integer level, YearMonthDay begin, YearMonthDay end, String blueprintNumber) {

        super();
        setSuroundingSpace(suroundingSpace);
        new FloorInformation(this, level, begin, end, blueprintNumber);
    }

    @Override
//    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted floor", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkPermissionsToManageSpace);
        super.delete();
    }

    @Override
    public void setSuroundingSpace(Space suroundingSpace) {
        if (suroundingSpace == null || suroundingSpace.isCampus() || suroundingSpace.isRoomSubdivision()) {
            throw new SpaceDomainException("error.Space.invalid.suroundingSpace");
        }
        super.setSuroundingSpace(suroundingSpace);
    }

    @Override
    public FloorInformation getSpaceInformation() {
        return (FloorInformation) super.getSpaceInformation();
    }

    @Override
    public FloorInformation getSpaceInformation(final YearMonthDay when) {
        return (FloorInformation) super.getSpaceInformation(when);
    }

    @Override
    public boolean isFloor() {
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

    public static abstract class FloorFactory implements Serializable, SpaceFactoryExecutor {
        private Integer level;

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

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getBlueprintNumber() {
            return blueprintNumber;
        }

        public void setBlueprintNumber(String blueprintNumber) {
            this.blueprintNumber = blueprintNumber;
        }
    }

    public static class FloorFactoryCreator extends FloorFactory {

        private Space surroundingSpaceReference;

        public Space getSurroundingSpace() {
            return surroundingSpaceReference;
        }

        public void setSurroundingSpace(Space surroundingSpace) {
            if (surroundingSpace != null) {
                this.surroundingSpaceReference = surroundingSpace;
            }
        }

        @Override
        public Floor execute() {
            return new Floor(getSurroundingSpace(), getLevel(), getBegin(), getEnd(), getBlueprintNumber());
        }
    }

    public static class FloorFactoryEditor extends FloorFactory {

        private Floor floorReference;

        public Floor getSpace() {
            return floorReference;
        }

        public void setSpace(Floor floor) {
            if (floor != null) {
                this.floorReference = floor;
            }
        }

        @Override
        public FloorInformation execute() {
            return new FloorInformation(getSpace(), getLevel(), getBegin(), getEnd(), getBlueprintNumber());
        }

    }
}
