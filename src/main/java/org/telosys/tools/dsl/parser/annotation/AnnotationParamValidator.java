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

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;

public class AnnotationParamValidator {

	private final String entityName;
	private final String fieldName;

	public AnnotationParamValidator(String entityName, String fieldName) {
		super();
		this.entityName = entityName;
		this.fieldName = fieldName;
	}

	public void checkSize(DomainAnnotation annotation) throws AnnotationOrTagError {
		String annotationName = annotation.getName();
		String annotationParam = annotation.getParameterAsString();
		if ( annotationParam != null ) {
			checkSizeParameter(annotation.getName(), annotationParam);
		}
		else {
			// not supposed to happen
			throw new AnnotationOrTagError(entityName, fieldName, annotationName, "parameter is null");
		}
	}
	
	private void checkSizeParameter(String annotationName, String p) throws AnnotationOrTagError {
		if ( p.contains(",")) {
			String[] parts = p.split(",");
			if (parts.length  != 2) {
				throw new AnnotationOrTagError(entityName, fieldName, annotationName, "invalid parameter '" + p + "'");
			}
			checkSizeInteger(annotationName, parts[0]);
			checkSizeInteger(annotationName, parts[1]);
		}
		else {
			checkSizeInteger(annotationName, p);
		}
	}
	
	private void checkSizeInteger(String annotationName, String parameterValue) throws AnnotationOrTagError {
		try {
			Integer i = new Integer(parameterValue);
			if ( i < 0 ) {
				throw new AnnotationOrTagError(entityName, fieldName, annotationName, "negative size '" + parameterValue + "'");
			}
		} catch (NumberFormatException e) {
			throw new AnnotationOrTagError(entityName, fieldName, annotationName, "invalid size '" + parameterValue + "'");
		}
	}

}
