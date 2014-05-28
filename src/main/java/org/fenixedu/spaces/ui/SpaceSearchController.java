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

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.Space;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;

@SpringFunctionality(app = SpacesController.class, title = "title.spaces.search")
@RequestMapping("/spaces/search")
public class SpaceSearchController {

    @RequestMapping
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
