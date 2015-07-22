package org.fenixedu.spaces.bootstrap;

import java.util.List;

import org.fenixedu.bennu.core.bootstrap.BootstrapError;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.portal.domain.PortalBootstrapper;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.spaces.domain.SpaceClassification;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;

@Bootstrapper(sections = {}, name = "SpaceBootstrapper", bundle = "", after = PortalBootstrapper.class)
public class SpacesBootstrapper {

    private static void setBaseClassification() {
        SpaceClassification baseClassification =
                new SpaceClassification("", new LocalizedString.Builder().with(I18N.getLocale(), "Base").build());
        baseClassification.setMetadataSpec(new JsonArray());
        Bennu.getInstance().getRootClassificationSet().clear();
        Bennu.getInstance().getRootClassificationSet().add(baseClassification);
    }

    @Bootstrap
    public static List<BootstrapError> boostrap() {
        Group.dynamic("spaceSuperUsers").mutator().changeGroup(Group.managers());
        setBaseClassification();
        return Lists.newArrayList();
    }

}
