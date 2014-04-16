package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.spaces.domain.Space;

public class OccupationBean {

    User requestor;
    Space campus;
    String subject;
    String description;

    public OccupationBean() {
        this.requestor = Authenticate.getUser();
    }

    public OccupationBean(User requestor, Space campus, String subject, String description) {
        super();
        this.requestor = requestor;
        this.campus = campus;
        this.subject = subject;
        this.description = description;
    }

    public Space getCampus() {
        return campus;
    }

    public void setCampus(Space campus) {
        this.campus = campus;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getRequestor() {
        return requestor;
    }

    public void setRequestor(User requestor) {
        this.requestor = requestor;
    }

}
