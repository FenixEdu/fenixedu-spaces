package net.sourceforge.fenixedu.domain.resource;

import java.util.Set;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;

import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.YearMonthDay;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public abstract class Resource extends Resource_Base {

    protected Resource() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public void delete() {
        if (!canBeDeleted()) {
            throw new SpaceDomainException("error.resource.cannot.be.deleted");
        }
        setRootDomainObject(null);
        deleteDomainObject();
    }

    private boolean canBeDeleted() {
        //TODO: transform into write transaction, on error abort.
//        return !hasAnyResourceAllocations() && !hasAnyResourceResponsibility();
        return true;
    }

    public boolean isSpace() {
        return false;
    }

    public boolean isVehicle() {
        return false;
    }

    public boolean isMaterial() {
        return false;
    }

    public boolean isCampus() {
        return false;
    }

    public boolean isBuilding() {
        return false;
    }

    public boolean isFloor() {
        return false;
    }

    public boolean isRoom() {
        return false;
    }

    public boolean isRoomSubdivision() {
        return false;
    }

    public boolean isExtension() {
        return false;
    }

    public boolean isFireExtinguisher() {
        return false;
    }

    public boolean isAllocatableSpace() {
        return false;
    }

    @Deprecated
    public java.util.Set<net.sourceforge.fenixedu.domain.resource.ResourceAllocation> getResourceAllocations() {
        return getResourceAllocationsSet();
    }

    @Deprecated
    public boolean hasAnyResourceAllocations() {
        return !getResourceAllocationsSet().isEmpty();
    }

    @Deprecated
    public boolean hasBennu() {
        return getRootDomainObject() != null;
    }

    public Set<ResourceResponsibility> getActiveResourceResponsibility() {
        return FluentIterable.from(getResourceResponsibilitySet()).filter(new Predicate<ResourceResponsibility>() {
            private final YearMonthDay now;

            {
                now = new YearMonthDay();
            }

            @Override
            public boolean apply(ResourceResponsibility input) {
                return input.isActive(now);
            }
        }).toSet();
    }

}
