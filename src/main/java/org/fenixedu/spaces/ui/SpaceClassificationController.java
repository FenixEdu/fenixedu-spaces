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

import javax.servlet.UnavailableException;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.ui.services.SpaceClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = SpacesController.class, title = "title.space.classification.management")
@RequestMapping("/classification")
public class SpaceClassificationController {

    @Autowired
    SpaceClassificationService spaceClassificationService;

    @RequestMapping
    public String home(Model model) {
        return listClassifications(model);
    }

    private List<SpaceClassification> allClassifications() {
        return SpaceClassification.all().stream().sorted((c1, c2) -> c1.getAbsoluteCode().compareTo(c2.getAbsoluteCode()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/list")
    public String listClassifications(Model model) {
        model.addAttribute("classifications", allClassifications());
        return "classification/list";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model) throws UnavailableException {
        return create(null, model);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String edit(@ModelAttribute SpaceClassificationBean information, BindingResult errors, Model model) {
        return create(null, information, errors, model);
    }

    @Atomic(mode = TxMode.WRITE)
    private SpaceClassification create(SpaceClassificationBean infoBean) {
        SpaceClassification sc = null;
        if (infoBean.getParent().length() > 0) {
            sc = FenixFramework.getDomainObject(infoBean.getParent());
        }
        return new SpaceClassification(infoBean.getCode(), infoBean.getLocalizedName(), sc, infoBean.getMetadataSpec());
    }

    @RequestMapping(value = "/edit/{classification}", method = RequestMethod.GET)
    public String create(@PathVariable SpaceClassification classification, Model model) {
        return create(classification, model, false);
    }

    public String create(SpaceClassification classification, Model model, boolean newInfo) {
        SpaceClassificationBean scb = null;
        if (classification == null) {
            model.addAttribute("action", "/classification/edit");
            scb = new SpaceClassificationBean();
        } else {
            model.addAttribute("action", "/classification/edit/" + classification.getExternalId());
            scb = classification.getBean();
        }
        if (newInfo == false) {
            model.addAttribute("information", scb);
        }
        model.addAttribute("classifications", allClassifications());
        model.addAttribute("currentUser", Authenticate.getUser());
        return "classification/edit";
    }

    @RequestMapping(value = "/edit/{classification}", method = RequestMethod.POST)
    public String create(@PathVariable SpaceClassification classification, @ModelAttribute SpaceClassificationBean information,
            BindingResult errors, Model model) {
        // validation
        try {
            spaceClassificationService.verifyClassification(information);
        } catch (DomainException e) {
            model.addAttribute("message", e.asJson().toString());
            model.addAttribute("information", information);
            return create(classification, model, true);
        }
        if (classification == null) {
            // create new classification
            classification = create(information);
        } else {

            spaceClassificationService.updateClassification(classification, information);
        }
        return "redirect:/classification/edit/" + classification.getExternalId();
    }

    @ResponseBody
    @RequestMapping(value = "/remove/{classification}", method = RequestMethod.DELETE)
    public String create(@PathVariable() SpaceClassification classification) {
        try {
            classification.delete();
        } catch (DomainException de) {
            return de.asJson().toString();
        }
        return "ok";
    }

}
