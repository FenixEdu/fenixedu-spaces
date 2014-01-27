package net.sourceforge.fenixedu.domain.space;

import net.sourceforge.fenixedu.domain.FrequencyType;
import net.sourceforge.fenixedu.domain.GenericEvent;
import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.util.DiaSemana;
import net.sourceforge.fenixedu.util.HourMinuteSecond;

import org.fenixedu.bennu.core.domain.groups.Group;
import org.joda.time.YearMonthDay;

public class GenericEventSpaceOccupation extends GenericEventSpaceOccupation_Base {

    public GenericEventSpaceOccupation(AllocatableSpace allocatableSpace, GenericEvent genericEvent) {
//        check(this, SpacePredicates.checkPermissionsToManageGenericEventSpaceOccupations);

        super();

        setGenericEvent(genericEvent);

        if (allocatableSpace != null && !allocatableSpace.isFree(this)) {
            throw new SpaceDomainException("error.roomOccupation.room.is.not.free");
        }

        setResource(allocatableSpace);
    }

    @Override
    public void delete() {
//        check(this, SpacePredicates.checkPermissionsToManageGenericEventSpaceOccupations);
        super.setGenericEvent(null);
        super.delete();
    }

    public void verifyIfIsPossibleCloseGenericEvent() {
//        check(this, SpacePredicates.checkPermissionsToManageGenericEventSpaceOccupations);
    }

    @Override
    public void setGenericEvent(GenericEvent genericEvent) {
        if (genericEvent == null) {
            throw new SpaceDomainException("error.GenericEventSpaceOccupation.empty.genericEvent");
        }
        super.setGenericEvent(genericEvent);
    }

    @Override
    public boolean isGenericEventSpaceOccupation() {
        return true;
    }

    @Override
    public Group getAccessGroup() {
        return getSpace().getGenericEventOccupationsAccessGroupWithChainOfResponsibility();
    }

    @Override
    public FrequencyType getFrequency() {
        return getGenericEvent().getFrequency();
    }

    @Override
    public YearMonthDay getBeginDate() {
        return getGenericEvent() == null ? null : getGenericEvent().getBeginDate();
    }

    @Override
    public YearMonthDay getEndDate() {
        return getGenericEvent().getEndDate();
    }

    @Override
    public Boolean getDailyFrequencyMarkSaturday() {
        return getGenericEvent().getDailyFrequencyMarkSaturday();
    }

    @Override
    public Boolean getDailyFrequencyMarkSunday() {
        return getGenericEvent().getDailyFrequencyMarkSunday();
    }

    @Override
    public HourMinuteSecond getStartTimeDateHourMinuteSecond() {
        return getGenericEvent().getStartTimeDateHourMinuteSecond();
    }

    @Override
    public HourMinuteSecond getEndTimeDateHourMinuteSecond() {
        return getGenericEvent().getEndTimeDateHourMinuteSecond();
    }

    @Override
    public DiaSemana getDayOfWeek() {
        return null;
    }

    // TODO: move to fenix
//    @Override
//    public boolean isOccupiedByExecutionCourse(ExecutionCourse executionCourse, DateTime start, DateTime end) {
//        return false;
//    }

    @Override
    public String getPresentationString() {
        return getGenericEvent().getGanttDiagramEventName().getContent();
    }

    @Deprecated
    public boolean hasGenericEvent() {
        return getGenericEvent() != null;
    }

}
