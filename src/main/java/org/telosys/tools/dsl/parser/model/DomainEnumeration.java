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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.telosys.tools.dsl.EntityParserException;

/**
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 * @date 2014-05-13
 */
public abstract class DomainEnumeration<T> extends DomainType {

    private final DomainEnumerationType itemsType ;
    
    private final Map<String, DomainEnumerationItem> items;

    public DomainEnumeration(String name, DomainEnumerationType enumerationType) {
        super(name, DomainTypeNature.ENUMERATION);
        this.itemsType = enumerationType ;
        this.items = new Hashtable<String, DomainEnumerationItem>();
    }

    public int getNumberOfItems() {
        return items.size();
    }

    public DomainEnumerationType getItemsType() {
        return itemsType;
    }

    @Override
    public String toString() {
    	String itemsString = "\n";
    	for (DomainEnumerationItem item : getItems()) {
    		itemsString += "\t\t"+item.toString()+"\n";
    	}
        return "{"+itemsString+"}";
    }

//    public void addItem(String name, T value) {
//        DomainEnumerationItem item = new DomainEnumerationItem(name, value);
//        addItem(item);
//    }

    public void addItem(DomainEnumerationItem item) {
        if (items.get(item.getName()) != null) {
            throw new EntityParserException("Item '" + item.getName() + "' already defined in enum '" + this.getName() + "'");
        }
        items.put(item.getName(), item);
    }
    
    /**
     * Adds an item of type 'String'
     * @param name
     * @param value
     */
    public void addItem(String name, String value) {
        checkInput(name, value);
        items.put(name, new DomainEnumerationItem(name, value) );
    }
    /**
     * Adds an item of type 'BigInteger'
     * @param name
     * @param value
     */
    public void addItem(String name, BigInteger value) {
        checkInput(name, value);
        items.put(name, new DomainEnumerationItem(name, value) );
    }
    /**
     * Adds an item of type 'BigDecimal'
     * @param name
     * @param value
     */
    public void addItem(String name, BigDecimal value) {
        checkInput(name, value);
        items.put(name, new DomainEnumerationItem(name, value) );
    }
    
    private void checkInput(String name, Object value) {
    	if ( name == null )  throw new IllegalArgumentException("'name' is null");
    	if ( value == null ) throw new IllegalArgumentException("'value' is null");
        if (items.get(name) != null) throw new EntityParserException("Name '" + name + "' already defined in enum '" + this.getName() + "'");
    }
    
    public DomainEnumerationItem getItem(String name) {
        return items.get(name);
    }

    public Object getItemValue(String name) {
        DomainEnumerationItem item = items.get(name);
        return (item != null ? item.getValue() : null);
    }

    public List<DomainEnumerationItem> getItems() {
        return new LinkedList<DomainEnumerationItem>(items.values());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
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
        DomainEnumeration<?> other = (DomainEnumeration<?>) obj;
        if (items == null) {
            if (other.items != null) {
                return false;
            }
        } else if (!items.equals(other.items)) {
            return false;
        }

        return true;
    }

}
