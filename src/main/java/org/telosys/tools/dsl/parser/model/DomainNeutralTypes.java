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

import java.util.HashMap;
import java.util.Map;

import org.telosys.tools.generic.model.types.NeutralType;

public final class DomainNeutralTypes {

    private DomainNeutralTypes(){}

	// Builds the Map of DomainNeutralType for each NeutralType
    private static final Map<String, DomainNeutralType> NEUTRAL_TYPES = new HashMap<>();
    static {
        for (String name : NeutralType.getAllNeutralTypes()) {
            DomainNeutralType type = new DomainNeutralType(name);
            NEUTRAL_TYPES.put(type.getName(), type);
        }
    }

    public static final boolean exists(String typeName) {
        return NEUTRAL_TYPES.containsKey(typeName);
    }

    public static final int size() {
        return NEUTRAL_TYPES.size();
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

}
