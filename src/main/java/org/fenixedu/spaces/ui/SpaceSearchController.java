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
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.services.SpaceBlueprintsDWGProcessor;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

@SpringApplication(group = "anyone", path = "spaces-view", title = "title.space.management.view", hint = "spaces-manager")
@SpringFunctionality(app = SpacesController.class, title = "title.spaces.search")
@RequestMapping("/spaces-view")
public class SpaceSearchController {

    @Autowired
    private OccupationService occupationService;

    @RequestMapping
    public String home(@RequestParam(required = false) String name, Model model) {
        return search(name, model);
    }

    @RequestMapping(value = "/search")
    public String search(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("name", name);
        if (!Strings.isNullOrEmpty(name)) {
            model.addAttribute("foundSpaces", findSpace(name));
        }
        return "spaces-view/search";
    }

    private Set<Space> findSpace(String text) {
        return Space.getSpaces().filter(s -> s.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toSet());
    }

    @RequestMapping(value = "/schedule/{space}")
    public String schedule(@PathVariable Space space, Model model) {
        model.addAttribute("space", space);
        return "spaces-view/schedule";
    }

    @RequestMapping(value = "/schedule/{space}/events", produces = "application/json; charset=utf-8")
    public @ResponseBody String schedule(@PathVariable Space space, @RequestParam(required = false) String start, @RequestParam(
            required = false) String end, Model model) {
        DateTime beginDate;
        DateTime endDate;

        if (Strings.isNullOrEmpty(start)) {
            DateTime now = new DateTime();
            beginDate = now.withDayOfWeek(DateTimeConstants.MONDAY).withHourOfDay(0).withMinuteOfHour(0);
            endDate = now.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1).withHourOfDay(0).withMinuteOfHour(0);
        } else {
            beginDate = new DateTime(Long.parseLong(start) * 1000);
            endDate = new DateTime(Long.parseLong(end) * 1000);
        }

        return occupationService.getOccupations(space, new Interval(beginDate, endDate));
    }

    private BlueprintTextRectangles getBlueprintTextRectangles(Space space, BigDecimal scale) {
        DateTime now = new DateTime();

        Space spaceWithBlueprint = SpaceBlueprintsDWGProcessor.getSuroundingSpaceMostRecentBlueprint(space);

        if (spaceWithBlueprint != null) {
            BlueprintFile mostRecentBlueprint = spaceWithBlueprint.getBlueprintFile().get();

            if (mostRecentBlueprint != null) {

                final byte[] blueprintBytes = mostRecentBlueprint.getContent();
                final InputStream inputStream = new ByteArrayInputStream(blueprintBytes);
                try {
                    return SpaceBlueprintsDWGProcessor.getBlueprintTextRectangles(inputStream, spaceWithBlueprint, now, false,
                            false, true, false, scale);
                } catch (IndexOutOfBoundsException e) {
                    return null;
                } catch (IOException e) {
                    return null;
                }
            }
        }

        return null;
    }

    private List<Space> getChildrenOrderedByName(Space space) {
        return space.getChildren().stream().sorted(SpacesController.BY_NAME_COMPARATOR).collect(Collectors.toList());
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

}
