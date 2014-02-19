package net.sourceforge.fenixedu.domain.space;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.joda.time.YearMonthDay;

public class Blueprint extends Blueprint_Base implements Comparable<Blueprint> {

//    @FenixDomainObjectActionLogAnnotation(actionName = "Created space blueprint", parameters = { "space", "person" })
    public Blueprint(Space space, User person) {
        super();
        setRootDomainObject(Bennu.getInstance());
        checkNewBluePrintDates(space);
        closeCurrentSpaceBlueprint(space);
        setSpace(space);
        setCreationPerson(person);
        super.setValidFrom(new YearMonthDay());
    }

//    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted space blueprint", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToManageBlueprints);

        Space space = getSpace();
        refreshBlueprintsDates(space);
        super.setSpace(null);
        super.setCreationPerson(null);
        setBlueprintFile(null);
        setRootDomainObject(null);
        openCurrentSpaceBlueprint(space);
        deleteDomainObject();
    }

    private void refreshBlueprintsDates(Space space) {
        SortedSet<Blueprint> blueprints = new TreeSet<Blueprint>(space.getBlueprints());
        if (!blueprints.isEmpty() && blueprints.last() != this) {
            for (Iterator<Blueprint> iter = blueprints.iterator(); iter.hasNext();) {
                Blueprint blueprint = iter.next();
                if (blueprint == this) {
                    Blueprint nextBlueprint = iter.next();
                    nextBlueprint.updateValidFromDate(blueprint.getValidFrom());
                    break;
                }
            }
        }
    }

    private void closeCurrentSpaceBlueprint(Space space) {
        SortedSet<Blueprint> blueprints = new TreeSet<Blueprint>(space.getBlueprints());
        if (!blueprints.isEmpty()) {
            blueprints.last().closeBlueprint();
        }
    }

    private void openCurrentSpaceBlueprint(Space space) {
        SortedSet<Blueprint> blueprints = new TreeSet<Blueprint>(space.getBlueprints());
        if (!blueprints.isEmpty()) {
            blueprints.last().openBlueprint();
        }
    }

    private void openBlueprint() {
        super.setValidUntil(null);
    }

    private void closeBlueprint() {
        super.setValidUntil(new YearMonthDay());
    }

    private void updateValidFromDate(YearMonthDay yearMonthDay) {
        super.setValidFrom(yearMonthDay);
    }

    @Override
    public void setValidFrom(YearMonthDay validFrom) {
        throw new SpaceDomainException("error.blueprint.invalid.validFrom.date");
    }

    @Override
    public void setValidUntil(YearMonthDay validUntil) {
        throw new SpaceDomainException("error.blueprint.invalid.validUntil.date");
    }

    @Override
    public void setCreationPerson(User creationPerson) {
        if (creationPerson == null) {
            throw new SpaceDomainException("error.blueprint.no.person");
        }
        super.setCreationPerson(creationPerson);
    }

    @Override
    public void setSpace(Space space) {
        if (space == null) {
            throw new SpaceDomainException("error.blueprint.no.space");
        }
        super.setSpace(space);
    }

    private void checkNewBluePrintDates(Space space) {
        Blueprint mostRecentBlueprint = space.getMostRecentBlueprint();
        if (mostRecentBlueprint != null && mostRecentBlueprint.getValidFrom().isEqual(new YearMonthDay())) {
            throw new SpaceDomainException("error.blueprint.validFrom.date.already.exists");
        }
    }

    @Override
    public int compareTo(Blueprint blueprint) {
        if (getValidUntil() == null) {
            return 1;
        } else if (blueprint.getValidUntil() == null) {
            return -1;
        } else {
            return getValidUntil().compareTo(blueprint.getValidUntil());
        }
    }

    @Deprecated
    public boolean hasValidFrom() {
        return getValidFrom() != null;
    }

    @Deprecated
    public boolean hasBennu() {
        return getRootDomainObject() != null;
    }

    @Deprecated
    public boolean hasBlueprintFile() {
        return getBlueprintFile() != null;
    }

    @Deprecated
    public boolean hasValidUntil() {
        return getValidUntil() != null;
    }

    @Deprecated
    public boolean hasCreationPerson() {
        return getCreationPerson() != null;
    }

    @Deprecated
    public boolean hasSpace() {
        return getSpace() != null;
    }

}
