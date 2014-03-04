package org.fenixedu.spaces.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.spaces.domain.Information;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.domain.UnavailableException;
import org.fenixedu.spaces.services.SpaceBlueprintsDWGProcessor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Controller
@RequestMapping("/spaces")
public class SpacesController {

    @Autowired
    ConversionService conversionService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView home() {
        return home(null);
    }

    @RequestMapping(value = "{space}", method = RequestMethod.GET)
    public ModelAndView home(@PathVariable Space space) {
        Set<Space> spaces;
        if (space == null) {
            spaces = Bennu.getInstance().getSpaceSet();
        } else {
            spaces = space.getValidChildrenSet();
        }
        return new ModelAndView("home", "spaces", spaces);
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) throws UnavailableException {
        return create(null, model);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public RedirectView create(@ModelAttribute InformationBean infoBean, BindingResult errors) {
        return create(null, infoBean, errors);
    }

    @RequestMapping(value = "/create/{space}", method = RequestMethod.GET)
    public String create(@PathVariable Space space, Model model) throws UnavailableException {
        if (space == null) {
            model.addAttribute("action", "/spaces/create");
        } else {
            model.addAttribute("action", "/spaces/create/" + space.getExternalId());
            model.addAttribute("parentSpace", space.bean());
        }
        model.addAttribute("information", new InformationBean());
        model.addAttribute("classifications", SpaceClassification.all());
        return "spaces/create";
    }

    @RequestMapping(value = "/create/{space}", method = RequestMethod.POST)
    public RedirectView create(@PathVariable Space space, @ModelAttribute InformationBean infoBean, BindingResult errors) {
        create(space, infoBean);
        if (space == null) {
            return new RedirectView("/spaces", true);
        }
        return new RedirectView("/spaces/" + space.getExternalId(), true);
    }

    @Atomic(mode = TxMode.WRITE)
    private void create(Space space, InformationBean infoBean) {
        final Information information = new Information.Builder(infoBean).build();
        new Space(space, information);
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.GET)
    public String edit(@PathVariable Space space, Model model) throws UnavailableException {
        model.addAttribute("information", space.bean());
        model.addAttribute("classifications", SpaceClassification.all());
        model.addAttribute("action", "/spaces/edit/" + space.getExternalId());
        return "spaces/create";
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.POST)
    public ModelAndView edit(@PathVariable Space space, @ModelAttribute InformationBean informationBean, BindingResult errors)
            throws UnavailableException {
        space.bean(informationBean);
        return home();
    }

    @RequestMapping(value = "/view/{space}", method = RequestMethod.GET)
    public String view(@PathVariable Space space, Model model) throws UnavailableException {
        model.addAttribute("information", space.bean());
        model.addAttribute("spaces", space.getValidChildrenSet());
        model.addAttribute("parentSpace", space.getParent());
        return "spaces/view";
    }

    @RequestMapping(value = "/timeline/{space}", method = RequestMethod.GET)
    public String timeline(@PathVariable Space space, Model model) throws UnavailableException {
        model.addAttribute("timeline", space.timeline());
        if (space.getParent() != null) {
            model.addAttribute("parent", space.getParent().bean());
        }
        return "spaces/timeline";
    }

    @ResponseBody
    @RequestMapping(value = "/{space}", method = RequestMethod.DELETE)
    public String delete(@PathVariable() Space space) throws UnavailableException {
        space.delete();
        return "ok";
    }

    @RequestMapping(value = "/blueprint/{space}", method = RequestMethod.GET)
    public void blueprint(@PathVariable Space space, @DateTimeFormat(pattern = InformationBean.DATE_FORMAT) @RequestParam(
            defaultValue = "#{new org.joda.time.DateTime()}") DateTime when, @RequestParam(defaultValue = "50") BigDecimal scale,
            HttpServletResponse response) throws IOException, UnavailableException {
        Boolean isSuroundingSpaceBlueprint = false;
        Boolean isToViewOriginalSpaceBlueprint = false;
        Boolean viewBlueprintNumbers = true;
        Boolean isToViewIdentifications = true;
        Boolean isToViewDoorNumbers = false;
        BigDecimal scalePercentage = scale;
        try (OutputStream outputStream = response.getOutputStream()) {
            SpaceBlueprintsDWGProcessor.writeBlueprint(space, when, isSuroundingSpaceBlueprint, isToViewOriginalSpaceBlueprint,
                    viewBlueprintNumbers, isToViewIdentifications, isToViewDoorNumbers, scalePercentage, outputStream);
        }
    }
}
