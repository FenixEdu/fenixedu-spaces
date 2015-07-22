package org.fenixedu.spaces.ui;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.UnavailableException;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.Occupation;
import org.fenixedu.spaces.domain.occupation.SharedOccupation;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@BennuSpringController(SpacesController.class)
@RequestMapping("/spaces/occupants")
public class SpaceOccupantsController {

    private List<SharedOccupation> getSharedOccupations(Space space) {
        return getSharedOccupations(space, null);
    }

    private List<SharedOccupation> getSharedOccupations(Space space, Boolean active) {
        List<SharedOccupation> occupationList = new ArrayList<SharedOccupation>();
        for (Occupation occupation : space.getOccupationSet()) {
            if (occupation instanceof SharedOccupation) {
                SharedOccupation sharedOccupation = (SharedOccupation) occupation;
                if (active == null
                        || (active ? !sharedOccupation.getActiveIntervals().isEmpty() : !sharedOccupation.getInactiveIntervals()
                                .isEmpty())) {
                    occupationList.add(sharedOccupation);
                }
            }
        }
        return occupationList;
    }

    @RequestMapping(value = "{space}", method = RequestMethod.GET)
    public String occupantsManagement(@PathVariable Space space, Model model) throws UnavailableException {
        canWrite(space);
        SpaceOccupantsBean theOb = new SpaceOccupantsBean();
        model.addAttribute("occupantsbean", theOb);
        model.addAttribute("spaceinfo", space.bean());
        model.addAttribute("space", space);
        model.addAttribute("activeOccupations", getSharedOccupations(space, true));
        model.addAttribute("inactiveOccupations", getSharedOccupations(space, false));
        model.addAttribute("action", "/spaces/occupants/" + space.getExternalId());
        return "spaces/occupants";
    }

    @Atomic(mode = TxMode.WRITE)
    private void occupantsManagement(Space space, SpaceOccupantsBean sab) {
        SharedOccupation sharedOccup = null;
        List<SharedOccupation> sharedOccups = getSharedOccupations(space);
        for (SharedOccupation so : sharedOccups) {
            if (so.getUser().equals(sab.getUserObject())) {
                sharedOccup = so;
            }
        }
        if (sharedOccups.size() == 0 || sharedOccup == null) {
            sharedOccup = new SharedOccupation();

        }

        if (sharedOccup.doConfig(sab)) {
            space.addOccupation(sharedOccup);
        }
        return;
    }

    @RequestMapping(value = "{space}", method = RequestMethod.POST)
    public String occupantsManagement(@PathVariable Space space, @ModelAttribute SpaceOccupantsBean spacebean,
            BindingResult errors, Model model) throws UnavailableException {
        canWrite(space);
        try {
            occupantsManagement(space, spacebean);
        } catch (DomainException e) {
            model.addAttribute("message", e.asJson().toString());
            return occupantsManagement(space, model);
        }
        return "redirect:/spaces/occupants/" + space.getExternalId();
    }

    private boolean accessControl(Space space) {
        User currentUser = Authenticate.getUser();
        if (space == null) {
            return Group.dynamic("spaceSuperUsers").isMember(currentUser);
        }
        return space.isOccupationMember(currentUser);
    }

    private void canWrite(Space space) {
        if (!accessControl(space)) {
            throw new RuntimeException("Unauthorized");
        }
    }
}
