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

import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;

public class DomainEntity {

    /**
     * Entity name
     */
    private final String name;
    
    private String databaseTable = "";
    
    /**
     * Map of fields used for direct access by field name and to check uniqueness 
     */
    private final Map<String, DomainField> fieldsMap;
    
//	private final List<FieldParsingError> errors = new LinkedList<>() ;
	private final List<ParsingError> errors = new LinkedList<>() ;


    /**
     * Constructor
     * @param name
     */
    public DomainEntity(String name) {
		if (name == null) {
			throw new IllegalStateException("DomainEntity name is null");
		}
        this.name = name ;
        this.databaseTable = name ; // by default 
        // LinkedHashMap to keep the original order
        this.fieldsMap = new LinkedHashMap<>(); 
    }
    
    /**
     * Returns the entity name
     * @return
     */
    public final String getName() {
        return name;
    }
    
	public String getDatabaseTable() {
		return databaseTable;
	}
	public void setDatabaseTable(String databaseTable) {
		this.databaseTable = databaseTable;
	}

    //-------------------------------------------------------------------------------------
    // FIELDS
    //-------------------------------------------------------------------------------------
    public boolean hasField(DomainField field) {
    	return fieldsMap.containsKey(field.getName());
    }
    public void addField(DomainField field) {
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
    
    //-------------------------------------------------------------------------------------
    // ERRORS
    //-------------------------------------------------------------------------------------
    /**
     * Add a new error to the field 
     * @param error
     */
//    public void addError(FieldParsingError error) {
    public void addError(ParsingError error) {
    	errors.add(error);
    }
    
    public boolean hasError() {
    	return ! errors.isEmpty();
    }
    
//    public List<FieldParsingError> getErrors() {
    public List<ParsingError> getErrors() {
    	return errors;
    }
    
    /**
     * Returns the number of errors
     * @return
     */
    public int getNumberOfErrors() {
        return errors.size();
    }

    //-------------------------------------------------------------------------------------
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(this.name);
    	sb.append(" {");
        for (DomainField field : fieldsMap.values()) {
        	sb.append("\n");
        	sb.append(field.toString());
        }
    	sb.append("\n");
    	sb.append("}");
    	sb.append("\n");
    	if ( errors.isEmpty() ) {
        	sb.append("OK (no error) \n");
    	} else {
        	sb.append(errors.size());
        	sb.append(" error(s) : \n");
//    		for ( FieldParsingError e : errors ) {
    		for ( ParsingError e : errors ) {
    	    	sb.append(" . " );
//    	    	sb.append(e.getFieldName() );
    	    	if ( e instanceof FieldParsingError ) {
        	    	sb.append(((FieldParsingError)e).getFieldName() );
    	    	}

    	    	sb.append(" : " );
    	    	sb.append(e.getMessage() );
    	    	sb.append("\n");
    		}
    	}
        return sb.toString();
    }

}
