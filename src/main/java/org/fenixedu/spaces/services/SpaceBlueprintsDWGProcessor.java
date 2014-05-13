package org.fenixedu.spaces.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.fenixedu.spaces.domain.BlueprintFile;
import org.fenixedu.spaces.domain.BlueprintFile.BlueprintTextRectangle;
import org.fenixedu.spaces.domain.BlueprintFile.BlueprintTextRectangles;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.UnavailableException;
import org.joda.time.DateTime;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.iver.cit.jdwglib.dwg.DwgFile;
import com.iver.cit.jdwglib.dwg.DwgObject;
import com.iver.cit.jdwglib.dwg.objects.DwgMText;
import com.iver.cit.jdwglib.dwg.objects.DwgText;

public class SpaceBlueprintsDWGProcessor extends DWGProcessor {

    private Space parentSpace;

    private Boolean viewSpaceIdentifications;

    private Boolean viewBlueprintNumbers;

    private Boolean viewDoorNumbers;

    private Boolean suroundingSpaceBlueprint;

    private Boolean viewOriginalSpaceBlueprint;

    private Space thisSpace;

    private DateTime when;

    public SpaceBlueprintsDWGProcessor(Space space, DateTime when, Boolean viewBlueprintNumbers_,
            Boolean viewSpaceIdentifications_, Boolean viewDoorNumbers_, BigDecimal scalePercentage) throws IOException {

        super(scalePercentage);
        this.thisSpace = space.getParent();
        this.parentSpace = space;
        this.suroundingSpaceBlueprint = thisSpace != null;
        this.viewOriginalSpaceBlueprint = false;

        this.viewDoorNumbers = viewDoorNumbers_;
        this.viewSpaceIdentifications = viewSpaceIdentifications_;
        this.viewBlueprintNumbers = viewBlueprintNumbers_;
        this.when = when;
    }

    public SpaceBlueprintsDWGProcessor(BigDecimal scalePercentage) throws IOException {
        super(scalePercentage);
        this.viewOriginalSpaceBlueprint = true;
    }

    @Override
    protected void drawText(ReferenceConverter referenceConverter, Graphics2D graphics2D, DwgMText dwgMText) {

        String text = getText(dwgMText);
        if (isToViewOriginalSpaceBlueprint() != null && isToViewOriginalSpaceBlueprint()) {
            super.drawText(referenceConverter, graphics2D, dwgMText);
        } else {
            int x = convXCoord(dwgMText.getInsertionPoint()[0], referenceConverter);
            int y = convYCoord(dwgMText.getInsertionPoint()[1], referenceConverter);

            Space discoveredSpace = getParentSpace().readChildByBlueprintNumber(text.trim(), when);
            String textToInsert =
                    getTextToInsert(text, discoveredSpace, isToViewBlueprintNumbers(), isToViewSpaceIdentifications(),
                            isToViewDoorNumbers());
            drawTextAndArc(graphics2D, x, y, discoveredSpace, textToInsert);
        }
    }

    @Override
    protected void drawText(ReferenceConverter referenceConverter, Graphics2D graphics2D, DwgText dwgText) {

        if (isToViewOriginalSpaceBlueprint() != null && isToViewOriginalSpaceBlueprint()) {
            super.drawText(referenceConverter, graphics2D, dwgText);

        } else {
            final Point2D point2D = dwgText.getInsertionPoint();
            int x = convXCoord(point2D.getX(), referenceConverter);
            int y = convYCoord(point2D.getY(), referenceConverter);

            Space discoveredSpace = getParentSpace().readChildByBlueprintNumber(dwgText.getText().trim(), when);
            String textToInsert =
                    getTextToInsert(dwgText.getText(), discoveredSpace, isToViewBlueprintNumbers(),
                            isToViewSpaceIdentifications(), isToViewDoorNumbers());
            drawTextAndArc(graphics2D, x, y, discoveredSpace, textToInsert);
        }
    }

    public static BlueprintTextRectangles getBlueprintTextRectangles(final InputStream inputStream, Space parentSpace,
            DateTime when, Boolean viewBlueprintNumbers, Boolean viewOriginalSpaceBlueprint, Boolean viewSpaceIdentifications,
            Boolean viewDoorNumbers, BigDecimal scalePercentage) throws IOException {

        BlueprintTextRectangles map = new BlueprintTextRectangles();
        if (viewOriginalSpaceBlueprint != null && viewOriginalSpaceBlueprint) {
            return map;
        }

        final File file = File.createTempFile("blueprint", "dwg");
        Files.copy(new InputSupplier<InputStream>() {

            @Override
            public InputStream getInput() throws IOException {
                return inputStream;
            }
        }, file);
        final SpaceBlueprintsDWGProcessor processor = new SpaceBlueprintsDWGProcessor(scalePercentage);
        final DwgFile dwgFile = processor.readDwgFile(file.getAbsolutePath());
        final Vector<DwgObject> dwgObjects = dwgFile.getDwgObjects();
        final ReferenceConverter referenceConverter = new ReferenceConverter(dwgObjects, processor.scaleRatio);

        for (final DwgObject dwgObject : dwgObjects) {

            if (dwgObject instanceof DwgText) {
                DwgText dwgText = ((DwgText) dwgObject);
                final Point2D point2D = dwgText.getInsertionPoint();
                Space discoveredSpace = parentSpace.readChildByBlueprintNumber(dwgText.getText().trim(), when);
                String textToInsert =
                        getTextToInsert(dwgText.getText(), discoveredSpace, viewBlueprintNumbers, viewSpaceIdentifications,
                                viewDoorNumbers);
                putLinksCoordinatesToMap(map, processor, referenceConverter, point2D.getX(), point2D.getY(), textToInsert,
                        discoveredSpace);

            } else if (dwgObject instanceof DwgMText) {
                DwgMText dwgMText = (DwgMText) dwgObject;
                String text = getText(dwgMText);
                Space discoveredSpace = parentSpace.readChildByBlueprintNumber(text.trim(), when);
                String textToInsert =
                        getTextToInsert(text, discoveredSpace, viewBlueprintNumbers, viewSpaceIdentifications, viewDoorNumbers);
                putLinksCoordinatesToMap(map, processor, referenceConverter, dwgMText.getInsertionPoint()[0],
                        dwgMText.getInsertionPoint()[1], textToInsert, discoveredSpace);
            }
        }
        return map;
    }

    private static void putLinksCoordinatesToMap(BlueprintTextRectangles map, final SpaceBlueprintsDWGProcessor processor,
            final ReferenceConverter referenceConverter, double x, double y, String textToInsert, Space space) {

        if (textToInsert != null) {

            List<BlueprintTextRectangle> blueprintTextRectangules = map.get(space);
            if (blueprintTextRectangules == null) {
                blueprintTextRectangules = new ArrayList<BlueprintTextRectangle>();
            }

            blueprintTextRectangules.add(new BlueprintTextRectangle(textToInsert, processor.convXCoord(x, referenceConverter),
                    processor.convYCoord(y, referenceConverter), processor.fontSize));
            map.put(space, blueprintTextRectangules);
        }
    }

    private void drawArcAroundText(Graphics2D graphics2D, int x, int y, String textToInsert) {

        double numberOfCharacters = textToInsert.length();
        double characterWidth = (fontSize / 1.6);
        double textSize = numberOfCharacters * characterWidth;

        int x1 = (int) (x - characterWidth);
        int y1 = y - (2 * fontSize);
        int width = (int) (Math.round(textSize) + (2 * characterWidth));
        int height = 4 * fontSize;
        int startAngle = 0;
        int arcAngle = 360;

        graphics2D.setColor(Color.YELLOW);
        graphics2D.fillArc(x1, y1, width, height, startAngle, arcAngle);
        graphics2D.setColor(Color.BLACK);
    }

    private void drawTextAndArc(Graphics2D graphics2D, int x, int y, Space discoveredSpace, String textToInsert) {
        if (textToInsert != null) {
            if (isSuroundingSpaceBlueprint() != null && isSuroundingSpaceBlueprint() && getThisSpace() != null
                    && discoveredSpace.equals(getThisSpace())) {

                drawArcAroundText(graphics2D, x, y, textToInsert);
                graphics2D.drawString(textToInsert, x, y);

            } else {
                graphics2D.drawString(textToInsert, x, y);
            }
        }
    }

    private static String getTextToInsert(String textToInsert, Space space, Boolean isToViewBlueprintNumbers,
            Boolean isToViewSpaceIdentifications, Boolean isToViewDoorNumbers) {

        if (space != null) {
            if (isToViewSpaceIdentifications != null && isToViewSpaceIdentifications) {

//                SpaceInformation spaceInformation = space.getSpaceInformation();
//                if (spaceInformation instanceof RoomInformation) {
//                    textToInsert = ((RoomInformation) spaceInformation).getIdentification();
//
//                } else if (spaceInformation instanceof FloorInformation) {
//                    textToInsert = ((FloorInformation) spaceInformation).getLevel().toString();
//
//                } else if (spaceInformation instanceof CampusInformation) {
//                    textToInsert = ((CampusInformation) spaceInformation).getName();
//
//                } else if (spaceInformation instanceof BuildingInformation) {
//                    textToInsert = ((BuildingInformation) spaceInformation).getName();
//                }

                try {
                    return space.getName();
                } catch (UnavailableException e) {
                    return "-";
                }

            } else if (isToViewDoorNumbers != null && isToViewDoorNumbers) {

//                SpaceInformation spaceInformation = space.getSpaceInformation();
//                if (spaceInformation instanceof RoomInformation) {
//                    textToInsert = ((RoomInformation) spaceInformation).getDoorNumber();
//                }

                try {
                    return space.getBlueprintNumber();
                } catch (UnavailableException e) {
                    return "-";
                }

            } else if (isToViewBlueprintNumbers != null && isToViewBlueprintNumbers) {
                return textToInsert;
            }
        }
        return null;
    }

    public Space getParentSpace() {
        return parentSpace;
    }

    public Boolean isToViewBlueprintNumbers() {
        return viewBlueprintNumbers;
    }

    public Boolean isToViewSpaceIdentifications() {
        return viewSpaceIdentifications;
    }

    public Boolean isToViewDoorNumbers() {
        return viewDoorNumbers;
    }

    public Boolean isSuroundingSpaceBlueprint() {
        return suroundingSpaceBlueprint;
    }

    public Boolean getViewBlueprintNumbers() {
        return viewBlueprintNumbers;
    }

    public Space getThisSpace() {
        return thisSpace;
    }

    public Boolean isToViewOriginalSpaceBlueprint() {
        return viewOriginalSpaceBlueprint;
    }

    public static void writeBlueprint(Space space, DateTime when, Boolean isSuroundingSpaceBlueprint,
            Boolean isToViewOriginalSpaceBlueprint, Boolean viewBlueprintNumbers, Boolean isToViewIdentifications,
            Boolean isToViewDoorNumbers, BigDecimal scalePercentage, final OutputStream writer) throws IOException,
            UnavailableException {

        BlueprintFile blueprintFile = space.getBlueprintFile(when);

        if (blueprintFile == null) {
            blueprintFile = space.getParent().getBlueprintFile(when);
        }

        final byte[] blueprintBytes = blueprintFile.getContent();
        final InputStream inputStream = new ByteArrayInputStream(blueprintBytes);

        SpaceBlueprintsDWGProcessor processor = null;

        if (isToViewOriginalSpaceBlueprint != null && isToViewOriginalSpaceBlueprint) {
            processor = new SpaceBlueprintsDWGProcessor(scalePercentage);

        } else {
            processor =
                    new SpaceBlueprintsDWGProcessor(space, when, viewBlueprintNumbers, isToViewIdentifications,
                            isToViewDoorNumbers, scalePercentage);
        }

        if (processor != null) {
            processor.generateJPEGImage(inputStream, writer);
        }
    }
}
