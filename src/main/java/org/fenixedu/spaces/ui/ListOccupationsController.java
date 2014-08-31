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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fenixedu.bennu.core.security.Authenticate;
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
            @RequestParam(defaultValue = "#{new org.joda.time.DateTime().getYear()}") int year,
            @RequestParam(defaultValue = "") String name) {
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
        model.addAttribute("occupations", occupationService.getOccupations(month, year, Authenticate.getUser(), name));
        model.addAttribute("name", name);
        return "occupations/list";
    }
}
