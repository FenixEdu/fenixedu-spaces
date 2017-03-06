package org.fenixedu.spaces.ui;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.submission.SpacePhoto;
import org.fenixedu.spaces.domain.submission.SpacePhotoSubmission;
import org.fenixedu.spaces.ui.services.SpacePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@BennuSpringController(SpacesController.class)
@RequestMapping("/spaces/photos")
public class SpacePhotoController {

    @Autowired
    SpacePhotoService photoService;

    @RequestMapping(value = "/review/{space}", method = RequestMethod.GET)
    public String pendingPhotos(@PathVariable Space space, Model model, @RequestParam(defaultValue = "1") String p) {
        model.addAttribute("submissions",
                photoService.getSubmissionBook(photoService.getAllSpacePhotoSubmissionsToProcess(space), p));
        model.addAttribute("space", space);
        return "photos/review";
    }

    @RequestMapping(value = "/{photoSubmission}/accept", method = RequestMethod.POST)
    public String acceptPhoto(HttpServletRequest request, @PathVariable SpacePhotoSubmission photoSubmission, Model model,
            @Value("null") @ModelAttribute FormBean form) {
        photoService.acceptSpacePhoto(photoSubmission, Authenticate.getUser());
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/{photoSubmission}/reject", method = RequestMethod.POST)
    public String rejectPhoto(HttpServletRequest request, @PathVariable SpacePhotoSubmission photoSubmission, Model model,
            @Value("null") @ModelAttribute FormBean form) {
        photoService.rejectSpacePhoto(photoSubmission, Authenticate.getUser(), form.getRejectMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @RequestMapping(value = "/{spacePhoto}/hide", method = RequestMethod.POST)
    public String hidePhoto(@PathVariable SpacePhoto spacePhoto, Model model, @Value("null") @ModelAttribute FormBean form) {
        photoService.hideSpacePhoto(spacePhoto);
        return "redirect:/spaces/photos/edit/" + form.getSpace().getExternalId() + "?p=" + form.getPage();
    }

    @RequestMapping(value = "/{spacePhoto}/show", method = RequestMethod.POST)
    public String showPhoto(@PathVariable SpacePhoto spacePhoto, Model model, @Value("null") @ModelAttribute FormBean form) {
        photoService.showSpacePhoto(spacePhoto);
        return "redirect:/spaces/photos/edit/" + form.getSpace().getExternalId() + "?p=" + form.getPage();
    }

    @RequestMapping(value = "/{spacePhoto}/delete", method = RequestMethod.POST)
    public String deletePhoto(@PathVariable SpacePhoto spacePhoto, Model model, @Value("null") @ModelAttribute FormBean form) {
        photoService.removeSpacePhoto(form.getSpace(), spacePhoto);
        return "redirect:/spaces/photos/edit/" + form.getSpace().getExternalId() + "?p=" + form.getPage();
    }

    @RequestMapping(value = "/edit/{space}", method = RequestMethod.GET)
    public String editSpacePhotos(@PathVariable Space space, Model model, @RequestParam(defaultValue = "1") String a,
            @RequestParam(defaultValue = "1") String ar, @RequestParam(defaultValue = "1") String p,
            @RequestParam(defaultValue = "1") String tab) {
        model.addAttribute("activePhotos", photoService.getPhotoBook(photoService.getAllSpacePhotos(space), a));
        model.addAttribute("archivedPhotoSubmissions",
                photoService.getSubmissionBook(photoService.getArchivedSpacePhotoSubmissions(space), ar));
        model.addAttribute("pendingPhotoSubmissions",
                photoService.getSubmissionBook(photoService.getSpacePhotoSubmissionsToProcess(space), p));
        model.addAttribute("activeTab", tab);
        model.addAttribute("space", space);
        return "photos/edit/edit";
    }
}
