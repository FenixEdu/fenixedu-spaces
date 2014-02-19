package org.fenixedu.spaces.ui.spring;

import java.beans.PropertyEditorSupport;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public class DomainObjectPropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(FenixFramework.getDomainObject(text));
    }

    @Override
    public String getAsText() {
        return ((DomainObject) getValue()).getExternalId();
    }

}
