package org.fenixedu.spaces.services;

import static org.fenixedu.bennu.FenixEduSpaceConfiguration.BUNDLE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;

import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ExportSpace {

    private static List<Object> getHeaders(List<String> metaKeys) {
        final List<Object> headers = new ArrayList<Object>();
        headers.add(BundleUtil.getString(BUNDLE, "export.excel.path"));
        headers.add(BundleUtil.getString(BUNDLE, "export.excel.space"));
        headers.add(BundleUtil.getString(BUNDLE, "export.excel.id"));
        headers.add(BundleUtil.getString(BUNDLE, "export.excel.blueprintNumber"));
        headers.add(BundleUtil.getString(BUNDLE, "export.excel.classification"));
        headers.add(BundleUtil.getString(BUNDLE, "export.excel.area"));
        HashMap<String, LocalizedString> showKeys = new HashMap<String, LocalizedString>();
        for (SpaceClassification spaceClassification : SpaceClassification.all()) {
            for (JsonElement je : spaceClassification.getMetadataSpec().getAsJsonArray()) {
                JsonObject attribute = je.getAsJsonObject();
                String name = attribute.get("name").getAsString();
                LocalizedString description = LocalizedString.fromJson(attribute.get("description"));
                if (showKeys.putIfAbsent(name, description) == null) {
                    metaKeys.add(name);
                }
            }
        }

        for (String header : metaKeys) {
            headers.add(showKeys.get(header).getContent());
        }
        return headers;
    }

    private static void exportToXls(Space space, OutputStream outputStream) throws IOException {
        List<String> metaKeys = new ArrayList<String>();
        final List<Object> headers = getHeaders(metaKeys);
        final Spreadsheet spreadsheet = new Spreadsheet("GestãoDeEspaços", headers);
        fillSpreadSheet(space, spreadsheet, metaKeys);
        spreadsheet.exportToXLSSheet(outputStream);
    }

    private static String StringPath(List<Space> path) {
        return path.stream().map(a -> a.getName()).collect(Collectors.joining(" > "));
    }

    private static void fillSpreadSheet(Space space, final Spreadsheet spreadsheet, List<String> metaKeys) {
        for (Space subSpace : space.getChildren()) {
            if (subSpace.isActive()) {
                final Row row = spreadsheet.addRow();

                row.setCell((subSpace.getParent() != null) ? StringPath(subSpace.getParent().getPath()) : "--");
                row.setCell(subSpace.getName());
                row.setCell(subSpace.bean().getIdentification() != null ? subSpace.bean().getIdentification() : "--");
                row.setCell(subSpace.bean().getBlueprintNumber() != null ? subSpace.bean().getBlueprintNumber() : "--");
                row.setCell(subSpace.bean().getClassification() != null ? subSpace.bean().getClassification().getName()
                        .getContent() : "--");
                row.setCell(subSpace.bean().getArea() != null ? subSpace.bean().getArea().toString() : "--");

                for (String field : metaKeys) {
                    row.setCell((subSpace.getMetadata(field).orElse("--")).toString());
                }
            }

            if (subSpace.getChildren().size() != 0) {
                fillSpreadSheet(subSpace, spreadsheet, metaKeys);
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
