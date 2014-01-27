package net.sourceforge.fenixedu.domain.space;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.space.Campus.CampusFactoryEditor;

import org.apache.commons.lang.StringUtils;
import org.joda.time.YearMonthDay;

public class CampusInformation extends CampusInformation_Base {

//    @FenixDomainObjectActionLogAnnotation(actionName = "Created campus information", parameters = { "campus", "name", "begin",
//            "end", "blueprintNumber" })
    public CampusInformation(Campus campus, String name, YearMonthDay begin, YearMonthDay end, String blueprintNumber) {
        super();
        super.setSpace(campus);
        setName(name);
        setBlueprintNumber(blueprintNumber);
        setFirstTimeInterval(begin, end);
    }

//    @FenixDomainObjectActionLogAnnotation(actionName = "Edited campus information", parameters = { "name", "begin", "end",
//            "blueprintNumber" })
    public void editCampusCharacteristics(String name, YearMonthDay begin, YearMonthDay end, String blueprintNumber) {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToEditSpaceInformation);
        setName(name);
        setBlueprintNumber(blueprintNumber);
        editTimeInterval(begin, end);
    }

    @Override
//    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted campus information", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToManageSpaceInformation);
        super.delete();
    }

    @Override
    public void setName(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new SpaceDomainException("error.campus.name.cannot.be.null");
        }
        super.setName(name);
    }

    @Override
    public void setSpace(final Space space) {
        throw new SpaceDomainException("error.incompatible.space");
    }

    public void setSpace(final Campus campus) {
        throw new SpaceDomainException("error.cannot.change.campus");
    }

    @Override
    public CampusFactoryEditor getSpaceFactoryEditor() {
        final CampusFactoryEditor campusFactoryEditor = new CampusFactoryEditor();
        campusFactoryEditor.setSpace((Campus) getSpace());
        campusFactoryEditor.setName(getName());
        campusFactoryEditor.setBegin(getNextPossibleValidFromDate());
        return campusFactoryEditor;
    }

    @Override
    public String getPresentationName() {
        return getName();
    }

    @Override
    public RoomClassification getRoomClassification() {
        // Necessary for Renderers
        return null;
    }

    @Deprecated
    public boolean hasName() {
        return getName() != null;
    }

    @Deprecated
    public boolean hasLocality() {
        return getLocality() != null;
    }

}
