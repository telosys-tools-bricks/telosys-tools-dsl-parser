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

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.commons.ParamError;
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
			else if ( scope == AnnotationScope.ENTITY ) {
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
	protected ParamError newParamError(DslModelEntity entity, DslModelAttribute attribute, String error) {
		return new ParamError(entity.getClassName() + "." + attribute.getName() + " : " + error); 
	}
	protected ParamError newParamError(DslModelEntity entity, DslModelLink link, String error) {
		return new ParamError(entity.getClassName() + "." + link.getFieldName() + " : " + error); 
	}
	protected ParamError newParamError(String entityName, String fieldName, String error) {
//		return new ParamError(entityName + "." + fieldName + " : " + error);
		StringBuilder sb = new StringBuilder();
		sb.append(entityName);
		if ( fieldName != null ) {
			sb.append(".").append(fieldName);
		}
		sb.append(" : ").append(error);
		return new ParamError(sb.toString());
	}
	protected DslModelError newError(String entityName, int lineNumber, String fieldName, String errorMessage) {
		return new DslModelError(entityName, lineNumber, fieldName, errorMessage);
	}
	
	//-------------------------------------------------------------------------------------------
	// Annotation parsing 
	//-------------------------------------------------------------------------------------------
	public void afterCreation(String entityName, String fieldName, 
								 DomainAnnotation annotation) throws ParamError {		
		// Override this method to process the annotation after build
	}

	//-------------------------------------------------------------------------------------------
	// Check annotation parameter value before application ( on entity or attribute or link )
	//-------------------------------------------------------------------------------------------
	protected void checkParamValue(DslModelEntity entity, Object paramValue) throws ParamError {
		checkParamValue(entity.getClassName(), null, paramValue);
	}
	protected void checkParamValue(DslModelEntity entity, DslModelAttribute attribute, Object paramValue) throws ParamError {
		checkParamValue(entity.getClassName(), attribute.getName(), paramValue);
	}
	protected void checkParamValue(DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(entity.getClassName(), link.getFieldName(), paramValue);
	}

	private void checkParamValue(String entityName, String fieldName, Object paramValue) throws ParamError {
		switch ( this.paramType ) {
		case STRING:
			if ( ! ( paramValue instanceof String ) ) {
				throw newParamError(entityName, fieldName, "String value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case SIZE: // Size is stored as a String
			if ( ! ( paramValue instanceof String ) ) {
				throw newParamError(entityName, fieldName, "Size value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case FK_ELEMENT: // FK element is stored as a String
			if ( ! ( paramValue instanceof String ) ) {
				throw newParamError(entityName, fieldName, "FK element expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case INTEGER:
			if ( ! ( paramValue instanceof Integer ) ) {
				throw newParamError(entityName, fieldName, "Integer value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case DECIMAL:
			if ( ! ( paramValue instanceof BigDecimal ) ) {
				throw newParamError(entityName, fieldName, "BigDecimal value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case BOOLEAN:
			if ( ! ( paramValue instanceof Boolean ) ) {
				throw newParamError(entityName, fieldName, "Boolean value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case LIST:
			if ( ! ( paramValue instanceof List<?> ) ) {
				throw newParamError(entityName, fieldName, "List value expected, actual type is " 
							+ getParamValueActualType( paramValue));
			}
			break;
		case NONE:
			if ( paramValue != null ) {
				throw newParamError(entityName, fieldName, "No value expected, actual value is " + paramValue );
			}
			break;
		default:
			throw newParamError(entityName, fieldName, "Unexpected parameter type '" + this.paramType + "'" );
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
	
	protected int toInt(DslModelEntity entity, DslModelAttribute attribute, String s) throws ParamError {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw newParamError(entity, attribute, "Cannot convert '"+s+"' to int");
		}  
	}
	
	/**
	 * Apply the current annotation to the given entity
	 * (supposed to be overridden in the annotation)
	 * @param model
	 * @param entity
	 * @param paramValue
	 * @throws ParamError
	 */
	public void applyToEntity(DslModel model, DslModelEntity entity, Object paramValue) throws ParamError {
		throw new IllegalStateException("applyToEntity() is not implemented");
	}
	
	/**
	 * Apply the current annotation to the given attribute
	 * (supposed to be overridden in the annotation)
	 * @param model
	 * @param entity
	 * @param attribute
	 * @param paramValue
	 */
	public void apply(DslModel model, DslModelEntity entity, DslModelAttribute attribute, Object paramValue) throws ParamError {
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
	public void apply(DslModel model, DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		throw new IllegalStateException("apply(link) is not implemented");
	}
	
}
