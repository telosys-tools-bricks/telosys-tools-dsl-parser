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
package org.telosys.tools.dsl.parser.model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class represents an enumeration item, i.e. a name with its associated value
 *
 */
public class DomainEnumerationItem {

    /**
     * Item name 
     */
    private final String name;

    /**
     * Item value ( String or Integer or Decimal )
     */
    private final Object value;

//    /**
//     * Constructor
//     * @param name
//     * @param value
//     */
//    public DomainEnumerationItem(String name, T value) {
//        super();
//        this.name = name;
//        this.value = value;
//    }

    /**
     * Constructor for String value
     * @param name
     * @param value
     */
    public DomainEnumerationItem(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor for BigInteger value
     * @param name
     * @param value
     */
    public DomainEnumerationItem(String name, BigInteger value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor for BigDecimal value
     * @param name
     * @param value
     */
    public DomainEnumerationItem(String name, BigDecimal value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the enumeration item
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the enumeration item ( String/Integer/Decimal)
     * @return
     */
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "'"+name +"' : '"+value+"'";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DomainEnumerationItem other = (DomainEnumerationItem) obj;
        if (name == null) {
            if (other.name != null){
                return false;
            }
        } else if (!name.equals(other.name)){
            return false;
        }
        if (value == null) {
            if (other.value != null){
                return false;
            }
        } else if (!value.equals(other.value)){
            return false;
        }
        return true;
    }

}
