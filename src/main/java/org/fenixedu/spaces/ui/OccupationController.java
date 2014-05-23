package org.fenixedu.spaces.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.Partial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SpringFunctionality(app = SpacesController.class, title = "title.occupation.management")
@RequestMapping("/spaces/occupations")
public class OccupationController {

    private final JsonParser jsonParser = new JsonParser();

    @Autowired
    OccupationService occupationService;

    private Model addCampus(Model model) {
        return model.addAttribute("campus", occupationService.getAllCampus());
    }

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

    @RequestMapping
    public String home(Model model) {
        addCampus(model);
        return "occupations/home";
    }

    @RequestMapping("list")
    public String list(Model model, @RequestParam(defaultValue = "#{new org.joda.time.DateTime().getMonthOfYear()}") int month,
            @RequestParam(defaultValue = "#{new org.joda.time.DateTime().getYear()}") int year) {
        DateTime now = new DateTime();
        int currentYear = now.getYear();
        model.addAttribute(
                "years",
                IntStream.rangeClosed(currentYear - 100, currentYear + 10).boxed().sorted((o1, o2) -> o2.compareTo(o1))
                        .collect(Collectors.toList()));

        List<Partial> months =
                IntStream.rangeClosed(1, 12).boxed().map(m -> new Partial(DateTimeFieldType.monthOfYear(), m))
                        .collect(Collectors.toList());
        model.addAttribute("months", months);
        model.addAttribute("currentMonth", month);
        model.addAttribute("currentYear", year);
        model.addAttribute("occupations", occupationService.getOccupations(month, year));
        return "occupations/list";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String getCreate(Model model) {
        return "occupations/create";
    }

    @RequestMapping(value = "search-create", method = RequestMethod.POST)
    public String searchSpaces(Model model, @RequestParam String events, @RequestParam String config) {
        final List<Interval> intervals = parseIntervals(events);
        model.addAttribute("events", events);
        model.addAttribute("config", config);
        model.addAttribute("freeSpaces", occupationService.searchFreeSpaces(intervals, Authenticate.getUser()));
        return "occupations/searchcreate";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model, @RequestParam String emails, @RequestParam String subject,
            @RequestParam String description, @RequestParam String selectedSpaces, @RequestParam String config,
            @RequestParam String events) {
        try {
            occupationService.createOccupation(emails, subject, description, selectedSpaces, config, events);
            return "redirect:/spaces/occupations";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return searchSpaces(model, events, config);
        }
    }

}
