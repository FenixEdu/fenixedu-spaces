package org.fenixedu.spaces.ui;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
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

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String getCreate(Model model) {
        return "occupations/create";
    }

    @RequestMapping(value = "search-create", method = RequestMethod.POST)
    public String searchSpaces(Model model, @RequestParam String events, @RequestParam String config) {
        final List<Interval> intervals = parseIntervals(events);
        model.addAttribute("config", config);
        model.addAttribute("freeSpaces", occupationService.searchFreeSpaces(intervals));
        return "occupations/searchcreate";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(Model model) {
        return "occupations/create";
    }

}
