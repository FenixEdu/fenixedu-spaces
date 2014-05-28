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
