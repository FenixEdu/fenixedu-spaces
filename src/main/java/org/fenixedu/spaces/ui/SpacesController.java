package org.fenixedu.spaces.ui;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.spaces.domain.Information;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.domain.UnavailableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

@Controller
@RequestMapping("/spaces")
public class SpacesController {

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("spaces", Bennu.getInstance().getRootSpaceSet());
        return "hello";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("action", "/spaces/create");
        model.addAttribute("information", new InformationBean());
        model.addAttribute("classifications", SpaceClassification.all());
        return "spaces/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Atomic(mode = TxMode.WRITE)
    public View create(@ModelAttribute InformationBean infoBean, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new RuntimeException("error");
        }
        final Information information = new Information.Builder(infoBean).build();
        new Space(information);
        return new RedirectView("/", true);
    }

    @RequestMapping(value = "/edit/{spaceId}", method = RequestMethod.GET)
    public String edit(@PathVariable("spaceId") String spaceId, Model model) throws UnavailableException {
        final Space space = FenixFramework.getDomainObject(spaceId);
        model.addAttribute("information", space.bean());
        model.addAttribute("classifications", SpaceClassification.all());
        model.addAttribute("action", "/spaces/edit/" + spaceId);
        return "spaces/create";
    }

    @RequestMapping(value = "/edit/{spaceId}", method = RequestMethod.POST)
    public View edit(@PathVariable("spaceId") String spaceId, @ModelAttribute InformationBean informationBean,
            BindingResult errors) throws UnavailableException {
        final Space space = FenixFramework.getDomainObject(spaceId);
        space.bean(informationBean);
        return new RedirectView("/", true);
    }

}
