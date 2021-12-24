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

import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.FieldParsingError;
//import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainTag;

public class FieldAnnotationsAndTagsParser {

	private final String entityName;

	/**
	 * Constructor
	 * @param entityNameFromFileName
	 */
	public FieldAnnotationsAndTagsParser(String entityNameFromFileName) {
		this.entityName = entityNameFromFileName;
	}
	
	/**
	 * Parse the annotations and tags of the given FieldParts 
	 * @param fieldName
	 * @param field
	 * @return
	 * @throws ParsingError
	 */
	public FieldAnnotationsAndTags parse(String fieldName, FieldParts field) throws ParsingError { // AnnotationOrTagError {
		return parse( fieldName, field.getAnnotationsPart());
	}

	/**
	 * @param fieldName
	 * @param annotationsAndTags
	 * @return
	 * @throws ParsingError
	 */
	private FieldAnnotationsAndTags parse(String fieldName, String annotationsAndTags) throws ParsingError { // AnnotationOrTagError {
		FieldAnnotationsAndTags result = new FieldAnnotationsAndTags();

    	if ( annotationsAndTags == null || "".equals(annotationsAndTags) ) {
    		return result; // void
    	}
    	FieldAnnotationsAndTagsSplitter splitter = new FieldAnnotationsAndTagsSplitter(entityName, fieldName);
		List<String> elements = splitter.split(annotationsAndTags);
		for ( String element : elements ) {
			ParserLogger.log(" . '" + element + "'");
//			DomainAnnotationOrTag annotationOrTag;
			try {
//				annotationOrTag = parseAnnotationOrTag(fieldName, element);
//				result.addAnnotationOrTag(annotationOrTag);
				parseAnnotationOrTag(fieldName, element, result);
			} catch (ParsingError e) {
				// invalid annotation or tag 
				result.addError(e);
			}
		}
		return result;
	}

	//--------------------------------------------------------------------------------------------------
	// moved from FieldAnnotationOrTagParser
	//--------------------------------------------------------------------------------------------------
	private void parseAnnotationOrTag(String fieldName, String annotationOrTagString, FieldAnnotationsAndTags result) throws ParsingError { //AnnotationOrTagError {
		char firstChar = annotationOrTagString.charAt(0);
		if (firstChar == '@') {
			// TAG
			AnnotationParser annotationParser = new AnnotationParser(entityName, fieldName);
			DomainAnnotation annotation = annotationParser.parseAnnotation(annotationOrTagString);
			result.addAnnotation(annotation);
		} else if (firstChar == '#') {
			// ANNOTATION
			TagParser tagParser = new TagParser(entityName, fieldName);
			DomainTag tag = tagParser.parseTag(annotationOrTagString);
			result.addTag(tag);
		} else {
			throw new FieldParsingError(entityName, fieldName, 
					"invalid element '" + annotationOrTagString + "' must start with '@' or '#'");
		}
	}
	
}
