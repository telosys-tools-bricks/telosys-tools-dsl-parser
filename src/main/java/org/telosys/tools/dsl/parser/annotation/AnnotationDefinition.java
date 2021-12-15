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
package org.telosys.tools.dsl.parser.annotation;

import java.math.BigDecimal;
import java.util.List;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.commons.ParamValue;
import org.telosys.tools.dsl.parser.commons.ParamValueOrigin;
import org.telosys.tools.dsl.parser.exceptions.AnnotationParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.enums.BooleanValue;

public abstract class AnnotationDefinition {

	private final String name;
	private final AnnotationParamType paramType;
	// Annotation scope :
	private boolean attributeScope = false ;
	private boolean linkScope      = false ;
	private boolean entityScope    = false ;

	/**
	 * Constructor
	 * @param name
	 * @param paramType
	 * @param scopes
	 */
	protected AnnotationDefinition(String name, AnnotationParamType paramType, AnnotationScope... scopes) {
		this.name = name ;
		this.paramType = paramType ;
		for ( AnnotationScope scope : scopes ) {
			if ( scope == AnnotationScope.ATTRIBUTE ) {
				this.attributeScope = true ;
			}
			else if ( scope == AnnotationScope.LINK ) {
				this.linkScope = true ;
			}
			else if (  scope == AnnotationScope.ENTITY ) {
				this.entityScope = true ;
			}
		}
	}

	public String getName() {
		return name;
	}

	public AnnotationParamType getParamType() {
		return paramType;
	}
	public boolean hasParam() {
		return paramType != AnnotationParamType.NONE;
	}
	
	public boolean hasAttributeScope() {
		return attributeScope;
	}
	public boolean hasLinkScope() {
		return linkScope;
	}
	public boolean hasEntityScope() {
		return entityScope;
	}

	//-------------------------------------------------------------------------------------------
	// Annotation error 
	//-------------------------------------------------------------------------------------------
	protected IllegalStateException newException(String msg) {
		return new IllegalStateException("@" + this.name + " : " + msg);
	}

	//-------------------------------------------------------------------------------------------
	// Annotation parsing 
	//-------------------------------------------------------------------------------------------
	/**
	 * Builds an annotation instance from its definition and the given param value
	 * @param entityName
	 * @param fieldName
	 * @param parameterValue
	 * @return
	 * @throws ParsingError
	 */
	public DomainAnnotation buildAnnotation(String entityName, String fieldName, 
			String parameterValue) throws ParsingError {
		DomainAnnotation annotation = createAnnotation(entityName, fieldName, parameterValue);
		afterCreation(annotation);
		return annotation;
	}
	
	private DomainAnnotation createAnnotation(String entityName, String fieldName, 
			String parameterValue) throws ParsingError {
		ParamValue paramValue = new ParamValue(entityName, fieldName, name, 
									parameterValue, ParamValueOrigin.FIELD_ANNOTATION);
		switch(this.paramType) {
		case STRING :
			return new DomainAnnotation(name, paramValue.getAsString() );
		case INTEGER :
			return new DomainAnnotation(name, paramValue.getAsInteger() );
		case DECIMAL :
			return new DomainAnnotation(name, paramValue.getAsBigDecimal() );
		case BOOLEAN :
			return new DomainAnnotation(name, paramValue.getAsBoolean() );
		case SIZE :
			return new DomainAnnotation(name, paramValue.getAsSize() );
		case LIST :
			return new DomainAnnotation(name, paramValue.getAsList() );
		case FK_ELEMENT :
			return new DomainAnnotation(name, paramValue.getAsForeignKeyElement() );

		default :
			// annotation without parameter
			if (parameterValue != null) {
				throw new AnnotationParsingError(entityName, fieldName, name, "unexpected parameter");
			}
			return new DomainAnnotation(name);
		}
	}
	
	protected void afterCreation(DomainAnnotation annotation) {
		// Override this method to process the annotation after build
	}

	//-------------------------------------------------------------------------------------------
	// Annotation application ( on attribute or link )
	//-------------------------------------------------------------------------------------------
	protected void checkParamValue(Object paramValue) {
		switch ( this.paramType ) {
		case STRING:
			if ( ! ( paramValue instanceof String ) ) {
				throw newException("String value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case SIZE: // Size is stored as a String
			if ( ! ( paramValue instanceof String ) ) {
				throw newException("Size value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case FK_ELEMENT: // FK element is stored as a String
			if ( ! ( paramValue instanceof String ) ) {
				throw newException("FK element expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case INTEGER:
			if ( ! ( paramValue instanceof Integer ) ) {
				throw newException("Integer value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case DECIMAL:
			if ( ! ( paramValue instanceof BigDecimal ) ) {
				throw newException("BigDecimal value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case BOOLEAN:
			if ( ! ( paramValue instanceof Boolean ) ) {
				throw newException("Boolean value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case LIST:
			if ( ! ( paramValue instanceof List<?> ) ) {
				throw newException("List value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case NONE:
			if ( paramValue != null ) {
				throw newException("No value expected, actual value is " + paramValue );
			}
			break;
		default:
			throw newException("Unexpected parameter type '" + this.paramType + "'" );
		}
	}
	
	private String getParamValueActualType(Object paramValue) {
		if ( paramValue != null ) {
			return paramValue.getClass().getCanonicalName() ;
		}
		else {
			return "null" ;
		}
	}
	
	/**
	 * Returns the special "BooleanValue" tyoe (Telosys type) 
	 * @param paramValue
	 * @return
	 */
	protected BooleanValue getBooleanValue(Object paramValue) {
		Boolean b = (Boolean) paramValue;
		if ( Boolean.TRUE.equals( b ) ) {
			return BooleanValue.TRUE;
		}
		else {
			return BooleanValue.FALSE;
		}
	}
	
	protected int toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw newException("Cannot convert '"+s+"' to int");
		}  
	}
	
	/**
	 * Apply the current annotation to the given attribute
	 * (supposed to be overridden in the annotation)
	 * @param model
	 * @param entity
	 * @param attribute
	 * @param paramValue
	 */
	public void apply(DslModel model, DslModelEntity entity, DslModelAttribute attribute, Object paramValue) {
		throw new IllegalStateException("apply(attribute) is not implemented");
	}
	
	/**
	 * Apply the current annotation to the given link
	 * (supposed to be overridden in the annotation)
	 * @param model
	 * @param entity
	 * @param link
	 * @param paramValue
	 */
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) {
		throw new IllegalStateException("apply(link) is not implemented");
	}
	
}
