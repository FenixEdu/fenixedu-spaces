package net.sourceforge.fenixedu.domain.resource;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.YearMonthDay;

public abstract class ResourceResponsibility extends ResourceResponsibility_Base {

    protected ResourceResponsibility() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public void delete() {
        super.setResource(null);
        super.setRootDomainObject(null);
        deleteDomainObject();
    }

    public boolean isActive(YearMonthDay currentDate) {
        return getBegin() != null && (!getBegin().isAfter(currentDate) && (getEnd() == null || !getEnd().isBefore(currentDate)));
    }

    public boolean isSpaceResponsibility() {
        return false;
    }

    public boolean isMaterialResponsibility() {
        return false;
    }

    @Override
    public void setResource(Resource resource) {
        if (resource == null) {
            throw new SpaceDomainException("error.ResourceResponsibility.empty.resource");
        }
        super.setResource(resource);
    }

    @jvstm.cps.ConsistencyPredicate
    protected boolean checkDateInterval() {
        final YearMonthDay start = getBegin();
        final YearMonthDay end = getEnd();
        return start != null && (end == null || !start.isAfter(end));
    }

    @Deprecated
    public boolean hasEnd() {
        return getEnd() != null;
    }

    @Deprecated
    public boolean hasResource() {
        return getResource() != null;
    }

    @Deprecated
    public boolean hasBennu() {
        return getRootDomainObject() != null;
    }

    @Deprecated
    public boolean hasBegin() {
        return getBegin() != null;
    }

}
