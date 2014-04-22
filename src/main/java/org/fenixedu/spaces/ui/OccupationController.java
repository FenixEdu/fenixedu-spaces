package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringFunctionality(app = SpacesController.class, title = "title.occupation.management")
@RequestMapping("/spaces/occupations")
public class OccupationController {

    @Autowired
    OccupationService occupationService;

    private Model addCampus(Model model) {
        return model.addAttribute("campus", occupationService.getAllCampus());
    }

    @RequestMapping
    public String home(Model model) {
        addCampus(model);
        return "occupations/home";
    }

}
