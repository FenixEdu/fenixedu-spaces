package org.fenixedu.spaces.tasks;

import java.util.Collection;
import java.util.Locale;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.MetadataSpec;
import org.fenixedu.spaces.domain.SpaceClassification;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class InitializeRoomClassficationTask extends CustomTask {
    final Locale LocalePT = Locale.forLanguageTag("pt-PT");
    final Locale LocaleEN = Locale.forLanguageTag("en-GB");
    Multimap<String, MetadataSpec> codeToMetadataSpecMap;

    private void initMetadataSpecMap() {
        codeToMetadataSpecMap = HashMultimap.create();
        codeToMetadataSpecMap.put(
                "11",
                new MetadataSpec("ageQualitity", new LocalizedString.Builder().with(LocalePT, "Qualidade em idade")
                        .with(LocaleEN, "Age Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put(
                "11",
                new MetadataSpec("distanceFromSanitaryInstalationsQuality", new LocalizedString.Builder()
                        .with(LocalePT, "Qualidade na distância às instalações sanitárias")
                        .with(LocaleEN, "Distance From Sanitary Instalations Quality").build(), java.lang.Boolean.class, true,
                        "false"));
        codeToMetadataSpecMap.put(
                "11",
                new MetadataSpec("heightQuality", new LocalizedString.Builder().with(LocalePT, "Qualidade em altura")
                        .with(LocaleEN, "Height Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put("11",
                new MetadataSpec("illuminationQuality", new LocalizedString.Builder().with(LocalePT, "Qualidade em iluminação")
                        .with(LocaleEN, "Illumination Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put(
                "11",
                new MetadataSpec("securityQuality", new LocalizedString.Builder().with(LocalePT, "Qualidade em segurança")
                        .with(LocaleEN, "Security Quality").build(), java.lang.Boolean.class, true, "false"));
        codeToMetadataSpecMap.put(
                "11",
                new MetadataSpec("doorNumber", new LocalizedString.Builder().with(LocalePT, "Número Porta")
                        .with(LocaleEN, "Door Number").build(), java.lang.Integer.class, false, "0"));
        codeToMetadataSpecMap.put("14",
                new MetadataSpec("level", new LocalizedString.Builder().with(LocalePT, "Piso").with(LocaleEN, "Level").build(),
                        java.lang.Integer.class, true, "0"));

    }

    public InitializeRoomClassficationTask() {
        super();
        initMetadataSpecMap();
    }

    @Override
    public void runTask() throws Exception {

        String[] en = new String[] { "Campus", "Room", "Room Subdivision", "Building", "Floor" };
        String[] pt = new String[] { "Campus", "Sala", "Subdivisão de Sala", "Edifício", "Piso" };
        String[] codes = new String[] { "10", "11", "12", "13", "14" };

        for (int i = 0; i < codes.length; i++) {
            String name_EN = en[i];
            String name_PT = pt[i];
            String code = codes[i];
            changeOrCreate(name_EN, name_PT, code);
        }
    }

    public void changeOrCreate(String name_EN, String name_PT, String code) {
        final LocalizedString name = new LocalizedString.Builder().with(LocalePT, name_PT).with(LocaleEN, name_EN).build();
        for (SpaceClassification classification : Bennu.getInstance().getRootClassificationSet()) {
            if (classification.getCode().equals(code)) {
                classification.setName(name);
                classification.setMetadataSpecs(getMetadataSpec(code));
                return;
            }
        }
        final SpaceClassification spaceClassification = new SpaceClassification(code, name);
        spaceClassification.setMetadataSpecs(getMetadataSpec(code));
    }

    private Collection<MetadataSpec> getMetadataSpec(String code) {
        return codeToMetadataSpecMap.get("code");
    }
}
