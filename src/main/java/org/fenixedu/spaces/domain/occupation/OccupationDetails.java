package org.fenixedu.spaces.domain.occupation;

public class OccupationDetails extends OccupationDetails_Base {

    public OccupationDetails(String emails, String subject, String description) {
        super();
        setEmails(emails);
        setSubject(subject);
        setDescription(description);
    }

    public OccupationDetails(String subject, String description) {
        this(null, subject, description);
    }
}
