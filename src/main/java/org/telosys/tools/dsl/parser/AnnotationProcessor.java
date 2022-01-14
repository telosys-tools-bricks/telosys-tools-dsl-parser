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

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinition;
import org.telosys.tools.dsl.parser.annotation.AnnotationDefinitions;
import org.telosys.tools.dsl.parser.annotation.AnnotationParamType;
import org.telosys.tools.dsl.parser.commons.ParamError;
import org.telosys.tools.dsl.parser.commons.ParamValue;
import org.telosys.tools.dsl.parser.commons.ParamValueOrigin;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;

/**
 * Annotation parsing 
 * 
 * @author Laurent GUERIN
 */
public class AnnotationProcessor extends AnnotationAndTagProcessor {

	private final DomainEntity entity; // to work at ENTITY level
	private final DomainField field; // to work at FIELD level
	
//	/**
//	 * Constructor for parsing at ENTITY level
//	 * 
//	 * @param entityName
//	 */
	public AnnotationProcessor(DomainEntity entity) {
		super(entity.getName());
		this.entity = entity;
		this.field = null ;
	}
	
//	/**
//	 * Constructor for parsing at FIELD level
//	 * 
//	 * @param entityName
//	 * @param fieldName
//	 */
//	public AnnotationProcessor(String entityName, String fieldName) {
//		super(entityName, fieldName);
//	}
	
	public AnnotationProcessor(String entityName, DomainField field) {
		super(entityName, field.getName());
		this.entity = null;
		this.field = field ;
	}
	
	/**
	 * Parse the given annotation element ( a string starting by '@' ) 
	 * @param element element with the full annotation string eg "@Id", "@Min(12)"
	 * @return
	 * @throws DslModelError
	 */
	public DomainAnnotation parseAnnotation(Element element) throws DslModelError {
		
//		// get the name 
//		String annotationName = getName(element);
//
//		// get the raw parameter value if any
//		String annotationParameter = getParameterValue(element);
//
//		// use annotation definition to build a new annotation instance
//		AnnotationDefinition ad = AnnotationDefinitions.get(annotationName);
//		if ( ad != null ) {
//			try {
//				DomainAnnotation annotation = buildAnnotation(annotationName, annotationParameter, ad.getParamType());
//				ad.afterCreation(getEntityName(), getFieldName(), annotation);
//				return annotation;
//			} catch (ParamError e) {
//				throw newError(element.getLineNumber(), "'" + element.getContent() + "' : " + e.getMessage() );
//			} 
//		}
//		else {
//			throw newError(element.getLineNumber(), "'" + element.getContent() + "' : unknown annotation");
//		}
		DomainAnnotation annotation = buildAnnotation(element);
		checkAnnotationScope(element, annotation);
		checkAnnotationSingleUse(element, annotation);
		return annotation;
	}
	
	protected DomainAnnotation buildAnnotation(Element element) throws DslModelError {
		// get the name 
		String annotationName = getName(element);

		// get the raw parameter value if any
		String annotationParameter = getParameterValue(element);

		// use annotation definition to build a new annotation instance
		AnnotationDefinition ad = AnnotationDefinitions.get(annotationName);
		if ( ad != null ) {
			try {
				DomainAnnotation annotation = buildAnnotation(annotationName, annotationParameter, ad.getParamType());
				ad.afterCreation(getEntityName(), getFieldName(), annotation);
				return annotation;
			} catch (ParamError e) {
//				throw newError(element.getLineNumber(), "'" + element.getContent() + "' : " + e.getMessage() );
				throw newError(element, e.getMessage());
			} 
		}
		else {
//			throw newError(element.getLineNumber(), "'" + element.getContent() + "' : unknown annotation");
			throw newError(element, "unknown annotation");
		}
	}
	
	protected DomainAnnotation buildAnnotation(String annotationName, String annotationParameter, 
			AnnotationParamType paramType) throws ParamError {
		
		// Build param value 
		ParamValue paramValue = new ParamValue(getEntityName(), getFieldName(), 
				annotationName, annotationParameter,
				ParamValueOrigin.FIELD_ANNOTATION);

		switch(paramType) {
		case STRING :
			return new DomainAnnotation(annotationName, paramValue.getAsString() );
		case INTEGER :
			return new DomainAnnotation(annotationName, paramValue.getAsInteger() );
		case DECIMAL :
			return new DomainAnnotation(annotationName, paramValue.getAsBigDecimal() );
		case BOOLEAN :
			return new DomainAnnotation(annotationName, paramValue.getAsBoolean() );
		case SIZE :
			return new DomainAnnotation(annotationName, paramValue.getAsSize() );
		case LIST :
			return new DomainAnnotation(annotationName, paramValue.getAsList() );
		case FK_ELEMENT :
			return new DomainAnnotation(annotationName, paramValue.getAsForeignKeyElement() );

		default :
			// annotation without parameter
			if (annotationParameter != null) {
				throw new ParamError("unexpected parameter '" + annotationParameter + "'");
			}
			return new DomainAnnotation(annotationName);
		}
		
	}
	
	private void checkAnnotationScope(Element element, DomainAnnotation annotation) throws DslModelError {
		if ( this.field != null ) {
			checkAnnotationScopeForField(element, field, annotation);
		}
		else if ( this.entity != null ) {
			checkAnnotationScopeForEntity(element, annotation);
		}		
	}
	private void checkAnnotationScopeForField(Element element, DomainField field, DomainAnnotation annotation) throws DslModelError {
		AnnotationDefinition ad = annotation.getAnnotationDefinition();
		if ( field.isAttribute() ) {
			if ( ! ad.hasAttributeScope() ) {
//				throw newError(element.getLineNumber(), "'" + element.getContent() 
//					+ "' : annotation not usable in an attribute (invalid scope)");
				throw newError(element, "annotation not usable in an attribute level (invalid scope)");
			}
		}
		else if ( field.isLink() ) {
			if ( ! ad.hasLinkScope() ) {
//				throw newError(element.getLineNumber(), "'" + element.getContent() 
//					+ "' : annotation not usable in a link (invalid scope)");
				throw newError(element, "annotation not usable in a link level (invalid scope)");
			}
		}
	}
	private void checkAnnotationScopeForEntity(Element element, DomainAnnotation annotation) throws DslModelError {
		AnnotationDefinition ad = annotation.getAnnotationDefinition();
		if ( ! ad.hasEntityScope() ) {
			throw newError(element, "annotation not usable at entity level (invalid scope)");
		}
	}

	private void checkAnnotationSingleUse(Element element, DomainAnnotation annotation) throws DslModelError {
		if ( annotation.canBeUsedMultipleTimes() ) {
			return ; // No checking
		}
		else {
			if ( this.field != null ) {
				// Check at FIELD level
				if ( field.hasAnnotation(annotation) ) {
					throw newError(element, "annotation used more than once");
				}
			}
			else  if ( this.entity != null ) {
				// Check at ENTITY level
				if ( entity.hasAnnotation(annotation) ) {
					throw newError(element, "annotation used more than once");
				}
			}		
		}
	}
	
////	public void checkAnnotationSingleUse(Element element, DomainField field, DomainAnnotation annotation) throws DslModelError {
//	public void checkAnnotationSingleUseForField(Element element, DomainAnnotation annotation) throws DslModelError {
//		if ( annotation.canBeUsedMultipleTimes() ) {
//			return ; // No checking
//		}
//		if ( field.hasAnnotation(annotation) ) {
////			throw newError(element.getLineNumber(), "'" + element.getContent() 
////			+ "' : annotation used more than once");
//			throw newError(element, "annotation used more than once");
//		}
//	}

	private DslModelError newError(Element element, String shortMessage) {
		String longMessage = "'" + element.getContent() + "' : " + shortMessage ;
		return newError(element.getLineNumber(), longMessage);
	}
	
}