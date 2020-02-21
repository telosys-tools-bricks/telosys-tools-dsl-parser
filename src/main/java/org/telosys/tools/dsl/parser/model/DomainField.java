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

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;

public class DomainField {
	
    public static final int THIRTY_ONE = 31;

    private final int lineNumber;
    private final String name;
    private final DomainType type;
    private final int cardinality; // if != 1 : "ONE-TO-MANY"

    private final Map<String, DomainAnnotation> annotations = new HashMap<>();
    private final Map<String, DomainTag> tags = new HashMap<>();
	private final List<AnnotationOrTagError> errors = new LinkedList<>() ;

    /**
     * Constructor with specific cardinality
     * @param name
     * @param type
     * @param cardinality
     */
    public DomainField(int lineNumber, String name, DomainType type, int cardinality) {
    	this.lineNumber = lineNumber;
        this.name = name;
        this.type = type;
        this.cardinality = cardinality;
    }

    /**
     * Simplified constructor (only for tests)
     * @param name
     * @param type
     */
    public DomainField(String name, DomainType type) {
    	this.lineNumber = 0;
        this.name = name;
        this.type = type;
        this.cardinality = 1;
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
        
//    /**
//     * Add a new annotation to the field <br>
//     * Throws an exception if the given annotation is already defined
//     * @param annotation
//     */
//    public void addAnnotation(DomainAnnotation annotation) throws AnnotationOrTagError {
//        if (!annotations.containsKey(annotation.getName())) {
//            annotations.put(annotation.getName(), annotation);
//        } else {
////            throw new DslParserException("Annotation '" + annotation.getName() + "' already define in field '" + this.getName() + "'");
//            throw new AnnotationOrTagError("", this.name, annotation.getName(), "annotation defined more than once" );
//        }
//    }

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
    
    /**
     * Add a new error to the field 
     * @param error
     */
    public void addError(AnnotationOrTagError error) {
    	errors.add(error);
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

    /**
     * Returns the cardinality of the type <br>
     * 1 = standard <br>
     * Not 1 = multiple (0..N) <br>
     * @return
     */
    public final int getCardinality() {
        return cardinality;
    }

    public final String getTypeName() {
        return type.getName();
    }

    public final boolean isNeutralType() {
        return type.isNeutralType();
    }

    public final boolean isEntity() {
        return type.isEntity();
    }

    public final boolean isEnumeration() {
        return type.isEnumeration();
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

    //------------------------------------------------------------------------
    // ERRORS
    //------------------------------------------------------------------------
    public boolean hasErrors() {
    	return ! this.errors.isEmpty();
    }
    
    public List<AnnotationOrTagError> getErrors() {
    	return this.errors;
    }
    
    //------------------------------------------------------------------------
    @Override
    public String toString() {
    	
    	String fieldString= "'"+name+"'";
    	if(isEntity()){
    		fieldString += ": Entity";
    	} else if(isEnumeration()){
    		fieldString += ": Enumeration";
    	} else {
    		fieldString += ": ";
    	}
    	
    	fieldString += "("+type.getName()+")";
    	if(cardinality != 1) {
    		if(cardinality == -1){
    			fieldString+="[]";
    		} else {
    			fieldString+="["+cardinality+"]";
    		}
    	}
    	
    	String annotationsString= "";    	
    	for (String mapKey : annotations.keySet()) {
    		DomainAnnotation annotation = annotations.get(mapKey);
    		String parameter = "";
    		if(annotation.hasParameter()){
    			parameter = "("+ annotation.getParameter() +")";
    		}
    		annotationsString += "\n\t\t" + annotation.getName()+parameter;
    	}
    	// TODO : tags 
        return  fieldString + '{' +
                 	annotationsString +
                '}';
    }

    //------------------------------------------------------------------------------------------
    
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        DomainField field = (DomainField) o;
//
//        if (!annotations.equals(field.annotations)) {
//            return false;
//        }
//        if (!name.equals(field.name)) {
//            return false;
//        }
//        if (!type.equals(field.type)) {
//            return false;
//        }
//        if (cardinality != (field.cardinality)) {
//        	return false;
//        }
//
//        return true;
//    }

//    @Override
//    public int hashCode() {
//        int result = name != null ? name.hashCode() : 0;
//        result = THIRTY_ONE * result + (type != null ? type.hashCode() : 0);
//        result = THIRTY_ONE * result + (annotations != null ? annotations.hashCode() : 0);
//        return result;
//    }

}
