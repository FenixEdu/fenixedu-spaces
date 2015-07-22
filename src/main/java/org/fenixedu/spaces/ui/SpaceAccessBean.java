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

package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.core.groups.Group;

public class SpaceAccessBean {
    private Group occupationGroup;
    private Group managementGroup;

    public SpaceAccessBean() {
        occupationGroup = Group.nobody();
        managementGroup = Group.nobody();
    }

    public String getOccupationExpression() {
        return occupationGroup.getExpression();
    }

    public void setOccupationExpression(String occupationExpression) {
        this.occupationGroup = Group.parse(occupationExpression);
    }

    public String getManagementExpression() {
        return managementGroup.getExpression();
    }

    public void setManagementExpression(String managementExpression) {
        this.managementGroup = Group.parse(managementExpression);
    }

    public void setManagementGroup(Group managementGroup) {
        this.managementGroup = managementGroup;
    }

    public void setOccupationGroup(Group occupationGroup) {
        this.occupationGroup = occupationGroup;
    }

    public Group getManagementGroup() {
        return managementGroup;
    }

    public Group getOccupationGroup() {
        return occupationGroup;
    }

}
