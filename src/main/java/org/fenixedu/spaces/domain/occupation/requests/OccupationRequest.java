package org.fenixedu.spaces.domain.occupation.requests;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.space.Campus;
import net.sourceforge.fenixedu.util.DomainObjectUtil;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;

import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

public class OccupationRequest extends OccupationRequest_Base {

    public static final Comparator<OccupationRequest> COMPARATOR_BY_IDENTIFICATION = new BeanComparator("identification");
    public static final Comparator<OccupationRequest> COMPARATOR_BY_INSTANT = new ComparatorChain();
    public static final Comparator<OccupationRequest> COMPARATOR_BY_MORE_RECENT_COMMENT_INSTANT = new ComparatorChain();
    static {
        ((ComparatorChain) COMPARATOR_BY_MORE_RECENT_COMMENT_INSTANT).addComparator(
                new BeanComparator("moreRecentCommentInstant"), true);
        ((ComparatorChain) COMPARATOR_BY_MORE_RECENT_COMMENT_INSTANT).addComparator(DomainObjectUtil.COMPARATOR_BY_ID);

        ((ComparatorChain) COMPARATOR_BY_INSTANT).addComparator(new BeanComparator("instant"), true);
        ((ComparatorChain) COMPARATOR_BY_INSTANT).addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
    }

    public OccupationRequest(User requestor, MultiLanguageString subject, Space campus, MultiLanguageString description) {
//        check(this, ResourceAllocationRolePredicates.checkPermissionsToManageOccupationRequests);
        super();
        checkIfRequestAlreadyExists(requestor, subject, description);
        setRootDomainObject(Bennu.getInstance());
        setRequestor(requestor);
        DateTime now = new DateTime();
        setInstant(now);
        setCampus(campus);
        addStateInstants(new OccupationStateInstant(this, OccupationRequestState.NEW, now));
        addComment(new OccupationComment(this, subject, description, requestor, now));
        setTeacherReadComments(1);
        setEmployeeReadComments(0);
        setIdentification(getNextRequestIdentification());
    }

    @jvstm.cps.ConsistencyPredicate
    protected boolean checkRequiredParameters() {
        return getInstant() != null && getIdentification() != null;
    }

    public Integer getNumberOfNewComments(User person) {
        if (person.equals(getOwner())) {
            return getCommentSet().size() - getEmployeeReadComments();
        } else if (person.equals(getRequestor())) {
            return getCommentSet().size() - getTeacherReadComments();
        }
        return Integer.valueOf(0);
    }

    public DateTime getMoreRecentCommentInstant() {
        SortedSet<OccupationComment> result = new TreeSet<OccupationComment>(OccupationComment.COMPARATOR_BY_INSTANT);
        result.addAll(getCommentSet());
        return result.last().getInstant();
    }

    public void createNewTeacherOrEmployeeComment(MultiLanguageString description, User commentOwner, DateTime instant) {
        new OccupationComment(this, getCommentSubject(), description, commentOwner, instant);
        if (commentOwner.equals(getRequestor())) {
            setTeacherReadComments(getCommentSet().size());
        } else {
            setOwner(commentOwner);
            setEmployeeReadComments(getCommentSet().size());
        }
    }

    public void createNewTeacherCommentAndOpenRequest(MultiLanguageString description, User commentOwner, DateTime instant) {
        openRequestWithoutAssociateOwner(instant);
        new OccupationComment(this, getCommentSubject(), description, commentOwner, instant);
        setTeacherReadComments(getCommentSet().size());
    }

    public void createNewEmployeeCommentAndCloseRequest(MultiLanguageString description, User commentOwner, DateTime instant) {
        new OccupationComment(this, getCommentSubject(), description, commentOwner, instant);
        closeRequestWithoutAssociateOwner(instant);
        setOwner(commentOwner);
        setEmployeeReadComments(getCommentSet().size());
    }

    public void closeRequestAndAssociateOwnerOnlyForEmployees(DateTime instant, User person) {
        closeRequestWithoutAssociateOwner(instant);
        if (!getOwner().equals(person)) {
            setEmployeeReadComments(0);
            setOwner(person);
        }
    }

    public void openRequestAndAssociateOwnerOnlyForEmployess(DateTime instant, User person) {
        openRequestWithoutAssociateOwner(instant);
        if (getOwner() == null || !getOwner().equals(person)) {
            setEmployeeReadComments(0);
            setOwner(person);
        }
    }

    private void closeRequestWithoutAssociateOwner(DateTime instant) {
        if (!getCurrentState().equals(OccupationRequestState.RESOLVED)) {
            addStateInstants(new OccupationStateInstant(this, OccupationRequestState.RESOLVED, instant));
        }
    }

    private void openRequestWithoutAssociateOwner(DateTime instant) {
        if (!getCurrentState().equals(OccupationRequestState.OPEN)) {
            addStateInstants(new OccupationStateInstant(this, OccupationRequestState.OPEN, instant));
        }
    }

    private MultiLanguageString getCommentSubject() {
        StringBuilder subject = new StringBuilder();
        subject.append("Re: ");
        OccupationComment firstComment = getFirstComment();
        if (firstComment != null) {
            subject.append(firstComment.getSubject().getContent());
        }
        return new MultiLanguageString(subject.toString());
    }

    @Override
    public void setOwner(User owner) {
        if (owner == null || !owner.equals(getRequestor())) {
            super.setOwner(owner);
        }
    }

    @Override
    public void setIdentification(Integer identification) {
        if (identification == null) {
            throw new SpaceDomainException("error.OccupationRequest.empty.identification");
        }
        super.setIdentification(identification);
    }

    @Override
    public void setRequestor(User requestor) {
        if (requestor == null) {
            throw new SpaceDomainException("error.OccupationRequest.empty.requestor");
        }
        super.setRequestor(requestor);
    }

    @Override
    public void setInstant(DateTime instant) {
        if (instant == null) {
            throw new SpaceDomainException("error.OccupationRequest.empty.instant");
        }
        super.setInstant(instant);
    }

    public String getPresentationInstant() {
        return getInstant().toString("dd/MM/yyyy HH:mm");
    }

    public static Set<OccupationRequest> getRequestsByTypeOrderByDate(OccupationRequestState state, Campus campus) {
        Set<OccupationRequest> result = new TreeSet<OccupationRequest>(OccupationRequest.COMPARATOR_BY_INSTANT);
        for (OccupationRequest request : Bennu.getInstance().getOccupationRequestSet()) {
            if (request.getCurrentState().equals(state) && (request.getCampus() == null || request.getCampus().equals(campus))) {
                result.add(request);
            }
        }
        return result;
    }

    public static OccupationRequest getRequestById(Integer requestID) {
        for (OccupationRequest request : Bennu.getInstance().getOccupationRequestSet()) {
            if (request.getIdentification().equals(requestID)) {
                return request;
            }
        }
        return null;
    }

    public static Set<OccupationRequest> getResolvedRequestsOrderByMoreRecentComment(Campus campus) {
        Set<OccupationRequest> result =
                new TreeSet<OccupationRequest>(OccupationRequest.COMPARATOR_BY_MORE_RECENT_COMMENT_INSTANT);
        for (OccupationRequest request : Bennu.getInstance().getOccupationRequestSet()) {
            if (request.getCurrentState().equals(OccupationRequestState.RESOLVED)
                    && (request.getCampus() == null || request.getCampus().equals(campus))) {
                result.add(request);
            }
        }
        return result;
    }

    public static Set<OccupationRequest> getRequestsByTypeAndDiferentOwnerOrderByDate(OccupationRequestState state, User owner,
            Campus campus) {
        Set<OccupationRequest> result = new TreeSet<OccupationRequest>(OccupationRequest.COMPARATOR_BY_INSTANT);
        for (OccupationRequest request : Bennu.getInstance().getOccupationRequestSet()) {
            if (request.getCurrentState().equals(state) && (request.getOwner() == null || !request.getOwner().equals(owner))
                    && (request.getCampus() == null || request.getCampus().equals(campus))) {
                result.add(request);
            }
        }
        return result;
    }

    public OccupationComment getFirstComment() {
        for (OccupationComment comment : getCommentSet()) {
            if (comment.getInstant().isEqual(getInstant())) {
                return comment;
            }
        }
        return null;
    }

    public Set<OccupationComment> getCommentsWithoutFirstCommentOrderByDate() {
        Set<OccupationComment> result = new TreeSet<OccupationComment>(OccupationComment.COMPARATOR_BY_INSTANT);
        for (OccupationComment comment : getCommentSet()) {
            if (!comment.getInstant().isEqual(getInstant())) {
                result.add(comment);
            }
        }
        return result;
    }

    public String getSubject() {
        final OccupationComment firstComment = getFirstComment();
        final String content = firstComment != null ? firstComment.getSubject().getContent() : null;
        return content == null || content.isEmpty() ? getIdentification().toString() : content;
    }

    public String getDescription() {
        final OccupationComment firstComment = getFirstComment();
        final MultiLanguageString description = firstComment == null ? null : firstComment.getDescription();
        final String content = description == null ? null : description.getContent();
        return content == null ? getExternalId() : content;
    }

    public OccupationRequestState getCurrentState() {
        SortedSet<OccupationStateInstant> result =
                new TreeSet<OccupationStateInstant>(OccupationStateInstant.COMPARATOR_BY_INSTANT);

        result.addAll(getStateInstantsSet());
        return result.last().getRequestState();
    }

    public OccupationRequestState getState(DateTime instanTime) {
        if (instanTime == null) {
            return getCurrentState();
        } else {
            for (OccupationStateInstant stateInstant : getStateInstantsSet()) {
                if (stateInstant.getInstant().isEqual(instanTime)) {
                    return stateInstant.getRequestState();
                }
            }
        }
        return null;
    }

    private Integer getNextRequestIdentification() {
        SortedSet<OccupationRequest> result = new TreeSet<OccupationRequest>(OccupationRequest.COMPARATOR_BY_IDENTIFICATION);
        Collection<OccupationRequest> requests = Bennu.getInstance().getOccupationRequestSet();
        for (OccupationRequest request : requests) {
            if (!request.equals(this)) {
                result.add(request);
            }
        }
        return result.isEmpty() ? 1 : result.last().getIdentification() + 1;
    }

    private void checkIfRequestAlreadyExists(User requestor, MultiLanguageString subject, MultiLanguageString description) {
        Set<OccupationRequest> requests = requestor.getOccupationRequestSet();
        for (OccupationRequest request : requests) {
            OccupationComment firstComment = request.getFirstComment();
            if (firstComment != null && firstComment.getSubject() != null && firstComment.getSubject().compareTo(subject) == 0
                    && firstComment.getDescription() != null && firstComment.getDescription().compareTo(description) == 0) {
                throw new SpaceDomainException("error.OccupationRequest.request.already.exists");
            }
        }
    }

}
