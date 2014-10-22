package org.fenixedu.spaces.domain.occupation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.userOccupationConfig;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;
import org.fenixedu.spaces.domain.occupation.config.SharedOccupationConfig;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.ui.SpaceOccupantsBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class SharedOccupation extends SharedOccupation_Base {

    public SharedOccupation() {
        super();
        setBennu(Bennu.getInstance());
    }

    public SharedOccupation(String emails, String subject, String description, SharedOccupationConfig config) {
        this(emails, subject, description, config, null);
    }

    public SharedOccupation(String emails, String subject, String description, SharedOccupationConfig config,
            OccupationRequest request) {
        this();
        setConfig(config);
        setEmails(emails);
        setSubject(subject);
        setDescription(description);
        setRequest(request);
    }

    public Set<userOccupationConfig> getUserOccupationConfigs() {
        return getUserConfigPairSet();
    }

    public void addConfig(SpaceOccupantsBean sob) {
        addUser(sob.getUserObject(), sob.getConfig());
    }

    @Override
    public void addSpace(Space space) {
        if (getSpaces().size() >= 1) {
            return;
        } else {
            super.addSpace(space);
        }
    }

    @Override
    public void removeSpace(Space space) {
        super.removeSpace(space);
    }

    @Override
    public Set<Space> getSpaces() {
        return getSpaceSet().stream().filter(space -> space.isActive()).collect(Collectors.toSet());
    }

    @Override
    public List<Interval> getIntervals() {
        List<Interval> li = new ArrayList<Interval>();
        for (userOccupationConfig uoc : getUserConfigPairSet()) {
            li.addAll(uoc.getConfig().getIntervals());
        }
        return li;
    }

    @Override
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

    @Override
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

    @Override
    public Boolean isActive() {
        return !getIntervals().get(getIntervals().size() - 1).getEnd().isBeforeNow();
    }

    @Override
    public String getSummary() {
        return getConfig().getSummary();
    }

    @Override
    public String getExtendedSummary() {
        return getConfig().getExtendedSummary();
    }

    @Override
    public DateTime getStart() {
        return getConfig().getStart();
    }

    @Override
    public DateTime getEnd() {
        return getConfig().getEnd();
    }

    @Override
    public void delete() {
        if (getRequest() != null) {
            setRequest(null);
        }
        setBennu(null);
        getSpaceSet().clear();
        super.deleteDomainObject();
    }

    @Override
    public boolean canManageOccupation(User user) {
        for (Space space : getSpaces()) {
            if (!space.isOccupationMember(user)) {
                return false;
            }
        }
        return true;
    }

    public int getSlotsLeft() {
        Space theSpace = getSpaceSet().iterator().next();
        if (theSpace == null) {
            return 0;
        }
        Integer capacity = theSpace.getAllocatableCapacity();
        return capacity - getUserConfigPairSet().size();
    }

    public void addUser(User user, OccupationConfig config) {
        if (getSlotsLeft() <= 0) {
            //TODO throw exception
            return;
        }
        addUserConfigPair(new userOccupationConfig(user, config));
        return;
    }

    public boolean updateUser(User user, OccupationConfig config) {
        for (userOccupationConfig uoc : getUserConfigPairSet()) {
            if (uoc.getUser().equals(user)) {
                uoc.setConfig(config);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
