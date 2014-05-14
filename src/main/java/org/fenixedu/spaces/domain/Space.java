package org.fenixedu.spaces.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.ui.InformationBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class Space extends Space_Base {

    public Space() {

    }

    public Space(Information information) {
        this(null, information);
    }

    public Space(Space parent, Information information) {
        init(parent, information);
    }

    private void init(Space parent, Information information) {
        setCreated(new DateTime());
        add(information);
        setParent(parent);
        setBennu(Bennu.getInstance());
    }

    public void init(Space parent, InformationBean informationBean) {
        init(parent, Information.builder(informationBean).build());
    }

    public Space(Space parent, InformationBean informationBean) {
        init(parent, informationBean);
    }

    public InformationBean bean() throws UnavailableException {
        return Information.builder(getInformation()).bean();
    }

    @Atomic(mode = TxMode.WRITE)
    public void bean(InformationBean informationBean) {
        add(Information.builder(informationBean).build());
    }

    public List<InformationBean> timeline() {
        List<InformationBean> timeline = new ArrayList<>();
        Information current = getCurrent();
        while (current != null) {
            timeline.add(Information.builder(current).bean());
            current = current.getPrevious();
        }
        return Lists.reverse(timeline);
    }

    public SpaceClassification getClassification() throws UnavailableException {
        return getInformation().getClassification();
    }

    public boolean isActive() {
        try {
            getInformation();
            return true;
        } catch (UnavailableException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T getMetadata(String field) throws UnavailableException {
        Information information = getInformation();
        final MetadataSpec metadataSpec = information.getClassification().getMetadataSpec(field);
        if (metadataSpec == null) {
            throw new UnavailableException();
        }
        final Class<?> type = metadataSpec.getType();
        final JsonObject metadata = information.getMetadata().getAsJsonObject();

        if (Boolean.class.isAssignableFrom(type)) {
            return (T) new Boolean(metadata.get(field).getAsBoolean());
        }
        if (Integer.class.isAssignableFrom(type)) {
            return (T) new Integer(metadata.get(field).getAsInt());
        }
        if (String.class.isAssignableFrom(type)) {
            return (T) new String(metadata.get(field).getAsString());
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return (T) metadata.get(field).getAsBigDecimal();
        }

        throw new UnavailableException();
    }

    /**
     * get the most recent space information
     * 
     * @return
     * @throws UnavailableException
     */
    protected Information getInformation() throws UnavailableException {
        return getInformation(new DateTime());
    }

    /**
     * get the most recent space information valid at the specified datetime.
     * 
     * @param when
     * @return
     * @throws UnavailableException
     */

    protected Information getInformation(DateTime when) throws UnavailableException {
        return getInformation(when, new DateTime());
    }

    /**
     * get the space information valid at the specified when date, created on atWhatDate.
     * 
     * @param when
     * @param atWhatDate
     * @return
     */

    protected Information getInformation(final DateTime when, final DateTime creationDate) throws UnavailableException {
        Information current = getCurrent();
        while (current != null) {
            if (current.contains(when)) {
                return current;
            }
            current = current.getPrevious();
        }
        throw new UnavailableException();
    }

    protected void add(Information information) {
        if (information == null) {
            return;
        }

        if (getCurrent() == null) {
            setCurrent(information);
            return;
        }

        final DateTime newStart = information.getValidFrom();
        final DateTime newEnd = information.getValidUntil();

        final Interval newValidity = information.getValidity();

        Information newCurrent = null;
        Information last = null;
        Information newHead = null;

        Information current = getCurrent();
        Information head = current;
        Interval currentValidity = current.getValidity();

        boolean foundEnd = false;
        boolean foundStart = false;

        // insert at head
        if (newValidity.isAfter(currentValidity)) {
            newHead = information;
            newHead.setPrevious(head);
        }

        if (newHead == null) {

            //last is the previous element of the new list
            //newCurrent is the current element of the new list

            while (current != null) {
                if (!foundEnd && !foundStart && current.contains(newValidity)) { //if start and end is in the current element
                    if (current.getValidity().equals(newValidity)) { // if it is the same period just replace current
                        newCurrent = information;
                    } else {
                        Information right = current.keepRight(newEnd);
                        if (last != null) {
                            last.setPrevious(right);
                        } else {
                            newHead = right; // no previous in new list, make right head
                        }
                        right.setPrevious(information);
                        last = information;
                        newCurrent = current.keepLeft(newStart);
                    }
                    foundEnd = true;
                    foundStart = true;
                } else {
                    if (!foundEnd) {
                        final boolean isAfter = current.isAfter(newEnd); //if newEnd is after current end date, then it is a gap
                        if (current.contains(newEnd) || isAfter) {
                            if (!isAfter) {
                                Information right = current.keepRight(newEnd);
                                if (last != null) {
                                    last.setPrevious(right);
                                } else {
                                    newHead = right; // no previous in new list, make right head
                                }
                                last = right;
                            }
                            newCurrent = information; // no need to cut current because it will be replaced by information
                            foundEnd = true;
                        }
                    }
                    final boolean isAfter = current.isAfter(newStart); //if newEnd is after current end date, then it is a gap
                    if (foundEnd && (current.contains(newStart) || isAfter)) { // looking for the start
                        newCurrent = current.keepLeft(newStart);
                        foundStart = true;
                    } else {
                        if (!foundEnd || foundStart) { // if not in the process of searching for information just keep copying current
                            newCurrent = current.copy();
                        }
                    }
                }

                //bookkeeping code
                if (last != null && !last.equals(newCurrent)) {
                    last.setPrevious(newCurrent);
                }

                last = newCurrent;

                if (newHead == null) {
                    newHead = newCurrent;
                }

                current = current.getPrevious();
            }

            //insert at end
            if (!foundEnd) {
                last.setPrevious(information);
            }
        }

        addHistory(head);
        setCurrent(newHead);
    }

    @Atomic(mode = TxMode.WRITE)
    public void delete() {
        setBennu(null);
        setDeletedBennu(Bennu.getInstance());
    }

    public Space readChildByBlueprintNumber(final String blueprintNumber, final DateTime when) {
        return FluentIterable.from(getChildrenSet()).firstMatch(new Predicate<Space>() {

            @Override
            public boolean apply(Space input) {
                try {
                    return blueprintNumber.equals(input.getBlueprintNumber());
                } catch (UnavailableException e) {
                    return false;
                }
            }
        }).orNull();
    }

    public String getBlueprintNumber() throws UnavailableException {
        return getBlueprintNumber(new DateTime());
    }

    public String getBlueprintNumber(DateTime when) throws UnavailableException {
        return getInformation(when).getBlueprintNumber();
    }

    public BlueprintFile getBlueprintFile() throws UnavailableException {
        return getBlueprintFile(new DateTime());
    }

    public BlueprintFile getBlueprintFile(DateTime when) throws UnavailableException {
        return getInformation(when).getBlueprint();
    }

    public List<Space> getPath() {
        List<Space> path = new ArrayList<>();
        Space parent = this;
        while (parent != null) {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
    }

    public String getName(DateTime when) throws UnavailableException {
        return getInformation(when).getName();
    }

    public String getName() {
        try {
            return getInformation().getName();
        } catch (UnavailableException e) {
            return "";
        }
    }

    public Integer getAllocatableCapacity(DateTime when) throws UnavailableException {
        return getInformation(when).getAllocatableCapacity();
    }

    public Integer getAllocatableCapacity() {
        try {
            return getInformation().getAllocatableCapacity();
        } catch (UnavailableException e) {
            return 0;
        }
    }

    public Set<Space> getValidChildrenSet() {
        return FluentIterable.from(getChildrenSet()).filter(new Predicate<Space>() {

            @Override
            public boolean apply(Space input) {
                try {
                    input.getInformation();
                    return true;
                } catch (UnavailableException e) {
                    return false;
                }
            }
        }).toSet();
    }

    public static Set<Space> getSpaces(final SpaceClassification classification) {
        Set<Space> spaces = Bennu.getInstance().getSpaceSet();
        return FluentIterable.from(spaces).filter(new Predicate<Space>() {

            @Override
            public boolean apply(Space space) {
                try {
                    return classification.equals(space.getClassification());
                } catch (UnavailableException e) {
                    return false;
                }
            }
        }).toSet();
    }

    public static Set<Space> getAllCampus() {
        return getSpaces(SpaceClassification.getCampusClassification());
    }

    public Group getManagementAccessGroupWithChainOfResponsability() {
        final PersistentGroup accessGroup = getManagementAccessGroup();
        if (accessGroup != null) {
            return accessGroup.toGroup();
        }
        final Space surroundingSpace = getParent();
        if (surroundingSpace != null) {
            return surroundingSpace.getManagementAccessGroupWithChainOfResponsability();
        }
        return null;
    }

    public Group getOccupationsAccessGroupWithChainOfResponsability() {
        final PersistentGroup accessGroup = getOccupationsAccessGroup();
        if (accessGroup != null) {
            return accessGroup.toGroup();
        }
        final Space surroundingSpace = getParent();
        if (surroundingSpace != null) {
            return surroundingSpace.getOccupationsAccessGroupWithChainOfResponsability();
        }
        return null;
    }

    public void setManagementAccessGroup(Group managementAccessGroup) {
        super.setManagementAccessGroup(managementAccessGroup == null ? null : managementAccessGroup.toPersistentGroup());
    }

    public void setOccupationsAccessGroup(Group occupationsAccessGroup) {
        super.setOccupationsAccessGroup(occupationsAccessGroup == null ? null : occupationsAccessGroup.toPersistentGroup());
    }

    public boolean isFree(List<Interval> intervals) {
        for (Occupation occupation : getOccupationSet()) {
            if (occupation.overlaps(intervals)) {
                return false;
            }
        }
        return true;
    }

    public String getNameWithParents() {
        final List<Space> path = Lists.reverse(getPath());
        final Space space = path.get(0);
        final Set<String> parents = FluentIterable.from(path.subList(1, path.size())).filter(new Predicate<Space>() {

            @Override
            public boolean apply(Space input) {
                return input.isActive();
            }
        }).transform(new Function<Space, String>() {

            @Override
            public String apply(Space input) {
                return input.getName();
            }

        }).toSet();

        final String others = Joiner.on(", ").join(parents);
        return String.format("%s (%s)", space.getName(), others);
    }

    public boolean isOccupationMember(final User user) {
        final Group group = getOccupationsAccessGroupWithChainOfResponsability();
        return group != null && group.isMember(user);
    }

    public boolean isSpaceManagementMember(final User user) {
        final Group group = getManagementAccessGroupWithChainOfResponsability();
        return group != null && group.isMember(user);
    }

}
