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
package org.telosys.tools.dsl.parser.commnos;

import java.math.BigDecimal;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.parser.exceptions.AnnotationParsingError;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.exceptions.TagParsingError;

public class ParamValue {

	private final String entityName;
	private final String fieldName;
	private final String annotationOrTagName;
	private final String parameterValue;
	private final ParamValueOrigin origin;
	
	public ParamValue(String entityName, String fieldName, 
			String annotationOrTagName, String parameterValue, ParamValueOrigin origin) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.annotationOrTagName = annotationOrTagName;
		this.parameterValue = parameterValue;
		this.origin = origin;
	}

	/**
	 * Creates a new error 
	 * @param message
	 * @return
	 */
	private ParsingError newError(String message) {
		switch(origin) {
		case ENTITY_ANNOTATION :
			return new AnnotationParsingError(entityName, annotationOrTagName, message);
		case ENTITY_TAG :
			return new TagParsingError(entityName, annotationOrTagName, message);
		case FIELD_ANNOTATION :
			return new AnnotationParsingError(entityName, fieldName, annotationOrTagName, message);
		case FIELD_TAG :
			return new TagParsingError(entityName, fieldName, annotationOrTagName, message);
		default:
			// can't happen
			return new EntityParsingError(entityName, annotationOrTagName + " : " + message);
		}
	}
	
	/**
	 * Check if parameter value exist
	 * @throws FieldParsingError
	 */
	private void checkParameterExistence() throws ParsingError { 
		if (parameterValue == null || parameterValue.length() == 0) {
			throw newError("parameter required");
		}
	}
	
	/**
	 * Try to convert the parameter value to Integer value
	 * @return
	 * @throws FieldParsingError
	 */
	public Integer getAsInteger() throws ParsingError { 
		checkParameterExistence();
		
		try {
			return new Integer(parameterValue);
		} catch (NumberFormatException e) {
			throw newError("invalid integer parameter '" + parameterValue + "'");
		}
	}
	
	/**
	 * Try to convert the parameter value to BigDecimal value
	 * @return
	 * @throws FieldParsingError
	 */
	public BigDecimal getAsBigDecimal() throws ParsingError {
		checkParameterExistence();
		
		try {
			return new BigDecimal(parameterValue);
		} catch (NumberFormatException e) {
			throw newError("invalid decimal parameter '" + parameterValue + "'");
		}
	}
	
	/**
	 * Try to convert the parameter value to Boolean value
	 * @return
	 * @throws FieldParsingError
	 */
	public Boolean getAsBoolean() throws ParsingError {
		checkParameterExistence();
		
		if ( ! StrUtil.nullOrVoid(parameterValue) ) {
			String v = parameterValue.trim().toUpperCase();
			if ("TRUE".equals(v)) {
				return Boolean.TRUE ;
			}
			else if ("FALSE".equals(v)) {
				return Boolean.FALSE ;
			}
		}
		throw newError("invalid boolean parameter '" + parameterValue + "'");
	}
	
	/**
	 * Try to convert the parameter value to String value
	 * @return
	 * @throws FieldParsingError
	 */
	public String getAsString() throws ParsingError {
		checkParameterExistence();
		
		// remove all void chars ( blank, tab, cr, lf, ...)
		String s = parameterValue.trim(); 
		// remove quotes if any
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return unquote(s, '"');
		} else if (s.startsWith("'") && s.endsWith("'")) {
			return unquote(s, '\'');
		} else {
			return s;
		}
	}
	
	/**
	 * Remove quote char at the first and last position if any
	 * 
	 * @param s
	 * @param quoteChar
	 * @return
	 */
	private String unquote(String s, char quoteChar) {
		if (s == null) {
			return s;
		}
		if (s.length() == 0) {
			return s;
		}
		int last = s.length() - 1;
		if (s.charAt(0) == quoteChar && s.charAt(last) == quoteChar) {
			return s.substring(1, last);
		} else {
			return s;
		}
	}
	
	/**
	 * Check that the parameter value conforms to the 'SIZE' format
	 * and return it if OK
	 * @return
	 * @throws ParsingError
	 */
	public String getAsSize() throws ParsingError {
		checkParameterExistence();
		checkSizeParameter(parameterValue);
		return parameterValue;
	}
	
	private void checkSizeParameter(String p) throws ParsingError {
		if ( p.contains(",")) {
			String[] parts = p.split(",");
			if (parts.length  != 2) {
				throw newError("invalid size parameter '" + p + "' (xx,xx expected)");
			}
			checkSizeInteger(parts[0]);
			checkSizeInteger(parts[1]);
		}
		else {
			checkSizeInteger(p);
		}
	}
	
	private void checkSizeInteger(String p) throws ParsingError {
		try {
			Integer i = new Integer(p);
			if ( i < 0 ) {
				throw newError("invalid size parameter '" + p + "' (negative value)");
			}
		} catch (NumberFormatException e) {
			throw newError("invalid size parameter '" + p + "' (not a number)");
		}
	}
	
}
