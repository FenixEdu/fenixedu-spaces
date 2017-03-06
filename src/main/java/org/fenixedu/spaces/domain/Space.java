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
package org.fenixedu.spaces.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.domain.submission.SpacePhoto;
import org.fenixedu.spaces.ui.InformationBean;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public final class Space extends Space_Base implements Comparable<Space> {
    public Space() {
        super();
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

    public InformationBean bean() {
        return getInformation().map(info -> Information.builder(info)).orElse(Information.builder()).bean();
    }

    @Atomic(mode = TxMode.WRITE)
    public void bean(InformationBean informationBean) {
        add(Information.builder(informationBean).build());
    }

    /**
     * get all information beans
     * 
     * @return information beans ordered by date (ascending)
     * @author cfscosta
     */
    public List<InformationBean> timeline() {
        List<InformationBean> timeline = new ArrayList<>();
        Information current = getCurrent();
        while (current != null) {
            timeline.add(Information.builder(current).bean());
            current = current.getPrevious();
        }
        return Lists.reverse(timeline);
    }

    public SpaceClassification getClassification() {
        return getInformation().map(info -> info.getClassification()).get();
    }

    public boolean isActive() {
        return getInformation().isPresent() && getBennu() != null;
    }

    public <T extends Object> Optional<T> getMetadata(String field) {
        Optional<Information> information = getInformation();
        return information.isPresent() ? information.get().getMetadata(field) : Optional.empty();
    }

    /**
     * get the most recent space information
     *
     * @return
     */
    protected Optional<Information> getInformation() {
        return getInformation(new DateTime());
    }

    /**
     * get the most recent space information valid at the specified datetime.
     *
     * @param when
     * @return
     */

    protected Optional<Information> getInformation(DateTime when) {
        return getInformation(when, new DateTime());
    }

    /**
     * get the space information valid at the specified when date, created on atWhatDate.
     *
     * @param when
     * @param atWhatDate
     * @return
     */

    protected Optional<Information> getInformation(final DateTime when, final DateTime creationDate) {
        Information current = getCurrent();
        while (current != null) {
            if (current.contains(when)) {
                return Optional.of(current);
            }
            current = current.getPrevious();
        }
        return Optional.empty();
    }

    private Boolean dateEquals(DateTime validFrom, DateTime validUntil) {
        if (validFrom == null && validUntil == null) {
            return Boolean.TRUE;
        }
        return validFrom == null ? Boolean.FALSE : validFrom.equals(validUntil);
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
                        Information right = dateEquals(current.getValidUntil(), newEnd) ? information : current.keepRight(newEnd);
                        if (last != null) {
                            last.setPrevious(right);
                        } else {
                            newHead = right; // no previous in new list, make right head
                        }
                        if (right != information) {
                            right.setPrevious(information);
                        }
                        last = information;
                        newCurrent = dateEquals(current.getValidFrom(), newStart) ? last : current.keepLeft(newStart);
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

    public Optional<Space> readChildByBlueprintNumber(final String blueprintNumber, final DateTime when) {
        return Strings.isNullOrEmpty(blueprintNumber) ? Optional.empty() : getChildren().stream()
                .filter(space -> blueprintNumber.equals(space.getBlueprintNumber().orElse(null))).findFirst();
    }

    public Optional<String> getBlueprintNumber() {
        return getInformation().map(info -> info.getBlueprintNumber());
    }

    public Optional<String> getBlueprintNumber(DateTime when) {
        return getInformation(when).map(info -> info.getBlueprintNumber());
    }

    public Optional<BlueprintFile> getBlueprintFile() {
        return getBlueprintFile(new DateTime());
    }

    public Optional<BlueprintFile> getBlueprintFile(DateTime when) {
        return getInformation(when).map(info -> info.getBlueprint());
    }

    public Optional<Set<SpacePhoto>> getSpacePhotoSet() {
        return getSpacePhotoSet(new DateTime());
    }

    public Optional<Set<SpacePhoto>> getSpacePhotoSet(DateTime when) {
        return getInformation(when).map(info -> info.getSpacePhotoSet());
    }

    public void addSpacePhoto(SpacePhoto photo) {
        getInformation(new DateTime()).get().addSpacePhoto(photo);
    }

    public List<Space> getPath() {
        List<Space> path = new ArrayList<>();
        Space parent = this;
        while (parent != null && parent.isActive()) {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
    }

    public Optional<String> getName(DateTime when) {
        return getInformation(when).map(info -> info.getName());
    }

    public String getName() {
        return getInformation().map(info -> info.getName()).orElse("");
    }

    public String getFullName() {
        String name = getName();
        String description = (String) getMetadata("description").orElse("");
        if (!description.isEmpty()) {
            if (!name.isEmpty()) {
                name += " - ";
            }
            name += description;
        }
        return name;
    }

    public Optional<Integer> getAllocatableCapacity(DateTime when) {
        return getInformation(when).map(info -> info.getAllocatableCapacity());
    }

    public Integer getAllocatableCapacity() {
        return getInformation().map(info -> info.getAllocatableCapacity()).orElse(0);
    }

    public Set<Space> getChildren() {
        return getChildrenSet().stream().filter(space -> space.isActive()).collect(Collectors.toSet());
    }

    /***
     * Get tree of spaces who have as root parent the current space.
     * 
     * @return set of spaces
     */
    public Set<Space> getChildTree() {
        return Stream.concat(Stream.of(this), getChildrenSet().stream().flatMap(space -> space.getChildTree().stream()))
                .collect(Collectors.toSet());
    }

    public static Set<Space> getSpaces(final SpaceClassification classification) {
        return getSpaces().filter(space -> classification.equals(space.getClassification())).collect(Collectors.toSet());
    }

    @Deprecated
    /***
     * To be removed in next major.
     * 
     * Use getTopLevelSpaces()
     * 
     * @see
     */
    public static Set<Space> getAllCampus() {
        return getTopLevelSpaces();
    }

    public static Set<Space> getTopLevelSpaces() {
        return getSpaces().filter(s -> s.getParent() == null).sorted().collect(Collectors.toSet());
    }

    public Group getManagementGroup() {
        return getManagementAccessGroup() != null ? getManagementAccessGroup().toGroup() : null;
    }

    public Group getManagementGroupWithChainOfResponsability() {
        Group accessGroup = getManagementGroup();
        if (accessGroup == null) {
            accessGroup = NobodyGroup.get();
        }
        final Space surroundingSpace = getParent();
        if (surroundingSpace != null) {
            return accessGroup.or(surroundingSpace.getManagementGroupWithChainOfResponsability());
        }
        return accessGroup;
    }

    public Group getOccupationsGroup() {
        return getOccupationsAccessGroup() != null ? getOccupationsAccessGroup().toGroup() : null;
    }

    public Group getOccupationsGroupWithChainOfResponsability() {
        Group accessGroup = getOccupationsGroup();
        if (accessGroup == null) {
            accessGroup = NobodyGroup.get();
        }
        final Space surroundingSpace = getParent();
        if (surroundingSpace != null) {
            return accessGroup.or(surroundingSpace.getOccupationsGroupWithChainOfResponsability());
        }
        return accessGroup;
    }

    public void setManagementAccessGroup(Group managementAccessGroup) {
        super.setManagementAccessGroup(managementAccessGroup == null ? null : managementAccessGroup.toPersistentGroup());
    }

    public void setOccupationsAccessGroup(Group occupationsAccessGroup) {
        super.setOccupationsAccessGroup(occupationsAccessGroup == null ? null : occupationsAccessGroup.toPersistentGroup());
    }

    public boolean isFree(Interval... intervals) {
        for (Occupation occupation : getOccupationSet()) {
            if (occupation.overlaps(intervals)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFree(List<Interval> intervals) {
        for (Occupation occupation : getOccupationSet()) {
            if (occupation.overlaps(intervals)) {
                return false;
            }
        }
        return true;
    }

    public String getPresentationName() {
        final List<Space> path = Lists.reverse(getPath());
        String others = path.subList(1, path.size()).stream().map(Space::getName).collect(Collectors.joining(", "));
        return String.format(Strings.isNullOrEmpty(others) ? "%s" : "%s (%s)", path.get(0).getName(), others);
    }

    public boolean isOccupationMember(final User user) {
        final Group group = getOccupationsGroupWithChainOfResponsability();
        return group != null && group.isMember(user);
    }

    public boolean isSpaceManagementMember(final User user) {
        final Group group = getManagementGroupWithChainOfResponsability();
        return group != null && group.isMember(user);
    }

    public static Stream<Space> getSpaces() {
        return getAllSpaces().filter(space -> space.isActive());
    }

    public static Stream<Space> getAllSpaces() {
        return Bennu.getInstance().getSpaceSet().stream();
    }

    @Override
    public int compareTo(Space o) {
        return getFullName().toLowerCase().compareTo(o.getFullName().toLowerCase());
    }

}
