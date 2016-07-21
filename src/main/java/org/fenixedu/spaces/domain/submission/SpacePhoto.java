package org.fenixedu.spaces.domain.submission;

import java.util.Comparator;

import org.fenixedu.bennu.core.domain.User;

public class SpacePhoto extends SpacePhoto_Base {

    public SpacePhoto(String filename, byte[] content) {
        super();
        setVisible(true);
        init(filename, filename, content);
    }

    public static final Comparator<SpacePhoto> COMPARATOR_BY_INSTANT = new Comparator<SpacePhoto>() {

        @Override
        public int compare(SpacePhoto ps1, SpacePhoto ps2) {
            int ps = ps1.getCreationDate().compareTo(ps2.getCreationDate());
            return ps != 0 ? ps : ps1.getExternalId().compareTo(ps2.getExternalId());
        }
    };

    public boolean isVisible() {
        return this.getVisible();
    }

    @Override
    public boolean isAccessible(User user) {
        return true;
    }

    @Override
    public void delete() {
        super.setSubmission(null);
        super.delete();
    }

}
