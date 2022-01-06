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

import java.math.BigDecimal;
import java.util.List;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinitions;
import org.telosys.tools.dsl.parser.commons.FkElement;
import org.telosys.tools.dsl.parser.commons.ParamError;

public class DomainAnnotation {
	
	private final String name;
	private final Object parameter;

	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

    /**
     * Constructor for annotation without parameter
     * @param name
     */
    public DomainAnnotation(String name) {
    	super();
		this.name = name;
		this.parameter = null;
    }

    /**
     * Constructor for annotation with String parameter
     * @param name
     * @param param
     */
    public DomainAnnotation(String name, String param) {
    	super();
		this.name = name;
		this.parameter = param;
    }
    
    /**
     * Constructor for annotation with Integer parameter
     * @param name
     * @param param
     */
    public DomainAnnotation(String name, Integer param) {
    	super();
		this.name = name;
		this.parameter = param;
    }
    
    /**
     * Constructor for annotation with BigDecimal parameter
     * @param name
     * @param param
     */
    public DomainAnnotation(String name, BigDecimal param) {
    	super();
		this.name = name;
		this.parameter = param;
    }

    /**
     * Constructor for annotation with Boolean parameter
     * @param name
     * @param param
     */
    public DomainAnnotation(String name, Boolean param) {
    	super();
		this.name = name;
		this.parameter = param;
    }
    
    /**
     * Constructor for annotation with List parameter
     * @param name
     * @param param
     */
    public DomainAnnotation(String name, List<String> param) {
    	super();
		this.name = name;
		this.parameter = param;
    }
    
    /**
     * Constructor for annotation with 'FK' parameter
     * @param name
     * @param param
     */
    public DomainAnnotation(String name, FkElement param) {
    	super();
		this.name = name;
		this.parameter = param;
    }

	//-------------------------------------------------------------------------
	// Getters
	//-------------------------------------------------------------------------

    /**
	 * Returns the annotation name ( without '@' )
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true if the annotation can be used multiple times in the same field
	 * @return
	 */
	public boolean canBeUsedMultipleTimes() {
		// Only "FK" annotation can be used multiple times
		return "FK".equals(name);
	}
	
	/**
	 * Returns true if the annotation has a parameter
	 * 
	 * @return
	 */
	public boolean hasParameter() {
		return this.parameter != null;
	}

	/**
	 * Returns the annotation parameter <br>
	 * 
	 * @return
	 */
	public Object getParameter() {
		return parameter;
	}

	/**
	 * Returns the annotation string parameter <br>
	 * or null if none
	 * 
	 * @return
	 */
	public String getParameterAsString() {
		// null is not an instance of anything
		if (parameter instanceof String) {
			return (String) parameter;
		}
		throw newParamTypeError("String");
	}

	/**
	 * Returns the annotation decimal parameter <br>
	 * or null if none
	 * 
	 * @return
	 */
	public BigDecimal getParameterAsBigDecimal() {
		// null is not an instance of anything
		if (parameter instanceof BigDecimal) { 
			return (BigDecimal) parameter;
		}
		throw newParamTypeError("BigDecimal");
	}

	/**
	 * Returns the annotation Integer parameter or null if none
	 * 
	 * @return
	 */
	public Integer getParameterAsInteger() {
		// null is not an instance of anything
		if (parameter instanceof Integer) {
			return (Integer) parameter;
		}
		throw newParamTypeError("Integer");
	}

	/**
	 * Returns the annotation Boolean parameter or null if none
	 * 
	 * @return
	 */
	public Boolean getParameterAsBoolean() {
		// null is not an instance of anything
		if (parameter instanceof Boolean) {
			return (Boolean) parameter;
		}
		throw newParamTypeError("Boolean");
	}

	/**
	 * Returns the annotation List parameter or null if none
	 * 
	 * @return
	 */
	public List<?> getParameterAsList() {
		// null is not an instance of anything
		if (parameter instanceof List<?>) {
			return (List<?>) parameter;
		}
		throw newParamTypeError("List");
	}

	/**
	 * Returns the annotation FkElement parameter or null if none
	 * 
	 * @return
	 */
	public FkElement getParameterAsFKElement() {
		// null is not an instance of anything
		if (parameter instanceof FkElement) { 
			return (FkElement) parameter;
		}
		throw newParamTypeError("FKElement");
	}

	private IllegalStateException newParamTypeError(String type) {
		return new IllegalStateException("'" + name + "' : parameter is not a " + type);
	}
    
	//-------------------------------------------------------------------------

    @Override
    public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("@");
		sb.append(name);
		if (this.parameter != null) {
			sb.append("(");
			sb.append(parameter);
			sb.append(")");
		}
		return sb.toString();
    }

	/**
	 * Apply annotation on the given attribute
	 * @param model
	 * @param entity
	 * @param attribute
	 */
	public void applyToAttribute(DslModel model, DslModelEntity entity, DslModelAttribute attribute ) throws ParamError {
    	AnnotationDefinition annotationDefinition = getAnnotationDefinition();
       	if ( annotationDefinition.hasAttributeScope() ) {
        	annotationDefinition.apply(model, entity, attribute, this.getParameter());
    	}
    	else {
    		throw new IllegalStateException("annotation '" + this.name + "' not applicable on an attribute" );
    	}
	}

	/**
	 * Apply annotation on the given link
	 * @param model
	 * @param entity
	 * @param link
	 */
	public void applyToLink(DslModel model, DslModelEntity entity, DslModelLink link ) throws ParamError {
    	AnnotationDefinition annotationDefinition = getAnnotationDefinition();
       	if ( annotationDefinition.hasLinkScope() ) {
    		annotationDefinition.apply(model, entity, link, this.getParameter());
    	}
    	else {
    		throw new IllegalStateException("annotation '" + this.name + "' not applicable on a link" );
    	}
    }
	
	/**
	 * Get annotation definition 
	 * @return
	 */
	public AnnotationDefinition getAnnotationDefinition() {
    	AnnotationDefinition annotationDefinition = AnnotationDefinitions.get(this.name);
    	if ( annotationDefinition != null ) {
    		return annotationDefinition ;
    	}
    	else {
    		throw new IllegalStateException("Unknown annotation '" + this.name + "'" );
    	}
	}
}
