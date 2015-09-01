/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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

public abstract class DomainType {

    /**
     * Type name
     * e.g. "string", "integer", "Book" (entity), "BookType" (enum), etc
     */
    private final String name;

    /**
     * Type nature : neutral type, entity or enumeration
     */
    private final DomainTypeNature nature;


    public DomainType(String name, DomainTypeNature nature) {
        super();
        this.name = name;
        this.nature = nature;
    }

    public final String getName() {
        return name;
    }

    public final DomainTypeNature getNature() {
        return nature;
    }

    public final boolean isNeutralType() {
        return nature == DomainTypeNature.NEUTRAL_TYPE;
    }

    public final boolean isEntity() {
        return nature == DomainTypeNature.ENTITY;
    }

    public final boolean isEnumeration() {
        return nature == DomainTypeNature.ENUMERATION;
    }

}
