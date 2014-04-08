package org.fenixedu.spaces.domain.occupation.requests;

import java.util.Comparator;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.util.DomainObjectUtil;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;

public class OccupationStateInstant extends OccupationStateInstant_Base {

    public static final Comparator<OccupationStateInstant> COMPARATOR_BY_INSTANT = new ComparatorChain();
    static {
        ((ComparatorChain) COMPARATOR_BY_INSTANT).addComparator(new BeanComparator("instant"));
        ((ComparatorChain) COMPARATOR_BY_INSTANT).addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
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
