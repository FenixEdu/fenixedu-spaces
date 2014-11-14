package org.fenixedu.spaces.bootstrap;

import java.util.List;

import org.fenixedu.bennu.core.bootstrap.BootstrapError;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.portal.domain.PortalBootstrapper;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.SpaceClassification;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Bootstrapper(sections = {}, name = "SpaceBootstrapper", bundle = "", after = PortalBootstrapper.class)
public class SpacesBootstrapper {

    void setBaseClassification() {
        JsonElement localizedName = new JsonParser().parse("{pt-PT : \"Classificação Base\"}");
        SpaceClassification baseClassification = new SpaceClassification("", LocalizedString.fromJson(localizedName));
        baseClassification.setMetadataSpec(new JsonArray());
        for (SpaceClassification exRoot : Bennu.getInstance().getRootClassificationSet()) {
            exRoot.setParent(baseClassification);
        }
        Bennu.getInstance().getRootClassificationSet().clear();
        Bennu.getInstance().getRootClassificationSet().add(baseClassification);
    }

    @Bootstrap
    public static List<BootstrapError> boostrap() {
        DynamicGroup.get("spaceSuperUsers").changeGroup(DynamicGroup.get("managers"));
        return Lists.newArrayList();
    }

}
