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

import javax.servlet.UnavailableException;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.spaces.domain.SpaceClassification;
import org.fenixedu.spaces.ui.services.SpaceClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonObject;

@SpringFunctionality(app = SpacesController.class, title = "title.space.classification.management")
@RequestMapping("/classification")
public class SpaceClassificationController {

    @Autowired
    SpaceClassificationService spaceClassificationService;

    @RequestMapping
    public String home(Model model) {
        return listClassifications(model);
    }

    @RequestMapping(value = "/list")
    public String listClassifications(Model model) {
        canWrite();
        model.addAttribute("classifications", SpaceClassification.all());
        return "classification/list";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Model model) throws UnavailableException {
        canWrite();
        return create(null, model);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String edit(@RequestBody String informationJson) {
        canWrite();
        return create(null, informationJson);
    }

    @Atomic(mode = TxMode.WRITE)
    private SpaceClassification create(SpaceClassificationBean infoBean) {
        SpaceClassification sc = null;
        if (infoBean.getParent().length() > 0) {
            sc = FenixFramework.getDomainObject(infoBean.getParent());
        }
        return new SpaceClassification(infoBean.getCode(), infoBean.getLocalizedName(), sc, infoBean.getMetadataSpec(), infoBean.getIsAllocatable());
    }

    @RequestMapping(value = "/edit/{classification}", method = RequestMethod.GET)
    public String create(@PathVariable SpaceClassification classification, Model model) {
        canWrite();
        return create(classification, model, false);
    }

    public String create(SpaceClassification classification, Model model, boolean newInfo) {
        SpaceClassificationBean scb = null;
        canWrite();
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
        model.addAttribute("classifications", SpaceClassification.all());
        model.addAttribute("currentUser", Authenticate.getUser());
        return "classification/edit";
    }

    @ResponseBody
    @RequestMapping(value = "/edit/{classification}", method = RequestMethod.POST)
    public String create(@PathVariable SpaceClassification classification, @RequestBody String json) {
        // validation
        canWrite();
        SpaceClassificationBean information = new SpaceClassificationBean(json);
        try {
            spaceClassificationService.verifyClassification(information);
        } catch (DomainException de) {
            return de.asJson().toString();
        }
        if (classification == null) {
            create(information);
        } else {
            try {
                spaceClassificationService.updateClassification(classification, information);
            } catch (DomainException de) {
                return de.asJson().toString();
            }
        }
        JsonObject ok = new JsonObject();
        String okS = ok.toString();
        return okS;
    }

    @ResponseBody
    @RequestMapping(value = "/remove/{classification}", method = RequestMethod.DELETE)
    public String create(@PathVariable() SpaceClassification classification) {
        canWrite();
        try {
            classification.delete();
        } catch (DomainException de) {
            return de.asJson().toString();
        }
        return "ok";
    }

    private boolean accessControl() {
        return Group.dynamic("spaceSuperUsers").isMember(Authenticate.getUser());
    }

    private void canWrite() {
        if (!accessControl()) {
            throw new RuntimeException("Unauthorized");
        }
    }

}
