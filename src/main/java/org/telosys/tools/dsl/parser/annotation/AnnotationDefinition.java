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

import org.telosys.tools.commons.exception.TelosysRuntimeException;
import org.telosys.tools.dsl.commons.StringUtil;
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
		if ( scopes.length < 1 ) {
			throw new IllegalArgumentException("@"+name+" : no scope");
		}
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
	// Annotation parsing 
	//-------------------------------------------------------------------------------------------
	public void afterCreation(String entityName, String fieldName, 
								 DomainAnnotation annotation) throws ParamError {		
		// Override this method to process the annotation after build
	}

	//-------------------------------------------------------------------------------------------
	// Check annotation parameter value before application ( on entity or attribute or link )
	// Ultimate check (no errors expected at this level, already checked in parameter constructor)
	//-------------------------------------------------------------------------------------------
	protected void checkParamValue(DslModelEntity entity, Object paramValue) throws ParamError {
		checkParamValue(paramValue);
	}
	protected void checkParamValue(DslModelEntity entity, DslModelAttribute attribute, Object paramValue) throws ParamError {
		checkParamValue(paramValue);
	}
	protected void checkParamValue(DslModelEntity entity, DslModelLink link, Object paramValue) throws ParamError {
		checkParamValue(paramValue);
	}

	private void checkParamType(Object paramValue, Class<?> expectedClass, String expectedMsg ) throws ParamError {
		if ( paramValue == null ) {
			throw new ParamError("Parameter is null (parameter expected)");
		}
		else {
			// check if paramValue is instance of the expected class
			if ( ! ( expectedClass.isInstance(paramValue) ) ) {
				throw new ParamError(expectedMsg + " expected, actual type is " + getParamValueActualType(paramValue));
			}
		}
	}
	private void checkParamValue(Object paramValue) throws ParamError {
		switch ( this.paramType ) {
		case STRING:
			checkParamType(paramValue, String.class, "String value");
			break;
		case SIZE: // Size is stored as a String
			checkParamType(paramValue, String.class, "Size value");
			break;
		case FK_ELEMENT: // FK element is stored as a String
			checkParamType(paramValue, String.class, "FK element");
			break;
		case INTEGER:
			checkParamType(paramValue, Integer.class, "Integer value");
			break;
		case DECIMAL:
			checkParamType(paramValue, BigDecimal.class, "BigDecimal value");
			break;
		case BOOLEAN:
			checkParamType(paramValue, Boolean.class, "Boolean value");
			break;
		case LIST:
			checkParamType(paramValue, List.class, "List value");
			break;
		case NONE:
			if ( paramValue != null ) {
				throw new ParamError("No value expected, actual value is " + paramValue );
			}
			break;
		default:
			throw new ParamError("Unexpected parameter type '" + this.paramType + "'" );
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
	
	protected int toInt(String s) throws ParamError {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new ParamError("Cannot convert '" + s + "' to int");
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
	
	private String atName() {
		return "@" + this.name ;
	}
	public String literal() {
		if ( ! this.hasParam() ) {
			return atName() ;
		}
		else {
			throw new IllegalStateException("literal() parameter expected");
		}
	}
	public String literal(String param) {
		if ( this.hasParam() ) {
			// protect with double quote ".." if necessary
			return atName() + "(" + protectStringIfNecessary(param) + ")";
		}
		else {
			throw new IllegalStateException("literal(String) no parameter expected");
		}
	}
	public String literal(BigDecimal param) {
		if ( this.hasParam() ) {
			return atName() + "(" + param + ")";
		}
		else {
			throw new IllegalStateException("literal(BigDecimal) no parameter expected");
		}
	}
	public String literal(Integer param) {
		if ( this.hasParam() ) {
			return atName() + "(" + param + ")";
		}
		else {
			throw new IllegalStateException("literal(Integer) no parameter expected");
		}
	}
	private String protectStringIfNecessary(String s) {
		if ( s.startsWith(" ") || s.endsWith(" ") 
				|| s.contains("(") || s.contains(")") 
				|| s.contains("\"") ) {
			return StringUtil.quote(s);
		}
		else {
			return s;
		}
	}
	
	/**
	 * Checks if the number of parameters is OK <br>
	 * (for an annotation with a list of parameters)
	 * @param annotation
	 * @param minimumExpected
	 * @param maximumExpected
	 * @param errorMessage
	 * @throws ParamError
	 */
	protected void checkNumberOfParameters(DomainAnnotation annotation, int minimumExpected, int maximumExpected, String errorMessage) throws ParamError {
		int numberOfParameters = annotation.getParameterAsList().size();
		if ( numberOfParameters < minimumExpected || numberOfParameters > maximumExpected ) {
			throw new ParamError(errorMessage);
		}
	}
	
	/**
	 * Converts the given parameter object to a List of String <br>
	 * (for an annotation with a list of parameters)
	 * @param param
	 * @return
	 */
	protected List<String> getListOfParameters(Object param) {
		if (param instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) param;
			return list;
		}
		else {
			throw new TelosysRuntimeException("@"+this.getName() + " : getParameter() : unexpected type " + param.getClass().getSimpleName() );
		}
	}
	
	/**
	 * Checks if the given parameter object is a list and has a parameter at the given index <br>
	 * (for an annotation with a list of parameters)
	 * @param param
	 * @param index
	 * @return
	 */
	protected boolean hasParameter(Object param, int index) {
		List<String> list = getListOfParameters(param);
		return index < list.size();
	}
	
	/**
	 * Returns the parameter value located at the given index in the given parameter object (list expected) <br>
	 * (for an annotation with a list of parameters)
	 * @param param
	 * @param index
	 * @return
	 */
	protected String getParameter(Object param, int index) {
		List<String> list = getListOfParameters(param);
		if ( index < list.size() ) {
			return list.get(index);
		}
		else {
			throw new TelosysRuntimeException("@"+this.getName() + " : getParameter("+ index + ") : index out of bounds");
		}
	}

}
