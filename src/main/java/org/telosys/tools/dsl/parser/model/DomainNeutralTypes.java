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

import org.telosys.tools.dsl.DslParserException;

import java.util.*;

public final class DomainNeutralTypes {

    private DomainNeutralTypes(){}

    // Neutral type list of predefined names
    public static final String STRING    = "string";
    
    public static final String BYTE      = "byte"; // Added
    public static final String SHORT     = "short"; // Added
//    public static final String INTEGER   = "integer"; 
    public static final String INTEGER   = "int"; // Changed
    public static final String LONG      = "long"; // Added
    
    public static final String DECIMAL   = "decimal";
    public static final String FLOAT     = "float";
    public static final String DOUBLE    = "double";
    
    public static final String BOOLEAN   = "boolean";
    
    public static final String DATE      = "date";
    public static final String TIME      = "time";
    public static final String TIMESTAMP = "timestamp";
    
    public static final String BINARY_BLOB   = "binary"; // BLOB
    //public static final String LONGTEXT_CLOB = "longtext"; // CLOB ( removed : string is sufficient )


    private static final String[] NAMES = {
    	STRING, 
    	BYTE, SHORT, INTEGER, LONG,
    	DECIMAL, FLOAT, DOUBLE,
    	BOOLEAN, 
    	DATE, TIME, TIMESTAMP, 
    	BINARY_BLOB };
    	//, LONGTEXT_CLOB};

    private static final Map<String, DomainNeutralType> NEUTRAL_TYPES = new Hashtable<String, DomainNeutralType>();

    static {
        for (String name : NAMES) {
            DomainNeutralType type = new DomainNeutralType(name);
            NEUTRAL_TYPES.put(type.getName(), type);
        }
    }

    public static final boolean exists(String typeName) {
        return NEUTRAL_TYPES.containsKey(typeName);
    }

    public static final DomainNeutralType getType(String typeName) {
        if (NEUTRAL_TYPES.containsKey(typeName)) {
            return NEUTRAL_TYPES.get(typeName);
        } else {
            throw new DslParserException("Invalid neutral type name '" + typeName + "'");
        }
    }

    public static final List<String> getNames() {
        return new LinkedList<String>(NEUTRAL_TYPES.keySet());
    }

    public static final List<String> getSortedNames() {
        List<String> list = getNames();
        Collections.sort(list);
        return list;
    }

}
