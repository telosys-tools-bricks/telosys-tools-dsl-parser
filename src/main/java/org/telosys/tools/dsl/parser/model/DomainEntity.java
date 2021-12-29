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
    
    private final Map<String, DomainAnnotation> annotations = new HashMap<>(); // V 3.4.0
    private final Map<String, DomainTag> tags = new HashMap<>(); // V 3.4.0

    /**
     * Map of fields used for direct access by field name and to check uniqueness 
     */
    private final Map<String, DomainField> fieldsMap;
    
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
    /**
     * Check if the given annotation is already defined in the field
     * @param annotation
     * @return
     */
    public boolean hasAnnotation(DomainAnnotation annotation) {
    	return annotations.containsKey(annotation.getName());
    }
    /**
     * Add a new annotation to the field 
     * @param annotation
     */
    public void addAnnotation(DomainAnnotation annotation) {
    	annotations.put(annotation.getName(), annotation);
    }
        
    /**
     * Check if the given tag is already defined in the field
     * @param tag
     * @return
     */
    public boolean hasTag(DomainTag tag) {
    	return tags.containsKey(tag.getName());
    }
    /**
     * Add a new tag to the field 
     * @param tag
     */
    public void addTag(DomainTag tag) {
    	tags.put(tag.getName(), tag);
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

    //------------------------------------------------------------------------
    // ANNOTATIONS
    //------------------------------------------------------------------------
    /**
     * Returns all the annotation names (in alphabetical order)
     *
     * @return
     */
    public final List<String> getAnnotationNames() {
        List<String> names = new LinkedList<>(annotations.keySet());
        Collections.sort(names);
        return names;
    }

    /**
     * Return annotations
     * @return annotations
     */
    public Map<String, DomainAnnotation> getAnnotations() {
        return this.annotations;
    }

    //------------------------------------------------------------------------
    // TAGS
    //------------------------------------------------------------------------
    public final List<String> getTagNames() {
        List<String> names = new LinkedList<>(tags.keySet());
        Collections.sort(names);
        return names;
    }

    public Map<String, DomainTag> getTags() {
        return this.tags;
    }

    //-------------------------------------------------------------------------------------
    // ERRORS
    //-------------------------------------------------------------------------------------
    /**
     * Add a new error to the field 
     * @param error
     */
    public void addError(ParsingError error) {
    	errors.add(error);
    }
    
    public boolean hasError() {
    	return ! errors.isEmpty();
    }
    
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
    	sb.append("Entity name : '");
    	sb.append(this.name);
    	sb.append("'\n");
    	sb.append("Annotations (");
    	sb.append(annotations.size());
    	sb.append(") : \n");
    	for ( DomainAnnotation a : annotations.values() ) {
        	sb.append(" - ");
        	sb.append(a.toString());
        	sb.append("\n");
    	}
    	sb.append("Tags (");
    	sb.append(tags.size());
    	sb.append(") : \n");
    	for ( DomainTag tag : tags.values() ) {
        	sb.append(" - ");
        	sb.append(tag.toString());
        	sb.append("\n");
    	}
    	sb.append("Fields (");
    	sb.append(fieldsMap.size());
    	sb.append(") : \n");
        for (DomainField field : fieldsMap.values()) {
        	sb.append(" - ");
        	sb.append(field.toString());
        	sb.append("\n");
        }
        /*** Errors no longer stored in entity
    	sb.append("Errors (");
    	sb.append(errors.size());
    	sb.append(") : \n");
		for ( ParsingError e : errors ) {
	    	sb.append(" . " );
	    	if ( e instanceof FieldParsingError ) {
    	    	sb.append(((FieldParsingError)e).getFieldName() );
	    	}
	    	sb.append(" : " );
	    	sb.append(e.getMessage() );
	    	sb.append("\n");
		}
		***/
        return sb.toString();
    }

}
