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

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;

public class FieldAnnotationsAndTagsParser {

	private final String entityNameFromFileName;

	public FieldAnnotationsAndTagsParser(String entityNameFromFileName) {
		this.entityNameFromFileName = entityNameFromFileName;
	}
	
	public FieldAnnotationsAndTags parse(String fieldName, FieldParts field) throws AnnotationOrTagError {
		return parse( fieldName, field.getAnnotationsPart());
	}

	public FieldAnnotationsAndTags parse(String fieldName, String annotationsAndTags) throws AnnotationOrTagError {
		FieldAnnotationsAndTags result = new FieldAnnotationsAndTags();

    	if ( annotationsAndTags == null || "".equals(annotationsAndTags) ) {
    		return result; // void
    	}
    	FieldAnnotationsAndTagsSplitter splitter = new FieldAnnotationsAndTagsSplitter(entityNameFromFileName, fieldName);
		List<String> elements = splitter.split(annotationsAndTags);
		for ( String element : elements ) {
			ParserLogger.log(" . '" + element + "'");
			FieldAnnotationOrTagParser annotationOrTagParser = new FieldAnnotationOrTagParser(entityNameFromFileName, fieldName);
			DomainAnnotationOrTag annotationOrTag;
			try {
				annotationOrTag = annotationOrTagParser.parse(element);
				result.addAnnotationOrTag(annotationOrTag);
			} catch (AnnotationOrTagError e) {
				// invalid annotation or tag 
				result.addError(e);
			}
		}
		return result;
	}

}
