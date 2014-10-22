package org.fenixedu.spaces.bootstrap;

import java.util.List;

import org.fenixedu.bennu.core.bootstrap.BootstrapError;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.portal.domain.PortalBootstrapper;

import com.google.common.collect.Lists;

@Bootstrapper(sections = {}, name = "SpaceBootstrapper", bundle = "", after = PortalBootstrapper.class)
public class SpacesBootstrapper {
    @Bootstrap
    public static List<BootstrapError> boostrap() {
        DynamicGroup.get("spaceSuperUsers").changeGroup(DynamicGroup.get("managers"));
        return Lists.newArrayList();
    }
}
