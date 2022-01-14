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

public abstract class DomainType {

    /**
     * Type name <br>
     * Examples for "neutral type" :  "string", "integer", etc <br> 
     * Examples for "entity" : "Book", "Car", etc <br>
     */
    private final String name;

    /**
     * Type nature : "neutral type" or "entity" 
     */
    private final DomainTypeNature nature;

    private final DomainCardinality cardinality; 

    /**
     * Constructor
     * @param name for example "string", "integer", "Book", "Car"
     * @param cardinality
     * @param nature
     */
    protected DomainType(String name, DomainCardinality cardinality, DomainTypeNature nature) {
        super();
        this.name = name;
        this.nature = nature;
        this.cardinality = cardinality;
    }

    /**
     * Returns the "type name" <br>
     * Examples for "neutral type" :  "string", "integer", etc <br> 
     * Examples for "entity" : "Book", "Car", etc <br>
     * @return
     */
    public final String getName() {
        return name;
    }

    public final DomainTypeNature getNature() {
        return nature;
    }
    
    public final DomainCardinality getCardinality() {
        return cardinality;
    }

    public final boolean isNeutralType() {
        return nature == DomainTypeNature.NEUTRAL_TYPE;
    }

    public final boolean isEntity() {
        return nature == DomainTypeNature.ENTITY;
    }

}
