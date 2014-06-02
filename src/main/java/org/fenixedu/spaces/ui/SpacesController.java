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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.BlueprintFile;
import org.fenixedu.spaces.domain.BlueprintFile.BlueprintTextRectangles;
import org.fenixedu.spaces.domain.Information;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.services.SpaceBlueprintsDWGProcessor;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.base.Strings;

@SpringApplication(group = "anyone", path = "spaces", title = "title.space.management", hint = "spaces-manager")
@SpringFunctionality(app = SpacesController.class, title = "title.space.management")
@RequestMapping("/spaces")
public class SpacesController {

    private BlueprintTextRectangles getBlueprintTextRectangles(Space space, BigDecimal scale) {
        DateTime now = new DateTime();

        Space spaceWithBlueprint = SpaceBlueprintsDWGProcessor.getSuroundingSpaceMostRecentBlueprint(space);

        BlueprintFile mostRecentBlueprint = spaceWithBlueprint.getBlueprintFile().get();

        if (mostRecentBlueprint != null) {

            final byte[] blueprintBytes = mostRecentBlueprint.getContent();
            final InputStream inputStream = new ByteArrayInputStream(blueprintBytes);
            try {
                return SpaceBlueprintsDWGProcessor.getBlueprintTextRectangles(inputStream, spaceWithBlueprint, now, false, false,
                        true, false, scale);
            } catch (IOException e) {
                return null;
            }
        }

        return null;
    }

    private Set<Space> getTopLevelSpaces() {
        return Space.getSpaces().filter(space -> space.getParent() == null).collect(Collectors.toSet());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        return home(null, model);
    }

    @RequestMapping(value = "{space}", method = RequestMethod.GET)
    public String home(@PathVariable Space space, Model model) {
        model.addAttribute("spaces", space == null ? getTopLevelSpaces() : getChildrenOrderedByName(space));
        model.addAttribute("currentUser", Authenticate.getUser());
        return "spaces/home";
    }

    Comparator<Space> BY_NAME_COMPARATOR = new Comparator<Space>() {

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
            model.addAttribute("parentSpace", space.bean());
        }
        model.addAttribute("information", new InformationBean());
        model.addAttribute("classifications", allClassifications());
        model.addAttribute("currentUser", Authenticate.getUser());
        return "spaces/create";
    }

    private List<SpaceClassification> allClassifications() {
        return SpaceClassification.all().stream().sorted((c1, c2) -> c1.getAbsoluteCode().compareTo(c2.getAbsoluteCode()))
                .collect(Collectors.toList());
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
        return space.isSpaceManagementMember(Authenticate.getUser());
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
        new Space(space, information);
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.GET)
    public String edit(@PathVariable Space space, Model model) throws UnavailableException {
        model.addAttribute("information", space.bean());
        model.addAttribute("classifications", allClassifications());
        model.addAttribute("currentUser", Authenticate.getUser());
        model.addAttribute("action", "/spaces/edit/" + space.getExternalId());
        return "spaces/create";
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.POST)
    public String edit(@PathVariable Space space, @ModelAttribute InformationBean informationBean, BindingResult errors)
            throws UnavailableException {
        canWrite(space);
        space.bean(informationBean);
        return "redirect:/spaces/view/" + space.getExternalId();
    }

    @RequestMapping(value = "/view/{space}", method = RequestMethod.GET)
    public String view(@PathVariable Space space, Model model, @RequestParam(defaultValue = "50") BigDecimal scale)
            throws UnavailableException {
        model.addAttribute("scale", scale);
        model.addAttribute("information", space.bean());
        model.addAttribute("blueprintTextRectangles", getBlueprintTextRectangles(space, scale));
        model.addAttribute("spaces", getChildrenOrderedByName(space));
        model.addAttribute("parentSpace", space.getParent());
        model.addAttribute("currentUser", Authenticate.getUser());
        return "spaces/view";
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
    public String access(@PathVariable Space space, Model model) throws UnavailableException {
        model.addAttribute("space", space);
        model.addAttribute("localOccupationsGroup", space.getOccupationsGroup());
        model.addAttribute("localManagementGroup", space.getManagementGroup());
        model.addAttribute("chainOccupationsGroup", space.getOccupationsGroupWithChainOfResponsability());
        model.addAttribute("chainManagementGroup", space.getManagementGroupWithChainOfResponsability());
        return "spaces/access";
    }

    @RequestMapping(value = "/access/{space}", method = RequestMethod.POST)
    public String changeAccess(@PathVariable Space space, Model model) throws UnavailableException {
        return "spaces/timeline";
    }

    @ResponseBody
    @RequestMapping(value = "/{space}", method = RequestMethod.DELETE)
    public String delete(@PathVariable() Space space) throws UnavailableException {
        space.delete();
        return "ok";
    }

    @RequestMapping(value = "/blueprint/{space}", method = RequestMethod.GET)
    public void blueprint(@PathVariable Space space, @DateTimeFormat(pattern = InformationBean.DATE_FORMAT) @RequestParam(
            defaultValue = "#{new org.joda.time.DateTime()}") DateTime when, @RequestParam(defaultValue = "50") BigDecimal scale,
            HttpServletResponse response) throws IOException, UnavailableException {
        Boolean isToViewOriginalSpaceBlueprint = false;
        Boolean viewBlueprintNumbers = true;
        Boolean isToViewIdentifications = true;
        Boolean isToViewDoorNumbers = false;
        BigDecimal scalePercentage = scale;
        try (OutputStream outputStream = response.getOutputStream()) {
            SpaceBlueprintsDWGProcessor.writeBlueprint(space, when, isToViewOriginalSpaceBlueprint, viewBlueprintNumbers,
                    isToViewIdentifications, isToViewDoorNumbers, scalePercentage, outputStream);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("name", name);
        if (!Strings.isNullOrEmpty(name)) {
            model.addAttribute("foundSpaces", findSpace(name));
        }
        return "spaces/search";
    }

    private Set<Space> findSpace(String text) {
        return Space.getSpaces().filter(s -> s.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toSet());
    }
}
