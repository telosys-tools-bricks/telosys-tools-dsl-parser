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
package org.telosys.tools.dsl.parser.commons;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.commons.StringUtil;

public class ParamValue {

	private final String entityName;
	private final String fieldName;
	private final String annotationOrTagName;
	private final String parameterValue;
	private final ParamValueOrigin origin;
	
	public ParamValue(String entityName, String fieldName, 
			String annotationOrTagName, String rawParameterValue, ParamValueOrigin origin) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.annotationOrTagName = annotationOrTagName;
		this.parameterValue = rawParameterValue;
		this.origin = origin;
	}

	/**
	 * Creates a new error 
	 * @param message
	 * @return
	 */
	private ParamError newError(String message) {
		return new ParamError(message);
	}
	
	/**
	 * Check if parameter value exist
	 * @throws FieldParsingError
	 */
	private void checkParameterExistence() throws ParamError { 
		if (parameterValue == null || parameterValue.length() == 0) {
			throw newError("parameter required");
		}
	}
	
	/**
	 * Try to convert the parameter value to Integer value
	 * @return
	 * @throws FieldParsingError
	 */
	public Integer getAsInteger() throws ParamError { 
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
	public BigDecimal getAsBigDecimal() throws ParamError {
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
	public Boolean getAsBoolean() throws ParamError {
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
	public String getAsString() throws ParamError {
		checkParameterExistence();
		
		// remove all void chars ( blank, tab, cr, lf, ...)
		String s = parameterValue.trim(); 
		// remove quotes if any
		if (s.startsWith("\"") && s.endsWith("\"")) {
			return StringUtil.unquote(s);
		} else {
			return s;
		}
	}
	
	/**
	 * Check that the parameter value conforms to the 'SIZE' format
	 * and return it if OK
	 * @return
	 * @throws ParamError
	 */
	public String getAsSize() throws ParamError {
		checkParameterExistence();
		checkSizeParameter(parameterValue);
		return parameterValue;
	}
	
	private void checkSizeParameter(String p) throws ParamError {
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
	
	private void checkSizeInteger(String p) throws ParamError {
		try {
			Integer i = new Integer(p);
			if ( i < 0 ) {
				throw newError("invalid size parameter '" + p + "' (negative value)");
			}
		} catch (NumberFormatException e) {
			throw newError("invalid size parameter '" + p + "' (not a number)");
		}
	}
	
	public FkElement getAsForeignKeyElement() throws ParamError {
		checkParameterExistence();
		FkElementBuilder builder = new FkElementBuilder(entityName);
		return builder.build(parameterValue);
	}
	
	public List<String> getAsList() throws ParamError {
		checkParameterExistence();
		return buildList(parameterValue);
	}
	
	private List<String> buildList(String paramString) throws ParamError {
		String[] elements = StrUtil.split(paramString, ',');
		if (elements.length == 0 ) {
			throw newError("invalid list parameter (at list 1 element expected)");
		}
		
		List<String> list = new LinkedList<>();
		for ( String s : elements ) {
			list.add(s.trim());
		}
		return list;
	}

}
