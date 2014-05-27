/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Spaces.
 *
 * FenixEdu Spaces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Spaces is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.
 */
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
