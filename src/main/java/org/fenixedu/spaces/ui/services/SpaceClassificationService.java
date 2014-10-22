package org.fenixedu.spaces.ui.services;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.ui.SpaceClassificationBean;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;

@Service
public class SpaceClassificationService {

    public SpaceClassificationService() {

    }

    public void updateClassification(SpaceClassification classification, SpaceClassificationBean bean) {
        setMetadataSpec(classification, bean.getMetadataSpec());
        setName(classification, bean.getLocalizedName());
        setCode(classification, bean.getCode());
        setParentClassification(classification, bean.getParent());
    }

    @Atomic
    public void setMetadataSpec(SpaceClassification classification, JsonElement metadata) {
        classification.setMetadataSpec(metadata);
    }

    @Atomic
    public void setName(SpaceClassification classification, LocalizedString name) {
        classification.setName(name);
    }

    @Atomic
    public void setCode(SpaceClassification classification, String code) {
        classification.setCode(code);
    }

    @Atomic
    public void setParentClassification(SpaceClassification classification, String parent) {
        SpaceClassification parentClassification = null;
        if (parent.length() > 0) {
            parentClassification = FenixFramework.getDomainObject(parent);
        }

        classification.setParent(parentClassification);
    }
}
