package org.fenixedu.spaces.ui;

import java.io.IOException;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.spaces.domain.Space;
import org.springframework.web.multipart.MultipartFile;

public class PhotoSubmissionBean {

    User submitor;
    Space space;
    MultipartFile submissionMultipartFile;

    public PhotoSubmissionBean() {
        setSubmitor(Authenticate.getUser());
    }

    public PhotoSubmissionBean(Space space) {
        this();
        setSpace(space);
    }

    public User getSubmitor() {
        return submitor;
    }

    public void setSubmitor(User submitor) {
        this.submitor = submitor;
    }

    public MultipartFile getSubmissionMultipartFile() {
        return submissionMultipartFile;
    }

    public void setSubmissionMultipartFile(MultipartFile submissionMultipartFile) {
        this.submissionMultipartFile = submissionMultipartFile;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public byte[] getSubmissionContent() {
        try {
            if (getSubmissionMultipartFile() != null && !getSubmissionMultipartFile().isEmpty()) {
                return getSubmissionMultipartFile().getBytes();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isImageFile() {
        if (submissionMultipartFile == null || submissionMultipartFile.isEmpty()) {
            return false;
        }
        String contentType = submissionMultipartFile.getContentType();
        if (contentType.equals("image/pjpeg") || contentType.equals("image/jpeg") || contentType.equals("image/png")
                || contentType.equals("image/x-png")) {
            return true;
        }
        return false;
    }

}
