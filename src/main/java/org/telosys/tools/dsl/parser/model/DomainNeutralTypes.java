/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl.parser.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DomainNeutralTypes {

    private DomainNeutralTypes(){}

    // Neutral type list of predefined names
    public static final String STRING    = "string";
    
    public static final String BYTE      = "byte";
    public static final String SHORT     = "short";
    public static final String INTEGER   = "int";
    public static final String LONG      = "long";
    
    public static final String DECIMAL   = "decimal";
    public static final String FLOAT     = "float";
    public static final String DOUBLE    = "double";
    
    public static final String BOOLEAN   = "boolean";
    
    public static final String DATE      = "date";
    public static final String TIME      = "time";
    public static final String TIMESTAMP = "timestamp";
    public static final String TIMESTAMPZ = "timestampz";

    public static final String BINARY_BLOB = "binary"; // BLOB


    private static final String[] NAMES = {
    	STRING, 
    	BYTE, SHORT, INTEGER, LONG,
    	DECIMAL, FLOAT, DOUBLE,
    	BOOLEAN, 
    	DATE, TIME, TIMESTAMP, TIMESTAMPZ,
    	BINARY_BLOB };

    private static final Map<String, DomainNeutralType> NEUTRAL_TYPES = new HashMap<>();

    static {
        for (String name : NAMES) {
            DomainNeutralType type = new DomainNeutralType(name);
            NEUTRAL_TYPES.put(type.getName(), type);
        }
    }

    public static final boolean exists(String typeName) {
        return NEUTRAL_TYPES.containsKey(typeName);
    }

    /**
     * Returns the Neutral Type for the given type name <br>
     * or null if the given type doesn't exist
     * @param typeName
     * @return
     */
    public static final DomainNeutralType getType(String typeName) {
        return NEUTRAL_TYPES.get(typeName);
    }

    public static final List<String> getNames() {
        return new LinkedList<>(NEUTRAL_TYPES.keySet());
    }

    public static final List<String> getSortedNames() {
        List<String> list = getNames();
        Collections.sort(list);
        return list;
    }

}
