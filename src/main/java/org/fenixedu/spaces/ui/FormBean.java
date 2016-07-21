package org.fenixedu.spaces.ui;

import org.fenixedu.spaces.domain.Space;

public class FormBean {

    private Space space;
    private String page;
    private String rejectMessage;

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRejectMessage() {
        return rejectMessage;
    }

    public void setRejectMessage(String rejectMessage) {
        this.rejectMessage = rejectMessage;
    }

}
