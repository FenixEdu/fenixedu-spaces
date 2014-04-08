package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringApplication(group = "anyone", path = "spaces", title = "spaces-manager")
@SpringFunctionality(app = OccupationController.class, title = "spaces-manager")
@RequestMapping("/spaces/occupations")
public class OccupationController {
    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "occupations";
    }
}
