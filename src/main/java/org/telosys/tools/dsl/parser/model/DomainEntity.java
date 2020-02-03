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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DomainEntity extends DomainType {

    public static final int THIRTY_ONE_HASH_CODE = 31; 

    /**
     * Map of fields used for direct access by field name and to check uniqueness 
     */
    private final Map<String, DomainField> fieldsMap;
    
	private final List<Exception> errors = new LinkedList<>() ;


    /**
     * Constructor
     * @param name
     */
    public DomainEntity(String name) {
        super(name, DomainTypeNature.ENTITY);
        // LinkedHashMap to keep the original order
        this.fieldsMap = new LinkedHashMap<>(); 
    }

    public boolean hasField(DomainField field) {
    	return fieldsMap.containsKey(field.getName());
    }
    public void addField(DomainField field) {
//        if (fieldsMap.containsKey(field.getName())) {
//            throw new DslParserException("Field '" + field.getName() + "' already defined");
//        }
        fieldsMap.put(field.getName(), field);
    }

    /**
     * Returns a list containing all the fields
     * @return
     */
    public List<DomainField> getFields() {
        return new LinkedList<>(fieldsMap.values());
    }

    /**
     * Returns the field identified by the given name
     * @param fieldName
     * @return the field found (or null if not found)
     */
    public DomainField getField(String fieldName) {
        return fieldsMap.get(fieldName);
    }

    /**
     * Returns the number of fields
     * @return
     */
    public int getNumberOfFields() {
        return fieldsMap.size();
    }
    
    /**
     * Add a new error to the field 
     * @param error
     */
    public void addError(Exception error) {
    	errors.add(error);
    }
    
    public boolean hasError() {
    	return ! errors.isEmpty();
    }
    
    public List<Exception> getErrors() {
    	return errors;
    }

    //-------------------------------------------------------------------------------------
    public String toString() {
        String fieldRet = "";
        for (DomainField f : fieldsMap.values()) {
            fieldRet += "\n\t\t"+f.toString();
        }
        return this.getName() + " {" + fieldRet + "}";
    }

//    @Override
//    public boolean equals(Object other) {
//        if (other == null) {
//            return false;
//        }
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof DomainEntity)) {
//            return false;
//        }
//        DomainEntity otherEntity = (DomainEntity) other;
//        if (!otherEntity.getName().equals(this.getName())) {
//            return false;
//        }
//        if (otherEntity.fieldsMap.size() != fieldsMap.size()) {
//            return false;
//        }
//        if (!otherEntity.fieldsMap.equals(fieldsMap)) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = this.getName() != null ? this.getName().hashCode() : 0;
//        result = THIRTY_ONE_HASH_CODE * result + (fieldsMap != null ? fieldsMap.hashCode() : 0);
//        return result;
//    }

}
