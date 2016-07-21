package org.fenixedu.spaces.ui.services;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.submission.SpacePhoto;
import org.fenixedu.spaces.domain.submission.SpacePhotoSubmission;
import org.fenixedu.spaces.ui.PhotoSubmissionBean;
import org.joda.time.DateTime;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

@Service
public class SpacePhotoService {

    private final int PHOTOS_IN_PAGE = 10;

    @Atomic
    public SpacePhotoSubmission createPhotoSubmission(PhotoSubmissionBean bean, Space space) {
        SpacePhoto photo = new SpacePhoto(bean.getSubmissionMultipartFile().getName(), bean.getSubmissionContent());

        return new SpacePhotoSubmission(space, bean.getSubmitor(), photo);

    }

    public List<SpacePhotoSubmission> getAllSpacePhotoSubmissionsToProcess(Space space) {
        Set<Space> allSpaces = space.getChildTree();
        List<SpacePhotoSubmission> allPendingPhotos = allSpaces.stream().map(s -> s.getSpacePhotoSubmissionPendingSet())
                .flatMap(set -> set.stream()).collect(Collectors.toList());
        return allPendingPhotos.stream().sorted(SpacePhotoSubmission.COMPARATOR_BY_INSTANT.reversed())
                .collect(Collectors.toList());
    }

    public List<SpacePhotoSubmission> getSpacePhotoSubmissionsToProcess(Space space) {
        return space.getSpacePhotoSubmissionPendingSet().stream().sorted(SpacePhotoSubmission.COMPARATOR_BY_INSTANT.reversed())
                .collect(Collectors.toList());
    }

    public List<SpacePhoto> getVisiblePhotos(Space space) {
        return getAllSpacePhotos(space).stream().filter(photo -> photo.isVisible()).collect(Collectors.toList());
    }

    public List<SpacePhoto> getAllSpacePhotos(Space space) {
        if (space.getSpacePhotoSet().isPresent()) {
            return space.getSpacePhotoSet().get().stream().sorted(SpacePhoto.COMPARATOR_BY_INSTANT.reversed())
                    .collect(Collectors.toList());
        }
        return Collections.<SpacePhoto> emptyList();
    }

    public PagedListHolder<SpacePhoto> getPhotoBook(List<SpacePhoto> photos, String pageString) {
        PagedListHolder<SpacePhoto> book = new PagedListHolder<>(photos);
        book.setPageSize(PHOTOS_IN_PAGE);
        int page = 0;

        if (Strings.isNullOrEmpty(pageString)) {
            page = 0;
        } else {
            try {
                page = Integer.parseInt(pageString);
            } catch (NumberFormatException nfe) {
                if ("f".equals(pageString)) {
                    page = 0;
                } else if ("l".equals(pageString)) {
                    page = book.getPageCount();
                }
            }
        }
        book.setPage(page == 0 ? 0 : page - 1);
        return book;
    }

    public PagedListHolder<SpacePhotoSubmission> getSubmissionBook(List<SpacePhotoSubmission> submissions, String pageString) {
        PagedListHolder<SpacePhotoSubmission> book = new PagedListHolder<>(submissions);
        book.setPageSize(PHOTOS_IN_PAGE);
        int page = 0;

        if (Strings.isNullOrEmpty(pageString)) {
            page = 0;
        } else {
            try {
                page = Integer.parseInt(pageString);
            } catch (NumberFormatException nfe) {
                if ("f".equals(pageString)) {
                    page = 0;
                } else if ("l".equals(pageString)) {
                    page = book.getPageCount();
                }
            }
        }
        book.setPage(page == 0 ? 0 : page - 1);
        return book;
    }

    @Atomic
    public void rejectSpacePhoto(SpacePhotoSubmission spacePhotoSubmission, User reviewer, String rejectionMessage) {
        spacePhotoSubmission.setModified(new DateTime());
        Space space = spacePhotoSubmission.getSpacePending();
        spacePhotoSubmission.setRejectionMessage(rejectionMessage);
        spacePhotoSubmission.setReviewer(reviewer);
        space.removeSpacePhotoSubmissionPending(spacePhotoSubmission);
        space.addSpacePhotoSubmissionArchived(spacePhotoSubmission);
    }

    @Atomic
    public void acceptSpacePhoto(SpacePhotoSubmission spacePhotoSubmission, User reviewer) {
        spacePhotoSubmission.setModified(new DateTime());
        Space space = spacePhotoSubmission.getSpacePending();
        space.addSpacePhoto(spacePhotoSubmission.getPhoto());
        spacePhotoSubmission.setReviewer(reviewer);
        space.removeSpacePhotoSubmissionPending(spacePhotoSubmission);

    }

    @Atomic
    public void removeSpacePhoto(Space space, SpacePhoto spacePhoto) {
        spacePhoto.getSubmission().setModified(new DateTime());
        space.getSpacePhotoSet().orElse(Collections.<SpacePhoto> emptySet()).remove(spacePhoto);
        space.addSpacePhotoSubmissionArchived(spacePhoto.getSubmission());
    }

    @Atomic
    public void cancelUserSubmission(SpacePhotoSubmission photoSubmission, User user) {
        if (photoSubmission.isPending() && user.equals(photoSubmission.getSubmitor())) {
            photoSubmission.delete();
        }

    }

    @Atomic
    public void hideSpacePhoto(SpacePhoto spacePhoto) {
        spacePhoto.setVisible(false);
    }

    @Atomic
    public void showSpacePhoto(SpacePhoto spacePhoto) {
        spacePhoto.setVisible(true);
    }

    public List<SpacePhotoSubmission> getPendingUserSubmissions(User user) {
        return user.getSpacePhotoSubmissionSet().stream().filter(s -> s.isPending())
                .sorted(SpacePhotoSubmission.COMPARATOR_BY_INSTANT.reversed()).collect(Collectors.toList());
    }

    public List<SpacePhotoSubmission> getAcceptedUserSubmissions(User user) {
        return user.getSpacePhotoSubmissionSet().stream().filter(s -> s.isAccepted())
                .sorted(SpacePhotoSubmission.COMPARATOR_BY_MODIFIED.reversed()).collect(Collectors.toList());
    }

    public List<SpacePhotoSubmission> getRejectedUserSubmissions(User user) {
        return user.getSpacePhotoSubmissionSet().stream().filter(s -> s.isRejected())
                .sorted(SpacePhotoSubmission.COMPARATOR_BY_MODIFIED.reversed()).collect(Collectors.toList());
    }

    public List<SpacePhotoSubmission> getArchivedSpacePhotoSubmissions(Space space) {
        return space.getSpacePhotoSubmissionArchivedSet().stream().sorted(SpacePhotoSubmission.COMPARATOR_BY_MODIFIED.reversed())
                .collect(Collectors.toList());
    }

}
