package org.fenixedu.spaces.domain.occupation.requests;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.SpaceDomainException;
import org.joda.time.DateTime;

import pt.ist.fenixframework.DomainObject;

public class OccupationComment extends OccupationComment_Base {

    public static final Comparator<OccupationComment> COMPARATOR_BY_INSTANT = new ComparatorChain();
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

    public OccupationComment(OccupationRequest request, String subject, String description, User owner, DateTime instant) {
//        check(this, ResourceAllocationRolePredicates.checkPermissionsToManageOccupationComments);

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
//        check(this, ResourceAllocationRolePredicates.checkPermissionsToManageOccupationComments);
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
