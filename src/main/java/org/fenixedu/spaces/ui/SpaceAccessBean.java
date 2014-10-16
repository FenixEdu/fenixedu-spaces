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

public class SpaceAccessBean {
    private String occupationExpression;
    private String managementExpression;
    private String currentOccupationExpression;

    public SpaceAccessBean() {
        occupationExpression = "";
        managementExpression = "";
        currentOccupationExpression = "";
    }

    public String getOccupationExpression() {
        return occupationExpression;
    }

    public void setOccupationExpression(String occupationExpression) {
        this.occupationExpression = occupationExpression;
    }

    public String getManagementExpression() {
        return managementExpression;
    }

    public void setManagementExpression(String managementExpression) {
        this.managementExpression = managementExpression;
    }

    public String getCurrentOccupationExpression() {
        return currentOccupationExpression;
    }

    public void setCurrentOccupationExpression(String currentOccupationExpression) {
        this.currentOccupationExpression = currentOccupationExpression;
    }
}
