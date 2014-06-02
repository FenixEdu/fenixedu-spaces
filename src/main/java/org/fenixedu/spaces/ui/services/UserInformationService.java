package org.fenixedu.spaces.ui.services;

import java.util.Set;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;

public interface UserInformationService {

    public Set<Group> getGroups(User user);

    public String getEmail(User user);

    public String getContacts(User user);

}
