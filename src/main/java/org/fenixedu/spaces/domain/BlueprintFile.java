package org.fenixedu.spaces.domain;

import java.util.HashMap;
import java.util.List;

public class BlueprintFile extends BlueprintFile_Base {

    public BlueprintFile(String filename, byte[] content) {
        super();
        init(filename, filename, content);
    }

    public static class BlueprintTextRectangles extends HashMap<Space, List<BlueprintTextRectangle>> {
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

}
