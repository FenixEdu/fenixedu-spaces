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
package org.fenixedu.spaces.domain.occupation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class Occupation extends Occupation_Base {

    public Occupation() {
        super();
        setBennu(Bennu.getInstance());
    }

    public Occupation(String emails, String subject, String description, OccupationConfig config) {
        this(emails, subject, description, config, null);
    }

    public Occupation(String emails, String subject, String description, OccupationConfig config, OccupationRequest request) {
        this();
        setConfig(config);
        setEmails(emails);
        setSubject(subject);
        setDescription(description);
        setRequest(request);
    }

    @Override
    public void addSpace(Space space) {
        super.addSpace(space);
    }

    @Override
    public void removeSpace(Space space) {
        super.removeSpace(space);
    }

    public Set<Space> getSpaces() {
        return getSpaceSet().stream().filter(space -> space.isActive()).collect(Collectors.toSet());
    }

    public List<Interval> getIntervals() {
        return getConfig().getIntervals();
    }

    public Boolean overlaps(List<Interval> intervals) {
        for (final Interval interval : intervals) {
            for (final Interval occupationInterval : getIntervals()) {
                if (occupationInterval.overlaps(interval)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean overlaps(Interval... intervals) {
        for (final Interval interval : intervals) {
            for (final Interval occupationInterval : getIntervals()) {
                if (occupationInterval.overlaps(interval)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isActive() {
        return !getIntervals().get(getIntervals().size() - 1).getEnd().isBeforeNow();
    }

    public String getSummary() {
        return getConfig().getSummary();
    }

    public String getExtendedSummary() {
        return getConfig().getExtendedSummary();
    }

    public DateTime getStart() {
        return getConfig().getStart();
    }

    public DateTime getEnd() {
        return getConfig().getEnd();
    }

    public void delete() {
        if (getRequest() != null) {
            setRequest(null);
        }
        setBennu(null);
        getSpaceSet().clear();
        super.deleteDomainObject();
    }

    public boolean canManageOccupation(User user) {
        for (Space space : getSpaces()) {
            if (!space.isOccupationMember(user)) {
                return false;
            }
        }
        return true;
    }

    public String getUrl() {
        if (!getClass().equals(Occupation.class)) {
            return "";
        }
        if (canManageOccupation(Authenticate.getUser())) {
            return CoreConfiguration.getConfiguration().applicationUrl() + "/spaces/occupations/view/" + getOid().toString();
        }
        return "";
    }

}
