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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.telosys.tools.dsl.parser.commons.FkElement;

public class DomainField {
	
    private final int lineNumber;
    private final String name;
    private final DomainType type;

    private final Map<String, DomainAnnotation> annotations = new HashMap<>();
    private final Map<String, DomainTag> tags = new HashMap<>();
    private final List<FkElement> fkElements = new LinkedList<>() ; // v 3.4.0
	
    /**
     * Constructor
     * @param lineNumber
     * @param name
     * @param type
     */
    public DomainField(int lineNumber, String name, DomainType type) {
    	this.lineNumber = lineNumber;
        this.name = name;
        this.type = type;
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
    
    public final int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the name of the field
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the type of the field
     * @return
     */
    public final DomainType getType() {
        return type;
    }

    public final DomainCardinality getCardinality() {
        return type.getCardinality();
    }

    public final String getTypeName() {
        return type.getName();
    }

    public final boolean isAttribute() {
        return type.isNeutralType();
    }

    public final boolean isLink() {
        return type.isEntity();
    }


    //------------------------------------------------------------------------
    // ANNOTATIONS
    //------------------------------------------------------------------------
    /**
     * Returns all annotation names (in alphabetical order)
     *
     * @return
     */
    public final List<String> getAnnotationNames() {
        List<String> names = new LinkedList<>(annotations.keySet());
        Collections.sort(names);
        return names;
    }

    /**
     * Return all annotations map
     * @return annotations
     */
    public Map<String, DomainAnnotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Return annotation by name if any (or null if none)
     * @param annotationName
     * @return
     */
    public DomainAnnotation getAnnotation(String annotationName) {
        return this.annotations.get(annotationName);
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

    //------------------------------------------------------------------------
    // FOREIGN KEYS DECLARATIONS 
    //------------------------------------------------------------------------
    public List<FkElement> getFkElements() { // v 3.4.0
    	return fkElements;
    }
    public void addFkElement(FkElement fke) { // v 3.4.0
    	fkElements.add(fke); 
    }
    
    //------------------------------------------------------------------------
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	// name
    	sb.append(name);
		sb.append( " : ");
    	// entity or enum
    	// type with cardinality
    	sb.append( type.toString() );
    	sb.append(" {");
    	// annotations
    	for (Map.Entry<String, DomainAnnotation> e : annotations.entrySet()) {
    		DomainAnnotation annotation = e.getValue();
    		sb.append(" @").append(annotation.getName());
    		if(annotation.hasParameter()){
    			sb.append("(").append(annotation.getParameter()).append(")");
    		}
    	}
    	// tags 
    	for (Map.Entry<String, DomainTag> e : tags.entrySet()) {
    		DomainTag tag = e.getValue();
    		sb.append(" #").append(tag.getName());
    		if(tag.hasParameter()){
    			sb.append("(").append(tag.getParameter()).append(")");
    		}
    	}
    	sb.append(" }");
    	return sb.toString();
    }

}
