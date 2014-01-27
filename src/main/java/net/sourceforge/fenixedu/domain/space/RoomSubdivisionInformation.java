package net.sourceforge.fenixedu.domain.space;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.space.Building.SpaceFactoryExecutor;
import net.sourceforge.fenixedu.domain.space.RoomSubdivision.RoomSubdivisionFactoryEditor;

import org.apache.commons.lang.StringUtils;
import org.joda.time.YearMonthDay;

public class RoomSubdivisionInformation extends RoomSubdivisionInformation_Base {

//    @FenixDomainObjectActionLogAnnotation(actionName = "Created roomSubdivision information", parameters = { "identification",
//            "roomSubdivision", "begin", "end" })
    public RoomSubdivisionInformation(String identification, RoomSubdivision roomSubdivision, YearMonthDay begin, YearMonthDay end) {
        super();
        super.setSpace(roomSubdivision);
        setIdentification(identification);
        setFirstTimeInterval(begin, end);
    }

//    @FenixDomainObjectActionLogAnnotation(actionName = "Edited roomSubdivision information", parameters = { "identification",
//            "begin", "end" })
    public void editRoomSubdivisionCharacteristics(String identification, YearMonthDay begin, YearMonthDay end) {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToManageSpaceInformation);
        editTimeInterval(begin, end);
        setIdentification(identification);
    }

    @Override
//    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted roomSubdivision information", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToManageSpaceInformation);
        super.delete();
    }

    @Override
    public void setIdentification(String identification) {
        if (StringUtils.isEmpty(identification)) {
            throw new SpaceDomainException("error.RoomSubdivisionInformation.empty.identification");
        }
        super.setIdentification(identification);
    }

    @Override
    public void setSpace(final Space space) {
        throw new SpaceDomainException("error.incompatible.space");
    }

    public void setSpace(final RoomSubdivision roomSubdivision) {
        throw new SpaceDomainException("error.cannot.change.roomSubdivision");
    }

    @Override
    public String getPresentationName() {
        return getIdentification();
    }

    @Override
    public SpaceFactoryExecutor getSpaceFactoryEditor() {
        final RoomSubdivisionFactoryEditor roomFactoryEditor = new RoomSubdivisionFactoryEditor();
        roomFactoryEditor.setIdentification(getIdentification());
        roomFactoryEditor.setSpace((RoomSubdivision) getSpace());
        roomFactoryEditor.setBegin(getNextPossibleValidFromDate());
        return roomFactoryEditor;
    }

    @Override
    public RoomClassification getRoomClassification() {
        return getSpace().getSuroundingSpace().getSpaceInformation().getRoomClassification();
    }

    @Deprecated
    public boolean hasIdentification() {
        return getIdentification() != null;
    }

}
