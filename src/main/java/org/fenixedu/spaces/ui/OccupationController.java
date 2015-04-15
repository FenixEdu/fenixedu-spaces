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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@BennuSpringController(SpacesController.class)
@RequestMapping("/spaces/occupations")
public class OccupationController {

    private final JsonParser jsonParser = new JsonParser();

    @Autowired
    OccupationService occupationService;

    private List<Interval> parseIntervals(String events) {
        final JsonArray jsonEvents = jsonParser.parse(events).getAsJsonArray();
        List<Interval> intervals = new ArrayList<>();
        for (JsonElement jsonEvent : jsonEvents) {
            final JsonObject event = jsonEvent.getAsJsonObject();
            final Long start = Long.parseLong(event.get("start").getAsString()) * 1000L;
            final Long end = Long.parseLong(event.get("end").getAsString()) * 1000L;
            intervals.add(new Interval(new DateTime(start), new DateTime(end)));
        }
        return intervals;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String getCreate(Model model, @RequestParam(required = false) OccupationRequest request) {
        model.addAttribute("request", request);
        return "occupations/create";
    }

    public String searchSpaces(Model model, @RequestParam String events, @RequestParam String config, @RequestParam(
            required = false) OccupationRequest request, @RequestParam(required = false) String mails) {
        final List<Interval> intervals = parseIntervals(events);
        model.addAttribute("events", events);
        model.addAttribute("config", config);
        model.addAttribute("request", request);
        if (mails != null) {
            model.addAttribute("emails", mails);
        }
        model.addAttribute("freeSpaces", occupationService.searchFreeSpaces(intervals, Authenticate.getUser()));
        return "occupations/searchcreate";
    }

    @RequestMapping(value = "search-create", method = RequestMethod.POST)
    public String searchSpaces(Model model, @RequestParam String events, @RequestParam String config, @RequestParam(
            required = false) OccupationRequest request) {
        return searchSpaces(model, events, config, request, null);
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @RequestParam String emails, @RequestParam String subject,
            @RequestParam String description, @RequestParam String selectedSpaces, @RequestParam String config,
            @RequestParam String events, @RequestParam(required = false) OccupationRequest request) {
        List<String> parsedMails = new ArrayList<String>();
        for (String m : emails.split(",")) {
            if (isValidEmailAddress(m.trim())) {
                parsedMails.add(m);
            }
        }
        emails = parsedMails.stream().collect(Collectors.joining(", "));
        try {
            occupationService.createOccupation(emails, subject, description, selectedSpaces, config, events, request,
                    Authenticate.getUser());
            if (request != null) {
                return "redirect:/spaces/occupations/requests/" + request.getExternalId();
            }
            return "redirect:/spaces/occupations/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return searchSpaces(model, events, config, request, emails);
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern =
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @RequestMapping("view/{occupation}")
    public String view(Model model, @PathVariable Occupation occupation) {
        if (!occupationService.canManageOccupation(occupation, Authenticate.getUser())) {
            return "redirect:/";
        }
        model.addAttribute("occupation", occupation);
        model.addAttribute("events", occupationService.exportEvents(occupation));
        model.addAttribute("config", occupationService.exportConfig(occupation));
        model.addAttribute("freeSpaces", occupationService.getFreeAndSelectedSpaces(occupation, Authenticate.getUser()));
        return "occupations/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String edit(Model model, @RequestParam Occupation occupation, @RequestParam String emails,
            @RequestParam String subject, @RequestParam String description, @RequestParam String selectedSpaces) {
        try {
            occupationService.editOccupation(occupation, emails, subject, description, selectedSpaces, Authenticate.getUser());
            return "redirect:/spaces/occupations/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return view(model, occupation);
        }
    }

    @RequestMapping(value = "{occupation}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Occupation occupation) {
        occupationService.delete(occupation, Authenticate.getUser());
        return "redirect:/spaces/occupations/list";
    }

}
