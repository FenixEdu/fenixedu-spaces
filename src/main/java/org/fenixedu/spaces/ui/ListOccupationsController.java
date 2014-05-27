package org.fenixedu.spaces.ui;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = SpacesController.class, title = "title.list.occupations")
@RequestMapping("/spaces/occupations/list")
public class ListOccupationsController {

    @Autowired
    OccupationService occupationService;

    @RequestMapping
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
}
