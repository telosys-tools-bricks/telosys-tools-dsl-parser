/**
 * Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.dsl.parser.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * All the properties of the parser are in this class.
 * To use this class :
 * TelosysDSLProperties.getProperties().getProperty("property");
 *
 * @author Jonhathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 * @date 2014-06-07
 */
public final class TelosysDSLProperties {

    /**
     * All the properties
     */
    private Map<String, String> properties;

    /**
     * the singleton of this class
     */
    private static TelosysDSLProperties instance;

    /**
     * @return all the properties
     */
    public static TelosysDSLProperties getProperties() {
        if (instance == null) {
            instance = new TelosysDSLProperties();
        }
        return instance;
    }

    /**
     * private constructor which initialize all the properties
     */
    private TelosysDSLProperties() {
        this.properties = new HashMap<String, String>();
        this.addValues();
    }

    /**
     * Init all the properties
     */
    private void addValues() {
        properties.put("start_comment", "//");
        properties.put("annotations", "Id,NotNull,Min#,Max#,SizeMin#,SizeMax#,Past,Future");
    }

    /**
     * @param key the key of the property
     * @return the value of the property or null if there isn't a property
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

}
