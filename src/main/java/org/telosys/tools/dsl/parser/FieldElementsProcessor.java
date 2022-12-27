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

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainTag;
import org.telosys.tools.dsl.parser.model.DomainType;

/**
 * Telosys DSL field elements processor
 *
 * @author Laurent GUERIN
 * 
 */
public class FieldElementsProcessor {
	
	private final String entityName;
	private final List<String> entitiesNamesInModel;

	/**
	 * Constructor
	 * @param entityName
	 * @param entitiesNames
	 */
	public FieldElementsProcessor(String entityName, List<String> entitiesNames) {
		super();
		this.entityName = entityName;
		this.entitiesNamesInModel = entitiesNames;		
	}

	/**
	 * Process the given field elements
	 * @param elements
	 * @param errors
	 * @return the new field (or null if it's impossible to create a new field)
	 */
	public DomainField processFieldElements(List<Element> elements, DslModelErrors errors) {
		
		// Build field with NAME and TYPE
		DomainField field;
		try {
			field = buildField(elements);
		} catch (DslModelError e) {
			errors.addError(e);
			return null;
		}
		
		// Extract additional elements for annotations and tags
		List<Element> additionalElements;
		try {
			additionalElements = extractAdditionalElements(field.getName(), elements);
		} catch (DslModelError e) {
			errors.addError(e);
			return field;
		}
		
		// Add annotations and tags if any
		for ( Element element : additionalElements ) {
			try {
				processAnnotationOrTag(field, element) ;
			} catch (DslModelError e) {
				errors.addError(e);
			}
		}
		return field;
	}
	
	private DomainField buildField(List<Element> elements) throws DslModelError {
		if ( elements.size() >= 3 ) {
			Element fieldNameElement = elements.get(0);
			Element separatorElement = elements.get(1);
			Element fieldTypeElement = elements.get(2);
			// Get field name, type and cardinality
			String fieldName = parseFieldName(fieldNameElement);
			parseSeparator(fieldName, separatorElement);
			DomainType fieldType = parseFieldType(fieldName, fieldTypeElement);
			// Build field
			return new DomainField(fieldNameElement.getLineNumber(), fieldName, fieldType);
		}
		else {
			// ERROR
			String fieldName = "" ;
			if ( ! elements.isEmpty() ) {
				fieldName = elements.get(0).getContent();
			}
			throw new DslModelError(entityName, 0, fieldName, "invalid field definition");
		}
	}
	
	private String parseFieldName(Element element) throws DslModelError {
		String fieldName = element.getContent();
		for ( char c : fieldName.toCharArray() ) {
			if ( ! ( Character.isLetterOrDigit(c) || c == '_') ) {
				throw new DslModelError(entityName, element.getLineNumber(), fieldName, "invalid field name (char '" + c + "')");
			}
		}
		return fieldName; // Field name is OK 
	}
	
	private void parseSeparator(String fieldName, Element element) throws DslModelError {
		String s = element.getContent();
		if ( ! ":".equals(s) ) {
			throw new DslModelError(entityName, element.getLineNumber(), fieldName, "invalid separator '" + s + "' (':' expected)");
		}
	}
	
	private DomainType parseFieldType(String fieldName, Element typeElement) throws DslModelError {
		String type = typeElement.getContent();
		DomainType neutralType = DomainNeutralTypes.getType(type);
		if ( neutralType != null ) {
			// Found in neutral type => use it 
			return neutralType ;
		}
		else {
			// It must be an entity type (with or without "[]")
			String typeWithoutBrackets = removeBracketsIfAny(fieldName, typeElement);
			if (entitiesNamesInModel.contains(typeWithoutBrackets) ) {
				// This entity exists => use it 
				DomainCardinality cardinality = parseFieldCardinality(typeElement);
				return new DomainEntityType(typeWithoutBrackets, cardinality); 
			}
		}
		// In all other cases : unknown type 
		throw new DslModelError(entityName, typeElement.getLineNumber(), fieldName, "invalid type '" + type + "'");
	}
	
	private DomainCardinality parseFieldCardinality(Element typeElement) {
		String type = typeElement.getContent();
		if ( type.endsWith("[]") ) {
			return DomainCardinality.MANY;
		}
		else {
			return DomainCardinality.ONE;
		}
	}
	
	protected String removeBracketsIfAny(String fieldName, Element typeElement) throws DslModelError {
		String type = typeElement.getContent();
		int count1 = 0 ;
		int count2 = 0 ;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < type.length(); i++) {
            char c = type.charAt(i);
            if ( c == '[' ) {
            	count1++;
            }
            else if ( c == ']') {
            	count2++;
            }
            else {
            	sb.append(c);
            }
        }
		if (       ( count1 != count2 ) 
				|| ( count1 == 1 && count2 == 1 && ( ! type.endsWith("[]") ) ) ) {
			// Error
			throw new DslModelError(entityName, typeElement.getLineNumber(), fieldName, "invalid field type : " + type);
		}
		return sb.toString();
	}
	
	protected List<Element> extractAdditionalElements(String fieldName, List<Element> elements) throws DslModelError {
		List<Element> selection = new LinkedList<>();
		
		boolean inAnnotationsAndTags = false;
		int openingBracePosition = 0 ; 
		int closingBracePosition = 0 ;
		int position = 0;
		for ( Element element : elements ) {
			position++;
			if ( position > 3 ) { // Skip "field name", ":" and "field type"
				if ( element.contentEquals("{") ) {
					if ( openingBracePosition != 0 ) {
						throw new DslModelError(entityName, element.getLineNumber(), fieldName, "multiple '{' ");
					}
					inAnnotationsAndTags = true ;
					openingBracePosition = position ;
				}
				else if ( element.contentEquals("}") ) {
					if ( closingBracePosition != 0 ) {
						throw new DslModelError(entityName, element.getLineNumber(), fieldName, "multiple '}' ");
					}
					inAnnotationsAndTags = false ;
					closingBracePosition = position ;
				}
				else {
					if ( inAnnotationsAndTags ) {
						selection.add(element);
					}
					else {
						// ERROR
						throw new DslModelError(entityName, element.getLineNumber(), fieldName, 
								"unexpected element '" + element.getContent() + "' out of {...}");
					}
				}
			}
		}
		return selection;
	}
	
	private void processAnnotationOrTag(DomainField field, Element element) throws DslModelError {
		if ( element.startsWithAnnotationPrefix() ) {
			// @Xxxx : Annotation			
			AnnotationProcessor annotationProcessor = new AnnotationProcessor(entityName, field);
			DomainAnnotation annotation = annotationProcessor.parseAnnotation(element);
			// no annotation error => continue
			if ( AnnotationName.FK.equals( annotation.getName() ) ) {
				// Special storage for "@FK" annotation (can be used 1..N times in a field )
				field.addFkElement(annotation.getParameterAsFKElement());				
			}
			else {
				// Standard storage for other annotations (usable onmy once)
				field.addAnnotation(annotation);
			}
		}
		else if ( element.startsWithTagPrefix() ) {
			// #Xxxx : Tag
			TagProcessor tagProcessor = new TagProcessor(entityName, field.getName());
			DomainTag tag = tagProcessor.parseTag(element);
			field.addTag(tag);
		}
		else {
			// ERROR
			throw new DslModelError(entityName, element.getLineNumber(), field.getName(), 
					"invalid element '" + element.getContent() + "'" + "(annotation or tag expected)");
		}
	}
	
}
