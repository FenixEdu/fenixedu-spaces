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
package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;
import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

@BennuSpringModule(basePackages = "org.fenixedu.spaces.ui", bundles = "SpacesResources")
public class FenixEduSpaceConfiguration {

    @ConfigurationManager(description = "Fenix Space Configuration")
    public interface ConfigurationProperties {
        @ConfigurationProperty(key = "scaleRatio", defaultValue = "1200")
        public String scaleRatio();

        @ConfigurationProperty(key = "fontSize", defaultValue = "0.007")
        public String fontSize();

        @ConfigurationProperty(key = "padding", defaultValue = "0.025")
        public String padding();

        @ConfigurationProperty(key = "xAxisOffset", defaultValue = "0.075")
        public String xAxisOffset();

        @ConfigurationProperty(key = "yAxisOffset", defaultValue = "0.3")
        public String yAxisOffset();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

    public static final String BUNDLE = "resources/FenixEduSpacesResources";

}
