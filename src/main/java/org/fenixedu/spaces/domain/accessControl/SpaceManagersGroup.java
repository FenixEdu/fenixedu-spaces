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
package org.fenixedu.spaces.domain.accessControl;

import static org.fenixedu.bennu.FenixEduSpaceConfiguration.BUNDLE;

import java.util.stream.Stream;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.GroupStrategy;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.DateTime;

@GroupOperator("spaceManager")
public class SpaceManagersGroup extends GroupStrategy {

    private static final long serialVersionUID = 1L;

    @Override
    public Stream<User> getMembers() {
        return Space.getSpaces().filter(Space::isActive).map(s -> s.getManagementGroup()).filter(g -> g != null)
                .flatMap(g -> g.getMembers());
    }

    @Override
    public boolean isMember(User user) {
        return Space.getSpaces().filter(Space::isActive).anyMatch(space -> space.isSpaceManagementMember(user));
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return getMembers();
    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return isMember(user);
    }

    @Override
    public String getPresentationName() {
        return BundleUtil.getString(BUNDLE, "label.name." + getClass().getSimpleName());
    }

}
