package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@SpringFunctionality(app = SpacesController.class, title = "title.view.my.occupations")
@RequestMapping("/spaces/occupations/requests/my")
public class MyOccupationRequestController {

    @Autowired
    OccupationService occupationService;

    @RequestMapping
    public String myRequests(Model model, @RequestParam(defaultValue = "1") String p) {
        model.addAttribute("requestor", Authenticate.getUser());
        model.addAttribute("requests", occupationService.getBook(occupationService.all(Authenticate.getUser()), p));
        return "occupations/requests/my";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        model.addAttribute("campus", occupationService.getAllCampus());
        model.addAttribute("occupation", new OccupationRequestBean());
        return "occupations/requests/create";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public RedirectView createRequest(@ModelAttribute OccupationRequestBean bean, BindingResult errors) {
        occupationService.createRequest(bean);
        return new RedirectView("/spaces/occupations/requests/my", true);
    }

    @RequestMapping(value = "/{occupationRequest}", method = RequestMethod.GET)
    public String view(@PathVariable OccupationRequest occupationRequest, Model model) {
        model.addAttribute("occupationRequest", occupationRequest);
        return "occupations/requests/mysingle";
    }

    @RequestMapping(value = "/{occupationRequest}/comments", method = RequestMethod.POST)
    public RedirectView addComment(@PathVariable OccupationRequest occupationRequest, @RequestParam String description,
            Model model, @RequestParam OccupationRequestState state) {
        occupationService.addComment(occupationRequest, description, state);
        return new RedirectView("/spaces/occupations/requests/my/" + occupationRequest.getExternalId(), true);
    }

}
