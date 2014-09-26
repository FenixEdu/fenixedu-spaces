package org.fenixedu.spaces.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;

import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

public class ExportSpace {
    public static final String CAMPUS = "Campus";
    public static final String BUILDING = "Building";
    public static final String FLOOR = "Floor";

    private static List<Object> getHeaders() {
        final List<Object> headers = new ArrayList<Object>();

        headers.add("Edifício");
        headers.add("Piso");
        headers.add("Espaço");
        headers.add("Identificação do Espaço");
        headers.add("Número na Porta");
        headers.add("Número na Planta");
        headers.add("Classificação");
        headers.add("Área");

        headers.add("Qualid. em Pé Direito");
        headers.add("Qualid. em Iluminação");
        headers.add("Qualid. em Dist. às Instalações Sanitárias");
        headers.add("Qualid. em Segurança");
        headers.add("Qualid. em Vetustez");

        //     headers.add("Unidade(s) responsáveis");
        //     headers.add("Ocupantes (Unidades)");
        //     headers.add("Ocupantes (Pessoas)");

        headers.add("Observações");

        return headers;
    }

    private static void exportToXls(Space space, OutputStream outputStream) throws IOException {
        final List<Object> headers = getHeaders();
        final Spreadsheet spreadsheet = new Spreadsheet("GestãoDeEspaços", headers);
        fillSpreadSheet(space, spreadsheet);
        spreadsheet.exportToXLSSheet(outputStream);
    }

    public static boolean isCampus(Space space) {
        return SpaceClassification.getByName(CAMPUS).equals(space.getClassification());
    }

    public static boolean isBuilding(Space space) {
        return SpaceClassification.getByName(BUILDING).equals(space.getClassification());
    }

    public static boolean isFloor(Space space) {
        return SpaceClassification.getByName(FLOOR).equals(space.getClassification());
    }

    public static Space getSpaceFloor(Space space) {
        if (isFloor(space)) {
            if (space.getParent() == null) {
                return space;
            } else if (isFloor(space.getParent())) {
                return getSpaceFloor(space.getParent());
            } else {
                return space;
            }
        }
        if (space.getParent() == null) {
            return null;
        }
        return getSpaceFloor(space.getParent());
    }

    private static void fillSpreadSheet(Space space, final Spreadsheet spreadsheet) {
        for (Space subSpace : space.getChildren()) {
            if (subSpace.isActive()) {
                Space room = subSpace;
                final Row row = spreadsheet.addRow();

                row.setCell(space.getName());

                Space spaceFloor = getSpaceFloor(subSpace);
                row.setCell((spaceFloor != null) ? spaceFloor.getName() : "--");

                row.setCell((subSpace.getMetadata("description").orElse("--")).toString());
                row.setCell(subSpace.bean().getIdentification() != null ? subSpace.bean().getIdentification() : "--");
                row.setCell((subSpace.getMetadata("doorNumber").orElse("--")).toString());
                row.setCell(subSpace.bean().getBlueprintNumber() != null ? subSpace.bean().getBlueprintNumber() : "--");
                row.setCell(subSpace.bean().getClassification() != null ? subSpace.bean().getClassification().getName()
                        .toString() : "--");
                row.setCell(subSpace.bean().getArea() != null ? subSpace.bean().getArea().toString() : "--");

                row.setCell((subSpace.getMetadata("heightQuality").orElse("--")).toString());
                row.setCell((subSpace.getMetadata("illuminationQuality").orElse("--")).toString());
                row.setCell((subSpace.getMetadata("distanceFromSanitaryInstalationsQuality").orElse("--")).toString());
                row.setCell((subSpace.getMetadata("securityQuality").orElse("--")).toString());
                row.setCell((subSpace.getMetadata("ageQualitity").orElse("--")).toString());

//                StringBuilder builder = new StringBuilder();
//                for (ResourceResponsibility responsibility : room.getResourceResponsibility()) {
//                    if (responsibility.isSpaceResponsibility()) {
//                        Unit unit = ((SpaceResponsibility) responsibility).getUnit();
//                        builder.append(unit.getPresentationName()).append("; ");
//                    }
//                }
//                row.setCell("--");

//                builder = new StringBuilder();
//                for (UnitSpaceOccupation occupation : room.getUnitSpaceOccupations()) {
//                    Unit unit = occupation.getUnit();
//                    builder.append(unit.getPresentationName()).append("; ");
//                }
                //               row.setCell("--");

//                builder = new StringBuilder();
//                for (PersonSpaceOccupation occupation : room.getPersonSpaceOccupations()) {
//                    Person person = occupation.getPerson();
//                    builder.append(person.getName() + " (" + person.getUsername() + "); ");
//                }
                //               row.setCell("--");
                row.setCell((String) subSpace.getMetadata("observations").orElse("--"));
            }

            if (subSpace.getChildren().size() != 0) {
                fillSpreadSheet(subSpace, spreadsheet);
            }
        }
    }

    public static void run(Space space, OutputStream outputStream) {
        try {
            exportToXls(space, outputStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
