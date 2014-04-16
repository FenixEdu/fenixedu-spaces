package org.fenixedu.spaces.domain.occupation.requests;

import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.spaces.domain.SpaceDomainException;
import org.joda.time.DateTime;

import pt.ist.fenixframework.DomainObject;

public class OccupationStateInstant extends OccupationStateInstant_Base {

    public static final Comparator<OccupationStateInstant> COMPARATOR_BY_INSTANT = new ComparatorChain();
    private static final Comparator<DomainObject> COMPARATOR_BY_ID = new Comparator<DomainObject>() {
        @Override
        public int compare(DomainObject o1, DomainObject o2) {
            return o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    static {
        ((ComparatorChain) COMPARATOR_BY_INSTANT).addComparator(new BeanComparator("instant"));
        ((ComparatorChain) COMPARATOR_BY_INSTANT).addComparator(COMPARATOR_BY_ID);
    }

    public OccupationStateInstant(OccupationRequest request, OccupationRequestState state, DateTime instant) {
//        check(this, ResourceAllocationRolePredicates.checkPermissionsToManageOccupationStateInstants);
        super();
        setRootDomainObject(Bennu.getInstance());
        setRequest(request);
        setRequestState(state);
        setInstant(instant);
    }

    @jvstm.cps.ConsistencyPredicate
    protected boolean checkRequiredParameters() {
        return getRequestState() != null && getInstant() != null;
    }

    @Override
    public void setRequest(OccupationRequest request) {
        if (request == null) {
            throw new SpaceDomainException("error.OccupationStateInstant.empty.request");
        }
        super.setRequest(request);
    }

    @Override
    public void setInstant(DateTime instant) {
        if (instant == null) {
            throw new SpaceDomainException("error.OccupationStateInstant.empty.instant");
        }
        super.setInstant(instant);
    }

}
