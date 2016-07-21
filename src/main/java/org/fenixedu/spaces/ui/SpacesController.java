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
package org.fenixedu.spaces.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.UnavailableException;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.NobodyGroup;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.Information;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.domain.submission.SpacePhoto;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@SpringApplication(group = "anyone", path = "spaces", title = "title.space.management", hint = "spaces-manager")
@SpringFunctionality(app = SpacesController.class, title = "title.space.management", accessGroup = "spaceManager")
@RequestMapping("/spaces")
public class SpacesController {

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        return home(null, model);
    }

    @RequestMapping(value = "{space}", method = RequestMethod.GET)
    public String home(@PathVariable Space space, Model model) {
        model.addAttribute("spaces", space == null ? Space.getTopLevelSpaces() : getChildrenOrderedByName(space));
        model.addAttribute("currentUser", Authenticate.getUser());
        model.addAttribute("isSpaceSuperUser", DynamicGroup.get("spaceSuperUsers").isMember(Authenticate.getUser()));
        return "spaces/home";
    }

    static Comparator<Space> BY_NAME_COMPARATOR = new Comparator<Space>() {

        @Override
        public int compare(Space o1, Space o2) {
            String o1Name = o1.getName();
            String o2Name = o2.getName();

            try {
                Integer o1Number = Integer.parseInt(o1Name);
                Integer o2Number = Integer.parseInt(o2Name);
                return o1Number.compareTo(o2Number);
            } catch (NumberFormatException fe) {
            }

            return o1Name.compareTo(o2Name);
        }

    };

    private List<Space> getChildrenOrderedByName(Space space) {
        return space.getChildren().stream().sorted(BY_NAME_COMPARATOR).collect(Collectors.toList());
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) throws UnavailableException {
        return create(null, model);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RedirectView create(@ModelAttribute InformationBean infoBean, BindingResult errors) {
        return create(null, infoBean, errors);
    }

    @RequestMapping(value = "/create/{space}", method = RequestMethod.GET)
    public String create(@PathVariable Space space, Model model) {
        if (space == null) {
            model.addAttribute("action", "/spaces/create");
        } else {
            model.addAttribute("action", "/spaces/create/" + space.getExternalId());
            model.addAttribute("parentSpace", space);
        }
        model.addAttribute("information", new InformationBean());
        model.addAttribute("classifications", SpaceClassification.all());
        model.addAttribute("currentUser", Authenticate.getUser());
        return "spaces/create";
    }

    @RequestMapping(value = "/create/{space}", method = RequestMethod.POST)
    public RedirectView create(@PathVariable Space space, @ModelAttribute InformationBean infoBean, BindingResult errors) {
        create(space, infoBean);
        if (space == null) {
            return new RedirectView("/spaces", true);
        }
        return new RedirectView("/spaces/" + space.getExternalId(), true);
    }

    private boolean accessControl(Space space) {
        User currentUser = Authenticate.getUser();
        if (space == null) {
            return DynamicGroup.get("spaceSuperUsers").isMember(currentUser);
        }
        return space.isSpaceManagementMember(currentUser);
    }

    private void canWrite(Space space) {
        if (!accessControl(space)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private void create(Space space, InformationBean infoBean) {
        canWrite(space);
        final Information information = new Information.Builder(infoBean).build();
        Space newSpace = new Space(space, information);
        if (space == null) {
            newSpace.setManagementAccessGroup(DynamicGroup.get("spaceSuperUsers"));
            newSpace.setOccupationsAccessGroup(DynamicGroup.get("spaceSuperUsers"));
        }
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.GET)
    public String edit(@PathVariable Space space, Model model) throws UnavailableException {
        InformationBean bean = space.bean();
        bean.setValidFrom(new DateTime());
        model.addAttribute("information", bean);
        model.addAttribute("classifications", SpaceClassification.all());
        model.addAttribute("currentUser", Authenticate.getUser());
        model.addAttribute("action", "/spaces/edit/" + space.getExternalId());
        return "spaces/create";
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.POST)
    public String edit(@PathVariable Space space, @ModelAttribute InformationBean informationBean, BindingResult errors)
            throws UnavailableException {
        canWrite(space);
        if (space.getBlueprintFile().isPresent() && informationBean.getBlueprintContent() == null) {
            informationBean.setBlueprint(space.getBlueprintFile().get());
        }
        informationBean.setSpacePhotoSet(space.getSpacePhotoSet().orElse(Collections.<SpacePhoto> emptySet()));
        space.bean(informationBean);
        return "redirect:/spaces-view/view/" + space.getExternalId();
    }

    @RequestMapping(value = "/timeline/{space}", method = RequestMethod.GET)
    public String timeline(@PathVariable Space space, Model model) throws UnavailableException {
        model.addAttribute("currentUser", Authenticate.getUser());
        model.addAttribute("timeline", space.timeline());
        if (space.getParent() != null) {
            model.addAttribute("parent", space.getParent().bean());
        }
        return "spaces/timeline";
    }

    @RequestMapping(value = "/access/{space}", method = RequestMethod.GET)
    public String accessManagement(@PathVariable Space space, Model model) throws UnavailableException {
        canWrite(space);
        SpaceAccessBean accessBean = new SpaceAccessBean();
        accessBean.setManagementGroup(space.getManagementGroup() != null ? space.getManagementGroup() : NobodyGroup.get());
        accessBean.setOccupationGroup(space.getOccupationsGroup() != null ? space.getOccupationsGroup() : NobodyGroup.get());
        model.addAttribute("spacebean", accessBean);
        model.addAttribute("space", space);
        model.addAttribute("action", "/spaces/access/" + space.getExternalId());
        model.addAttribute("managementGroups", Bennu.getInstance().getGroupForSpacesSet());
        return "spaces/access";
    }

    @Atomic(mode = TxMode.WRITE)
    private void changeAccess(Space space, SpaceAccessBean sab) {
        space.setManagementAccessGroup(sab.getManagementGroup());
        space.setOccupationsAccessGroup(sab.getOccupationGroup());
        return;
    }

    @RequestMapping(value = "/access/{space}", method = RequestMethod.POST)
    public String changeAccess(@PathVariable Space space, @ModelAttribute SpaceAccessBean spacebean, BindingResult errors)
            throws UnavailableException {
        canWrite(space);
        changeAccess(space, spacebean);
        return "redirect:/spaces/access/" + space.getExternalId();
    }

    @ResponseBody
    @RequestMapping(value = "/{space}", method = RequestMethod.DELETE)
    public String delete(@PathVariable() Space space) throws UnavailableException {
        space.delete();
        return "ok";
    }

}
