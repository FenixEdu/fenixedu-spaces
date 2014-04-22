package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.Space;
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

@SpringFunctionality(app = SpacesController.class, title = "title.space.occupations.requests")
@RequestMapping("/spaces/occupations/requests")
public class OccupationRequestsController {

    @Autowired
    OccupationService occupationService;

    @RequestMapping(value = "/search/{id}", method = RequestMethod.GET)
    public String search(@PathVariable Integer id, Model model) {
        model.addAttribute("occupationRequest", occupationService.search(id));
        return "occupations/requests/single";
    }

    @RequestMapping(value = "/{occupationRequest}", method = RequestMethod.GET)
    public String view(@PathVariable OccupationRequest occupationRequest, Model model) {
        model.addAttribute("occupationRequest", occupationRequest);
        return "occupations/requests/single";
    }

    @RequestMapping(value = "/{occupationRequest}/comments", method = RequestMethod.POST)
    public RedirectView addComment(@PathVariable OccupationRequest occupationRequest, @RequestParam String description,
            Model model, @RequestParam OccupationRequestState state) {
        occupationService.addComment(occupationRequest, description, state);
        return new RedirectView("/spaces/occupations/requests/" + occupationRequest.getExternalId(), true);
    }

    @RequestMapping(value = "/{occupationRequest}/{state}", method = RequestMethod.GET)
    public RedirectView changeRequestState(@PathVariable OccupationRequest occupationRequest, Model model,
            @PathVariable OccupationRequestState state) {
        if (occupationRequest.getCurrentState() != OccupationRequestState.NEW) {
            return new RedirectView("/spaces/occupations/requests/", true);
        }
        switch (state) {
        case OPEN:
            occupationService.openRequest(occupationRequest, Authenticate.getUser());
        case RESOLVED:
            occupationService.closeRequest(occupationRequest, Authenticate.getUser());
            break;
        }
        return new RedirectView("/spaces/occupations/requests/", true);
    }

    @RequestMapping(value = "/filter/{campus}", method = RequestMethod.GET)
    public String filter(@PathVariable Space campus, Model model) {
        addCampus(model);
        model.addAttribute("selectedCampi", campus);
        addRequests(model, campus);
        return "occupations/requests/view";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewRequests(Model model) {
        addCampus(model);
        addRequests(model, null);
        return "occupations/requests/view";
    }

    public void addRequests(Model model, Space campus) {
        model.addAttribute("myRequests", occupationService.getRequestsToProcess(Authenticate.getUser(), campus));
        model.addAttribute("openRequests", occupationService.all(OccupationRequestState.OPEN, campus));
        model.addAttribute("newRequests", occupationService.all(OccupationRequestState.NEW, campus));
        model.addAttribute("resolvedRequests", occupationService.all(OccupationRequestState.RESOLVED, campus));
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String showCreateForm(Model model) {
        addCampus(model);
        model.addAttribute("occupation", new OccupationRequestBean());
        return "occupations/requests/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RedirectView createRequest(@ModelAttribute OccupationRequestBean bean, BindingResult errors) {
        occupationService.createRequest(bean);
        return new RedirectView("/spaces/occupations/requests", true);
    }

    private Model addCampus(Model model) {
        return model.addAttribute("campus", occupationService.getAllCampus());
    }

}
