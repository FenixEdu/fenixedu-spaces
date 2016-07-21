package org.fenixedu.spaces.domain.submission;

import java.util.Comparator;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceDomainException;
import org.joda.time.DateTime;

public class SpacePhotoSubmission extends SpacePhotoSubmission_Base {
    
    public SpacePhotoSubmission(Space space, User submitor, SpacePhoto photo) {
        super();
        setSpace(space);
        setSpacePending(space);
        DateTime init = new DateTime();
        setCreated(init);
        setModified(init);
        setSubmitor(submitor);
        setPhoto(photo);
    }
    
    public static final Comparator<SpacePhotoSubmission> COMPARATOR_BY_INSTANT = new Comparator<SpacePhotoSubmission>() {

        @Override
        public int compare(SpacePhotoSubmission ps1, SpacePhotoSubmission ps2) {
            int ps = ps1.getCreated().compareTo(ps2.getCreated());
            return ps != 0 ? ps : ps1.getExternalId().compareTo(ps2.getExternalId());
        }
    };

    public static final Comparator<SpacePhotoSubmission> COMPARATOR_BY_MODIFIED = new Comparator<SpacePhotoSubmission>() {

        @Override
        public int compare(SpacePhotoSubmission ps1, SpacePhotoSubmission ps2) {
            int ps = ps1.getModified().compareTo(ps2.getModified());
            return ps != 0 ? ps : ps1.getExternalId().compareTo(ps2.getExternalId());
        }
    };

    @Override
    public void setSubmitor(User submitor) {
        if (submitor != null) {
            super.setSubmitor(submitor);
        }
    }

    @Override
    public void setReviewer(User reviewer) {
        if (reviewer != null) {
            super.setReviewer(reviewer);
        }
    }

    @Override
    public void setCreated(DateTime instant) {
        if (instant == null) {
            throw new SpaceDomainException("error.OccupationRequest.empty.instant");
        }
        super.setCreated(instant);
    }

    @Override
    public void setModified(DateTime instant) {
        if (instant == null) {
            throw new SpaceDomainException("error.OccupationRequest.empty.instant");
        }
        super.setModified(instant);
    }

    public boolean isPending() {
        if (getSpacePending() != null) {
            return true;
        }
        return false;
    }

    public boolean isRejected() {
        if (getSpaceArchived() != null && getRejectionMessage() != null) {
            return true;
        }
        return false;
    }

    /**
     * The submission may have been accepted but later deleted in this case the state is still accepted.
     * 
     * @return
     */
    public boolean isAccepted() {
        if (getSpacePending() == null
                && ((getSpaceArchived() == null) || (getSpaceArchived() != null && getRejectionMessage() == null))) {
            return true;
        }
        return false;
    }

    public void delete() {
        this.getPhoto().delete();
        super.setSubmitor(null);
        super.setSpace(null);
        super.setSpacePending(null);
        super.deleteDomainObject();
    }

}
