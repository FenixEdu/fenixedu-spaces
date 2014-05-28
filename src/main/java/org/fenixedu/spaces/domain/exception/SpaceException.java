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
package org.fenixedu.spaces.domain.exception;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class SpaceException extends DomainException {

    private static final long serialVersionUID = -59199476461461293L;

    public SpaceException(Status status, String bundle, String key, String... args) {
        super(status, bundle, key, args);
    }

    public SpaceException(String key, String... args) {
        super("resources.FenixSpaceResources", key, args);
    }

    public SpaceException(Throwable cause, Status status, String bundle, String key, String... args) {
        super(cause, status, bundle, key, args);
    }

    public SpaceException(Throwable cause, String bundle, String key, String... args) {
        super(cause, bundle, key, args);
    }

}
