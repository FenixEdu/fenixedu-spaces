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

import java.util.HashMap;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;

public class BlueprintFile extends BlueprintFile_Base {

    public BlueprintFile(String filename, byte[] content) {
        super();
        init(filename, filename, content);
    }

    public static class BlueprintTextRectangles extends HashMap<Space, List<BlueprintTextRectangle>> {
        private static final long serialVersionUID = 4137530994580538348L;
    }

    public static class BlueprintTextRectangle {

        private final BlueprintPoint p1;

        private final BlueprintPoint p2;

        private final BlueprintPoint p3;

        private final BlueprintPoint p4;

        public BlueprintTextRectangle(String text, double x, double y, int fontSize) {

            double numberOfCharacters = text.length();
            double characterWidth = (fontSize / 1.6);
            double textSize = numberOfCharacters * characterWidth;

            p1 = new BlueprintPoint((int) x, (int) Math.round(y - fontSize));
            p2 = new BlueprintPoint((int) x, (int) y);
            p3 = new BlueprintPoint((int) Math.round(x + textSize), (int) y);
            p4 = new BlueprintPoint((int) Math.round(x + textSize), (int) Math.round(y - fontSize));

        }

        public BlueprintPoint getP1() {
            return p1;
        }

        public BlueprintPoint getP2() {
            return p2;
        }

        public BlueprintPoint getP3() {
            return p3;
        }

        public BlueprintPoint getP4() {
            return p4;
        }
    }

    public static class BlueprintPoint {

        private final int x;

        private final int y;

        public BlueprintPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    @Override
    public boolean isAccessible(User user) {
        return true;
    }

}
