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
import java.util.List;

import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.KeyWords;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;
import org.telosys.tools.dsl.parser.model.DomainTag;

/**
 * Field annotations parsing
 * 
 * @author Laurent Guerin
 */
public class AnnotationOrTagParser {

	private static final char OPENING_PARENTHESIS = '(';
	private static final char CLOSING_PARENTHESIS = ')';

	private final String entityName;
	private final String fieldName;

	/**
	 * Constructor
	 * 
	 * @param entityName
	 * @param fieldName
	 */
	public AnnotationOrTagParser(String entityName, String fieldName) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
	}

	/**
	 * Throws an annotation parsing exception
	 * 
	 * @param annotationString
	 * @param message
	 */
	private void throwAnnotationParsingError(String annotationString, String message) {
		String errorMessage = entityName + "." + fieldName + " : '" + annotationString + "' (" + message + ")";
		throw new DslParserException(errorMessage);
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
	public DomainAnnotationOrTag parse(String annotationOrTagString) {

		char firstChar = annotationOrTagString.charAt(0);
		if (firstChar == '@') {
			DomainAnnotationOrTag annotation = parseAnnotation(annotationOrTagString);
			// TODO annotationOrTag.setType(ANNOTATION);
			return annotation;
		} else if (firstChar == '#') {
			DomainAnnotationOrTag tag = parseTag(annotationOrTagString);
			// TODO annotationOrTag.setType(TAG);
			return tag;
		} else {
			throwAnnotationParsingError(annotationOrTagString, "must start with '@' or '#'");
		}
		return null;
	}

	protected DomainAnnotationOrTag parseAnnotation(String annotation) {
		
		// get the name 
		String name = getName(annotation);

		// get the raw parameter value if any
		String parameterValue = getParameterValue(annotation);

		// check annotation exist
		List<String> definedAnnotations = KeyWords.getAnnotations();

		// is it a known annotation ?
		for (String annotationDefinition : definedAnnotations) { // "Id",
																	// "SizeMin%",
																	// "SizeMax%",
																	// "Max#",
																	// "Min#",
																	// ""...
			if (annotationDefinition.startsWith(name)) {
				// Annotation name found in defined annotations
				if (annotationDefinition.endsWith("%")) { // INTEGER parameter
															// required
					// this annotation must have an integer parameter between (
					// and )
					Integer numberValue = null;
					try {
						numberValue = getParameterValueAsInteger(annotation, parameterValue);
					} catch (Exception e) {
						throwAnnotationParsingError(annotation, "integer parameter required ");
					}
					return new DomainAnnotation(name, numberValue);
				} else if (annotationDefinition.endsWith("#")) { // DECIMAL
																	// parameter
																	// required
					BigDecimal numberValue = null;
					try {
						numberValue = getParameterValueAsBigDecimal(annotation, parameterValue);
					} catch (Exception e) {
						throwAnnotationParsingError(annotation, "numeric parameter required ");
					}
					return new DomainAnnotation(name, numberValue);
				} else if (annotationDefinition.endsWith("$")) { // STRING
																	// parameter
																	// required
					String value = null;
					try {
						value = getParameterValueAsString(annotation, parameterValue);
					} catch (Exception e) {
						throwAnnotationParsingError(annotation, "string parameter required ");
					}
					return new DomainAnnotation(name, value);
				} else {
					// annotation without parameter
					if (parameterValue != null) {
						throwAnnotationParsingError(annotation, "unexpected parameter");
					}
					return new DomainAnnotation(name);
				}
			}
		}

		throwAnnotationParsingError(annotation, "unknown annotation");
		return null; // never reached
	}

	protected DomainAnnotationOrTag parseTag(String tagString) {
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
	protected String getName(String annotationOrTag) {
		boolean blankCharFound = false;
		StringBuilder sb = new StringBuilder();
		// skip the first char (supposed to be '@' or '#' )
		for (int i = 1; i < annotationOrTag.length(); i++) {
			char c = annotationOrTag.charAt(i);
			if (Character.isLetter(c)) {
				if (blankCharFound) {
					// Case letter after a blank char : "Id xxx" or "aaa bbb"
					throwAnnotationParsingError(annotationOrTag, "Invalid syntax");
				}
				sb.append(c);
			} else if (Character.isWhitespace(c)) {
				blankCharFound = true;
			} else if (c == '(') {
				break;
			} else {
				// Unexpected ending character
				throwAnnotationParsingError(annotationOrTag, "Invalid syntax");
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
	protected String getParameterValue(String annotationOrTag) { 
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
					throwAnnotationParsingError(annotationOrTag, "unbalanced ( and )");
				}
			} else {
				// unbalanced ( and ) eg "(aa" or "aa)"
				if (openIndex < 0) {
					throwAnnotationParsingError(annotationOrTag, "'(' missing");
				} else {
					throwAnnotationParsingError(annotationOrTag, "')' missing");
				}
			}
		}
		return null;
	}

	private void checkParameterExistence(String annotationOrTag, String parameterValue) { 
		if (parameterValue == null || parameterValue.length() == 0) {
			throwAnnotationParsingError(annotationOrTag, "Parameter required");
		}
	}

	protected Integer getParameterValueAsInteger(String annotationOrTag, String parameterValue) { 
		// Integer value
		checkParameterExistence(annotationOrTag, parameterValue);
		try {
			return new Integer(parameterValue);
		} catch (NumberFormatException e) {
			throwAnnotationParsingError(annotationOrTag, "Invalid integer parameter '" + parameterValue + "'");
			return 0;
		}
	}

	protected BigDecimal getParameterValueAsBigDecimal(String annotationOrTag, String parameterValue) {
		// Decimal value
		checkParameterExistence(annotationOrTag, parameterValue);
		try {
			return new BigDecimal(parameterValue);
		} catch (NumberFormatException e) {
			throwAnnotationParsingError(annotationOrTag, "Invalid decimal parameter '" + parameterValue + "'");
			return null;
		}
	}

	protected String getParameterValueAsString(String annotationOrTag, String parameterValue) {
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

	/**
	 * Remove quote char at the first and last position if any
	 * 
	 * @param s
	 * @param quoteChar
	 * @return
	 */
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

}