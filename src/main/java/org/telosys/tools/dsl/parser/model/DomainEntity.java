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

import org.telosys.tools.dsl.EntityParserException;

import java.util.*;

/**
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 * @date 2014-05-13
 */
public class DomainEntity extends DomainType {

    private final Map<String, DomainEntityField> fields;
    public static final int THIRTY_ONE_HASH_CODE = 31; // TODO rename

    public DomainEntity(String name) {
        super(name, DomainTypeNature.ENTITY);
        this.fields = new Hashtable<String, DomainEntityField>();
    }

    public void addField(DomainEntityField field) {
        if (fields.containsKey(field.getName())) {
            throw new EntityParserException("Field '" + field.getName() + "' already defined");
        }
        fields.put(field.getName(), field);
    }

    public List<DomainEntityField> getFields() {
        return new LinkedList<DomainEntityField>(fields.values());
    }

    public DomainEntityField getField(String fieldName) {
        return fields.get(fieldName);
    }

    public int getNumberOfFields() {
        return fields.size();
    }

    public String toString() {
        String fieldRet = "";
        for (DomainEntityField f : fields.values()) {
            fieldRet += "\n\t\t"+f.toString();
        }
        return this.getName() + " {" + fieldRet + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof DomainEntity)) {
            return false;
        }
        DomainEntity otherEntity = (DomainEntity) other;
        if (!otherEntity.getName().equals(this.getName())) {
            return false;
        }
        if (otherEntity.fields.size() != fields.size()) {
            return false;
        }
        if (!otherEntity.fields.equals(fields)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.getName() != null ? this.getName().hashCode() : 0;
        result = THIRTY_ONE_HASH_CODE * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }

    /**
     * Copy the field of the entity into another entity
     *
     * @param intoCopy the destination
     */
    public void copyIn(DomainEntity intoCopy) {
        Collection<DomainEntityField> e = fields.values();
        for (DomainEntityField entity : e) {
            intoCopy.addField(entity);
        }
    }
}
