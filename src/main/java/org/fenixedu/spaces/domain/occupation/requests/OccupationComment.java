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

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.SpaceDomainException;
import org.joda.time.DateTime;

public class OccupationComment extends OccupationComment_Base {

    public static final Comparator<OccupationComment> COMPARATOR_BY_INSTANT = new Comparator<OccupationComment>() {

        @Override
        public int compare(OccupationComment o1, OccupationComment o2) {
            int o = o1.getInstant().compareTo(o2.getInstant());
            return o != 0 ? o : o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    public OccupationComment(OccupationRequest request, String subject, String description, User owner, DateTime instant) {
        super();
        checkIfCommentAlreadyExists(owner, subject, description);
        setRootDomainObject(Bennu.getInstance());
        setRequest(request);
        setOwner(owner);
        setSubject(subject);
        setDescription(description);
        setInstant(instant);
    }

    public void edit(String subject, String description) {
        if (!getRequest().getCurrentState().equals(OccupationRequestState.NEW)) {
            throw new SpaceDomainException("error.OccupationRequest.impossible.edit");
        }
        setSubject(subject);
        setDescription(description);
    }

    @jvstm.cps.ConsistencyPredicate
    protected boolean checkRequiredParameters() {
        return getInstant() != null && getSubject() != null && !getSubject().isEmpty() && getDescription() != null
                && !getDescription().isEmpty();
    }

    public String getPresentationInstant() {
        return getInstant().toString("dd/MM/yyyy HH:mm");
    }

    public OccupationRequestState getState() {
        return getRequest().getState(getInstant());
    }

    @Override
    public void setInstant(DateTime instant) {
        if (instant == null) {
            throw new SpaceDomainException("error.OccupationComment.empty.instant");
        }
        super.setInstant(instant);
    }

    @Override
    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new SpaceDomainException("error.OccupationComment.empty.description");
        }
        super.setDescription(description);
    }

    @Override
    public void setSubject(String subject) {
        if (subject == null || subject.isEmpty()) {
            throw new SpaceDomainException("error.OccupationComment.empty.subject");
        }
        super.setSubject(subject);
    }

    @Override
    public void setRequest(OccupationRequest request) {
        if (request == null) {
            throw new SpaceDomainException("error.OccupationComment.empty.request");
        }
        super.setRequest(request);
    }

    @Override
    public void setOwner(User owner) {
        if (owner == null) {
            throw new SpaceDomainException("error.OccupationComment.empty.owner");
        }
        super.setOwner(owner);
    }

    private void checkIfCommentAlreadyExists(User owner, String subject, String description) {
        Collection<OccupationComment> comments = owner.getOccupationCommentSet();
        for (OccupationComment comment : comments) {
            if (comment.getDescription().compareTo(description) == 0) {
                throw new SpaceDomainException("error.OccupationComment.comment.already.exists");
            }
        }
    }

}
