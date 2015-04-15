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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.I18NBean;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.SpaceDomainException;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequest;
import org.fenixedu.spaces.domain.occupation.requests.OccupationRequestState;
import org.fenixedu.spaces.ui.services.OccupationService;
import org.fenixedu.spaces.ui.services.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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

    @Autowired
    I18NBean bundle;

    @Autowired(required = false)
    UserInformationService userInformationService;

    @RequestMapping(value = "/search/{id}", method = RequestMethod.GET)
    public String search(@PathVariable String id, Model model, @RequestParam(required = false, defaultValue = "1") String p) {
        List<OccupationRequest> result = occupationService.search(id);

        if (result.size() < 2) {
            return view(result.isEmpty() ? null : result.iterator().next(), model);
        }
        model.addAttribute("searchId", id);
        model.addAttribute("userRequestSearchResult", occupationService.getBook(result, p));
        return viewRequests(model, null, null);
    }

    @RequestMapping(value = "/{occupationRequest}", method = RequestMethod.GET)
    public String view(@PathVariable OccupationRequest occupationRequest, Model model) {
        model.addAttribute("occupationRequest", occupationRequest);
        if (userInformationService != null && occupationRequest != null) {
            model.addAttribute("email", userInformationService.getEmail(occupationRequest.getRequestor()));
            model.addAttribute("contacts", userInformationService.getContacts(occupationRequest.getRequestor()));
            model.addAttribute("groups", getUserGroups(occupationRequest.getRequestor()));
        }
        return "occupations/requests/single";
    }

    @RequestMapping(value = "/{occupationRequest}/comments", method = RequestMethod.POST)
    public String addComment(@PathVariable OccupationRequest occupationRequest, @RequestParam String description,
            @RequestParam OccupationRequestState state, Model model) {
        try {
            occupationService.addComment(occupationRequest, description, state);
        } catch (SpaceDomainException sde) {
            model.addAttribute("errors", sde.getLocalizedMessage());
            return view(occupationRequest, model);
        }
        return "redirect:/spaces/occupations/requests/" + occupationRequest.getExternalId();
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
            break;
        case RESOLVED:
            occupationService.closeRequest(occupationRequest, Authenticate.getUser());
            break;
        }
        return new RedirectView("/spaces/occupations/requests/" + occupationRequest.getExternalId(), true);
    }

    @RequestMapping(value = "/filter/{campus}", method = RequestMethod.GET)
    public String filter(@PathVariable Space campus, Model model, @RequestParam(defaultValue = "1") String p, @RequestParam(
            required = false) OccupationRequestState state) {
        model.addAttribute("campus", occupationService.getAllCampus());
        model.addAttribute("selectedCampi", campus);
        addRequests(model, campus, p, state);
        return "occupations/requests/view";
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportAnyCampusToExcel(@RequestParam(required = false) Space campus,
            @RequestParam(required = false) OccupationRequestState state, HttpServletResponse response) {
        List<OccupationRequest> requests;

        if (state != null) {
            requests = occupationService.all(state, campus);
        } else {
            requests = occupationService.getRequestsToProcess(Authenticate.getUser(), campus);
        }

        String filename = bundle.message("label.occupation.request.filename");
        if (campus != null) {
            filename += "_" + campus.getPresentationName();
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xls");
        try {
            makeExcel(requests, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewRequests(Model model, @RequestParam(defaultValue = "1") String p,
            @RequestParam(required = false) OccupationRequestState state) {
        model.addAttribute("campus", occupationService.getAllCampus());
        addRequests(model, null, p, state);
        return "occupations/requests/view";
    }

    public void addRequests(Model model, Space campus) {
        addRequests(model, campus, null, null);
    }

    public void addRequests(Model model, Space campus, String page, OccupationRequestState state) {

        String myRequestsPage = state == null ? page : null;
        String openRequestsPage = OccupationRequestState.OPEN.equals(state) ? page : null;
        String newRequestsPage = OccupationRequestState.NEW.equals(state) ? page : null;
        String resolvedRequestsPage = OccupationRequestState.RESOLVED.equals(state) ? page : null;

        List<OccupationRequest> myRequests = occupationService.getRequestsToProcess(Authenticate.getUser(), campus);
        List<OccupationRequest> openRequests = occupationService.all(OccupationRequestState.OPEN, campus);
        List<OccupationRequest> newRequests = occupationService.all(OccupationRequestState.NEW, campus);
        List<OccupationRequest> resolvedRequests = occupationService.all(OccupationRequestState.RESOLVED, campus);

        model.addAttribute("myRequests", occupationService.getBook(myRequests, myRequestsPage));
        model.addAttribute("openRequests", occupationService.getBook(openRequests, openRequestsPage));
        model.addAttribute("newRequests", occupationService.getBook(newRequests, newRequestsPage));
        model.addAttribute("resolvedRequests", occupationService.getBook(resolvedRequests, resolvedRequestsPage));
    }

    private String getUserGroups(final User requestor) {
        return userInformationService.getGroups(requestor).stream().map(g -> g.getPresentationName().trim())
                .collect(Collectors.joining(","));
    }

    private void makeExcel(List<OccupationRequest> requests, OutputStream outputStream) throws IOException {
        SheetData<OccupationRequest> data = new SheetData<OccupationRequest>(requests) {

            @Override
            protected void makeLine(OccupationRequest request) {
                addCell(bundle.message("label.occupation.request.identification"), request.getIdentification());
                addCell(bundle.message("label.occupation.request.instant"), request.getPresentationInstant());
                addCell(bundle.message("label.occupation.request.subject"), request.getSubject());
                final User requestor = request.getRequestor();
                addCell(bundle.message("label.occupation.request.requestor"),
                        String.format("%s (%s)", requestor.getPresentationName(), requestor.getUsername()));
                if (userInformationService != null) {
                    addCell(bundle.message("label.occupation.request.email"), userInformationService.getEmail(requestor));
                    addCell(bundle.message("label.occupation.request.roles"), getUserGroups(requestor).toString());
                }
                final User owner = request.getOwner();
                addCell(bundle.message("label.occupation.request.owner"),
                        String.format("%s (%s)", owner.getPresentationName(), owner.getUsername()));
            }

        };

        new SpreadsheetBuilder().addSheet(bundle.message("label.occupation.request.filetitle"), data).build(
                WorkbookExportFormat.EXCEL, outputStream);

    }
}
