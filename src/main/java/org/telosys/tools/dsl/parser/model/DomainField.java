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

import org.telosys.tools.dsl.DslParserException;

import java.util.*;

public class DomainField {

    private final String name;
    private final DomainType type;
    private final int cardinality; // if != 1 : "ONE-TO-MANY"
    public static final int THIRTY_ONE = 31;

    /*
      TODO : Hashtable (each annotation is unique for a field)
      private List<DomainEntityFieldAnnotation> annotationList;
      TODO : final
      */

    private final Map<String, DomainAnnotationOrTag> annotations = new Hashtable<String, DomainAnnotationOrTag>();

    /**
     * Constructor with default cardinality of 1
     * @param name
     * @param type
     */
    public DomainField(String name, DomainType type) {
        // this.annotationList = new ArrayList<DomainEntityFieldAnnotation>();
        this.name = name;
        this.type = type;
        this.cardinality = 1;
    }

    /**
     * Constructor with specific cardinality
     * @param name
     * @param type
     * @param cardinality
     */
    public DomainField(String name, DomainType type, int cardinality) {
        this.name = name;
        this.type = type;
        this.cardinality = cardinality;
        //    this.annotationList = new ArrayList<DomainEntityFieldAnnotation>();
    }

    public void setAnnotationList(List<DomainAnnotationOrTag> annotationList) {
        for (DomainAnnotationOrTag annotation : annotationList) {
            addAnnotation(annotation);
        }
    }

    public void addAnnotation(DomainAnnotationOrTag annotation) {
        if (!annotations.containsKey(annotation.getName())) {
            annotations.put(annotation.getName(), annotation);
        } else {
            throw new DslParserException("The annotation " + annotation.getName() + " is already define in the field " + this.getName());
        }
    }

    public final String getName() {
        return name;
    }

    public final DomainType getType() {
        return type;
    }

    /**
     * Returns the cardinality <br>
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
    		String parameter = "";
    		if(annotations.get(mapKey).hasParameter()){
    			parameter = "("+ annotations.get(mapKey).getParameterAsString() +")";
    		}
    		annotationsString += "\n\t\t\t\t" + annotations.get(mapKey).getName()+parameter;
    	}
        return  fieldString + '{' +
                 	annotationsString +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DomainField field = (DomainField) o;

        if (!annotations.equals(field.annotations)) {
            return false;
        }
        if (!name.equals(field.name)) {
            return false;
        }
        if (!type.equals(field.type)) {
            return false;
        }
        if (cardinality != (field.cardinality)) {
        	return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = THIRTY_ONE * result + (type != null ? type.hashCode() : 0);
        result = THIRTY_ONE * result + (annotations != null ? annotations.hashCode() : 0);
        return result;
    }

    /**
     * Returns all the annotation names (in alphabetical order)
     *
     * @return
     */
    public final List<String> getAnnotationNames() {
        List<String> names = new LinkedList<String>(annotations.keySet());
        Collections.sort(names);
        return names;
    }

    /**
     * Return annotations
     * @return annotations
     */
    public Map<String, DomainAnnotationOrTag> getAnnotations() {
        return this.annotations;
    }
}
