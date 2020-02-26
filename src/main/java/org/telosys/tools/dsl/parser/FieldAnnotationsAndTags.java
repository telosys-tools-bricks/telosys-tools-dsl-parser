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

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.exceptions.AnnotationOrTagError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainAnnotationOrTag;
import org.telosys.tools.dsl.parser.model.DomainTag;

public class FieldAnnotationsAndTags {

	private final List<DomainAnnotation>  annotations ;
	
	private final List<DomainTag>  tags ;
	
	private final List<AnnotationOrTagError>  errors ;
	
	
	public FieldAnnotationsAndTags() {
		super();
		this.annotations = new LinkedList<>();
		this.tags = new LinkedList<>();
		this.errors = new LinkedList<>();
	}

	public void addAnnotationOrTag(DomainAnnotationOrTag annotationOrTag) {
		if ( annotationOrTag instanceof DomainAnnotation) {
			annotations.add((DomainAnnotation)annotationOrTag);
		}
		else if ( annotationOrTag instanceof DomainTag) {
			tags.add((DomainTag)annotationOrTag);
		}
	}
	
	protected void addError(AnnotationOrTagError exception) {
		errors.add(exception);
	}
	
	public List<DomainAnnotation> getAnnotations() {
		return annotations;
	}
	
	public List<DomainTag> getTags() {
		return tags;
	}
	
	public List<AnnotationOrTagError> getErrors() {
		return errors;
	}
}
