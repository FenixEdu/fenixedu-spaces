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
package org.fenixedu.spaces.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.MetadataSpec;
import org.fenixedu.spaces.domain.SpaceClassification;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class InitializeRoomClassficationTask extends CustomTask {
    final Locale LocalePT = Locale.forLanguageTag("pt-PT");
    final Locale LocaleEN = Locale.forLanguageTag("en-GB");

    Multimap<String, MetadataSpec> codeToMetadataSpecMap;

    private void initMetadataSpecMap() {
        codeToMetadataSpecMap = HashMultimap.create();
        codeToMetadataSpecMap.put(
                "Room",
                new MetadataSpec("ageQualitity", new LocalizedString.Builder().with(LocalePT, "Qualidade em idade")
                        .with(LocaleEN, "Age Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put(
                "Room",
                new MetadataSpec("distanceFromSanitaryInstalationsQuality", new LocalizedString.Builder()
                        .with(LocalePT, "Qualidade na distância às instalações sanitárias")
                        .with(LocaleEN, "Distance From Sanitary Instalations Quality").build(), java.lang.Boolean.class, true,
                        "false"));
        codeToMetadataSpecMap.put(
                "Room",
                new MetadataSpec("heightQuality", new LocalizedString.Builder().with(LocalePT, "Qualidade em altura")
                        .with(LocaleEN, "Height Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put("11",
                new MetadataSpec("illuminationQuality", new LocalizedString.Builder().with(LocalePT, "Qualidade em iluminação")
                        .with(LocaleEN, "Illumination Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put(
                "Room",
                new MetadataSpec("securityQuality", new LocalizedString.Builder().with(LocalePT, "Qualidade em segurança")
                        .with(LocaleEN, "Security Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put(
                "Room",
                new MetadataSpec("doorNumber", new LocalizedString.Builder().with(LocalePT, "Número Porta")
                        .with(LocaleEN, "Door Number").build(), java.lang.Integer.class, false, "0"));
        codeToMetadataSpecMap.put("Floor",
                new MetadataSpec("level", new LocalizedString.Builder().with(LocalePT, "Piso").with(LocaleEN, "Level").build(),
                        java.lang.Integer.class, true, "0"));

    }

    private static class ClassificationBean {
        public String name;
        public Set<ClassificationBean> childs;
        public Integer code;

        public ClassificationBean(Integer code, String name, Set<ClassificationBean> childs) {
            super();
            this.code = code;
            this.name = name;
            this.childs = childs;
        }

        public ClassificationBean(Integer code, String name) {
            this(code, name, Sets.<ClassificationBean> newHashSet());
        }
    }

    public InitializeRoomClassficationTask() {
        super();
        initMetadataSpecMap();
    }

    private static final String IMPORT_URL = "/home/sfbs/Documents/fenix-spaces/import";

    private void importClassificationsTask(Gson gson) {

        try {
            File file = new File(IMPORT_URL + "/occupations_recent.json");
            List<ClassificationBean> classificationJson;
            classificationJson = gson.fromJson(new JsonReader(new FileReader(file)), new TypeToken<List<ClassificationBean>>() {
            }.getType());
            for (ClassificationBean bean : classificationJson) {
                create(null, bean);
            }
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void create(SpaceClassification parent, ClassificationBean bean) {
        final LocalizedString name = new LocalizedString.Builder().with(LocalePT, bean.name).build();
        final String code = bean.code.toString();
        final SpaceClassification spaceClassification = new SpaceClassification(code, name, parent);
        for (ClassificationBean child : bean.childs) {
            create(spaceClassification, child);
        }
    }

    @Override
    public void runTask() throws Exception {
        Gson gson = new Gson();
        importClassificationsTask(gson);
        initSpaceTypes();
    }

    public void initSpaceTypes() {
        String[] en = new String[] { "Campus", "Room Subdivision", "Building", "Floor" };
        String[] pt = new String[] { "Campus", "Subdivisão de Sala", "Edifício", "Piso" };
        String[] codes = new String[] { "3", "4", "5", "6" };

        final SpaceClassification otherSpaces = SpaceClassification.get("11"); // other spaces

        for (int i = 0; i < codes.length; i++) {
            String name_EN = en[i];
            String name_PT = pt[i];
            String code = codes[i];
            changeOrCreate(otherSpaces, name_EN, name_PT, code);
        }
    }

    public void changeOrCreate(SpaceClassification parent, String name_EN, String name_PT, String code) {
        final LocalizedString name = new LocalizedString.Builder().with(LocalePT, name_PT).with(LocaleEN, name_EN).build();
        for (SpaceClassification classification : Bennu.getInstance().getRootClassificationSet()) {
            if (classification.getCode().equals(code)) {
                classification.setName(name);
                final String nameEn = classification.getName().getContent(LocaleEN);
                classification.setMetadataSpecs(getMetadataSpec(nameEn));
                return;
            }
        }
        final SpaceClassification spaceClassification = new SpaceClassification(code, name, parent, null);
        spaceClassification.setMetadataSpecs(getMetadataSpec(code));
    }

    private Collection<MetadataSpec> getMetadataSpec(String name) {
        return codeToMetadataSpecMap.get(name);
    }
}
