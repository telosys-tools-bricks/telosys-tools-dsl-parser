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
package org.telosys.tools.dsl.parser;

import java.math.BigDecimal;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamValidator;
import org.telosys.tools.dsl.parser.annotation.Annotations;
import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;
import org.telosys.tools.dsl.parser.model.DomainTag;

/**
 * Field annotations parsing
 * 
 * @author Laurent GUERIN
 */
public class FieldAnnotationOrTagParser {

	private static final char OPENING_PARENTHESIS = '(';
	private static final char CLOSING_PARENTHESIS = ')';

	private final String entityName;
	private final String fieldName;
	private final AnnotationParamValidator paramValidator;
	
	/**
	 * Constructor
	 * 
	 * @param entityName
	 * @param fieldName
	 */
	public FieldAnnotationOrTagParser(String entityName, String fieldName) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.paramValidator = new AnnotationParamValidator(entityName, fieldName);
	}

	/**
	 * Parse a single annotation or tag
	 * 
	 * @param entityName
	 * @param fieldName
	 * @param annotationString
	 *            e.g. "@Id", "@Max(12)", "#Tag", etc
	 * @return
	 */
	public DomainAnnotationOrTag parse(String annotationOrTagString) throws ParsingError { // AnnotationOrTagError {

		char firstChar = annotationOrTagString.charAt(0);
		if (firstChar == '@') {
			DomainAnnotation annotation = parseAnnotation(annotationOrTagString);
			checkAnnotation(annotation);
			return annotation;
		} else if (firstChar == '#') {
			return parseTag(annotationOrTagString);
		} else {
			throw new AnnotationOrTagError(entityName, fieldName, annotationOrTagString, "must start with '@' or '#'");
		}
	}

	protected DomainAnnotation parseAnnotation(String annotation) throws ParsingError { //AnnotationOrTagError {
		
		// get the name 
		String name = getName(annotation);

		// get the raw parameter value if any
		String parameterValue = getParameterValue(annotation);

		/**
		// check annotation exist
		List<String> definedAnnotations = KeyWords.getAnnotations();

		// is it a known annotation ?
		for (String annotationDefinition : definedAnnotations) { // "Id",
																	// "SizeMin%",
																	// "SizeMax%",
																	// "Max#",
																	// "Min#",
																	// ""...
			if (annotationDefinition.startsWith(name)) { // TODO : Bug "Size" and "SizeMin" all startswith !
				// Annotation name found in defined annotations
				if (annotationDefinition.endsWith("%")) { // INTEGER parameter
															// required
					// this annotation must have an integer parameter between (
					// and )
					Integer numberValue = null;
					try {
						numberValue = getParameterValueAsInteger(annotation, parameterValue);
					} catch (Exception e) {
						throw new AnnotationOrTagError(entityName, fieldName, annotation, "integer parameter required");
						
					}
					return new DomainAnnotation(name, numberValue);
				} else if (annotationDefinition.endsWith("#")) { // DECIMAL
																	// parameter
																	// required
					BigDecimal numberValue = null;
					try {
						numberValue = getParameterValueAsBigDecimal(annotation, parameterValue);
					} catch (Exception e) {
						throw new AnnotationOrTagError(entityName, fieldName, annotation, "numeric parameter required");
					}
					return new DomainAnnotation(name, numberValue);
				} else if (annotationDefinition.endsWith("$")) { // STRING
																	// parameter
																	// required
					String value = null;
					try {
						value = getParameterValueAsString(annotation, parameterValue);
					} catch (Exception e) {
						throw new AnnotationOrTagError(entityName, fieldName, annotation, "string parameter required");
					}
					return new DomainAnnotation(name, value);
				} else {
					// annotation without parameter
					if (parameterValue != null) {
						throw new AnnotationOrTagError(entityName, fieldName, annotation, "unexpected parameter");
					}
					return new DomainAnnotation(name);
				}
			}
		}
		throw new AnnotationOrTagError(entityName, fieldName, annotation, "unknown annotation");
		**/
		AnnotationDefinition ad = Annotations.get(name);
		if ( ad != null ) {
			/**
			DomainAnnotation domainAnnotation = null;
			switch(ad.getParamType()) {
			case STRING :
				domainAnnotation = new DomainAnnotation(name, 
									getParameterValueAsString(annotation, parameterValue) );
				break;
			case INTEGER :
				domainAnnotation = new DomainAnnotation(name, 
									getParameterValueAsInteger(annotation, parameterValue) );
				break;
			case DECIMAL :
				domainAnnotation = new DomainAnnotation(name, 
									getParameterValueAsBigDecimal(annotation, parameterValue) );
				break;
			case BOOLEAN :
				domainAnnotation = new DomainAnnotation(name, 
									getParameterValueAsBoolean(annotation, parameterValue) );
				break;
			default :
				// annotation without parameter
				if (parameterValue != null) {
					throw new AnnotationOrTagError(entityName, fieldName, annotation, "unexpected parameter");
				}
				domainAnnotation = new DomainAnnotation(name);
				break;
			}
			return domainAnnotation;
			**/
			return ad.buildAnnotation(name, name, annotation, parameterValue);
		}
		else {
			throw new AnnotationOrTagError(entityName, fieldName, annotation, "unknown annotation");
		}
	}

	protected void checkAnnotation(DomainAnnotation annotation) throws AnnotationOrTagError  {
		if ( annotation.getName().equals(AnnotationName.DB_SIZE) 
		  || annotation.getName().equals(AnnotationName.SIZE) ) {
			
			paramValidator.checkSize(annotation);
		}
	}

	protected DomainTag parseTag(String tagString) throws AnnotationOrTagError  {
		String name = getName(tagString);
		String rawParameterValue = getParameterValue(tagString);
		if ( rawParameterValue != null ) {
			String parameterValue = getParameterValueAsString(rawParameterValue);
			return new DomainTag(name, parameterValue);
		}
		else {
			return new DomainTag(name);
		}
	}

	/**
	 * Returns the annotation name <br>
	 * 
	 * @param annotationOrTag
	 *            e.g. "@Id", "@Max(12)", etc
	 * @return "Id", "Max", etc
	 * @throws Exception
	 */
	protected String getName(String annotationOrTag) throws AnnotationOrTagError {
		boolean blankCharFound = false;
		StringBuilder sb = new StringBuilder();
		// skip the first char (supposed to be '@' or '#' )
		for (int i = 1; i < annotationOrTag.length(); i++) {
			char c = annotationOrTag.charAt(i);
			if (Character.isLetter(c)) {
				if (blankCharFound) {
					// Case letter after a blank char : "Id xxx" or "aaa bbb"
					throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "invalid name");
				}
				sb.append(c);
			} else if (Character.isWhitespace(c)) {
				blankCharFound = true;
			} else if (c == '(') {
				break;
			} else {
				// Unexpected ending character
				throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "invalid name");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the parameter value if any <br>
	 * Returns the raw string located between the '(' and ')'.<br>
	 * The returned value is trimed.<br>
	 * 
	 * @param annotationOrTag
	 *            e.g. "@Id", "@Max(12)", etc
	 * @return the parameter value or null if none
	 * @throws Exception
	 */
	protected String getParameterValue(String annotationOrTag) throws AnnotationOrTagError { 
		int openIndex = annotationOrTag.indexOf(OPENING_PARENTHESIS); //  first occurrence of '('
		int closeIndex = annotationOrTag.lastIndexOf(CLOSING_PARENTHESIS); // last occurrence of ')'
		if (openIndex < 0 && closeIndex < 0) {
			// no open nor close char
			return null;
		} else {
			// 1 or 2 chars found
			if (openIndex >= 0 && closeIndex >= 0) {
				// open and close char found
				if (openIndex < closeIndex) {
					// valid order eg "(aa)"
					// get string between ( and )
					String paramValue = annotationOrTag.substring(openIndex + 1, closeIndex);
					// trim
					return paramValue.trim();
				} else {
					// unbalanced ( and ) eg ")aa("
					throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "unbalanced ( and )");
				}
			} else {
				// unbalanced ( and ) eg "(aa" or "aa)"
				if (openIndex < 0) {
					throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "'(' missing");
				} else {
					throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "')' missing");
				}
			}
		}
	}

	/***
	private void checkParameterExistence(String annotationOrTag, String parameterValue) throws AnnotationOrTagError { 
		if (parameterValue == null || parameterValue.length() == 0) {
			throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "parameter required");
		}
	}

	protected Integer getParameterValueAsInteger(String annotationOrTag, String parameterValue) throws AnnotationOrTagError { 
		// Integer value
		checkParameterExistence(annotationOrTag, parameterValue);
		try {
			return new Integer(parameterValue);
		} catch (NumberFormatException e) {
			throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "invalid integer parameter '" + parameterValue + "'");
		}
	}

	protected BigDecimal getParameterValueAsBigDecimal(String annotationOrTag, String parameterValue) throws AnnotationOrTagError {
		// Decimal value
		checkParameterExistence(annotationOrTag, parameterValue);
		try {
			return new BigDecimal(parameterValue);
		} catch (NumberFormatException e) {
			throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "invalid decimal parameter '" + parameterValue + "'");
		}
	}

	protected Boolean getParameterValueAsBoolean(String annotationOrTag, String parameterValue) throws AnnotationOrTagError {
		// Boolean value
		checkParameterExistence(annotationOrTag, parameterValue);
		
		if ( ! StrUtil.nullOrVoid(parameterValue) ) {
			String v = parameterValue.trim().toUpperCase();
			if ("TRUE".equals(v)) {
				return Boolean.TRUE ;
			}
			else if ("FALSE".equals(v)) {
				return Boolean.FALSE ;
			}
		}
		throw new AnnotationOrTagError(entityName, fieldName, annotationOrTag, "invalid boolean parameter '" + parameterValue + "'");
	}
	
	protected String getParameterValueAsString(String annotationOrTag, String parameterValue) throws AnnotationOrTagError {
		checkParameterExistence(annotationOrTag, parameterValue);
		return getParameterValueAsString(parameterValue);
	}
	
	protected String getParameterValueAsString(String parameterValue) {
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
**/
	/**
	 * Remove quote char at the first and last position if any
	 * 
	 * @param s
	 * @param quoteChar
	 * @return
	 */
	/**
	protected static String unquote(String s, char quoteChar) {
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
**/
}