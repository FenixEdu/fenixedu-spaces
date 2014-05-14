package org.fenixedu.spaces.domain.occupation.requests;

import java.util.Comparator;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.spaces.domain.SpaceDomainException;
import org.joda.time.DateTime;

public class OccupationStateInstant extends OccupationStateInstant_Base {

    public static final Comparator<OccupationStateInstant> COMPARATOR_BY_INSTANT = new Comparator<OccupationStateInstant>() {
        @Override
        public int compare(OccupationStateInstant o1, OccupationStateInstant o2) {
            int o = o1.getInstant().compareTo(o2.getInstant());
            return o != 0 ? o : o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    public OccupationStateInstant(OccupationRequest request, OccupationRequestState state, DateTime instant) {
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
