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
package org.telosys.tools.dsl.parser2;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.parser.AnnotationParser;
import org.telosys.tools.dsl.parser.ParserLogger;
import org.telosys.tools.dsl.parser.TagParser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.exceptions.ParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainTag;

/**
 * Telosys DSL entity parser
 *
 * @author Laurent GUERIN
 * 
 */
public class EntityElementsProcessor {

	private final String entityName;
	private boolean entityNameChecked = false ;

	private final FieldElementsProcessor fieldElementsProcessor ;
	
	/**
	 * Constructor
	 * @param entityName
	 * @param entitiesNames
	 */
	public EntityElementsProcessor(String entityName, List<String> entitiesNames) {
		super();
		this.entityName = entityName;
		this.fieldElementsProcessor = new FieldElementsProcessor(entityName, entitiesNames);
	}

	/**
	 * Parse entity defined in the current file
	 * @return
	 * @throws EntityParsingError
	 */
	public DomainEntity processEntityElements(List<Element> elements, ParsingErrors errors) { //throws ParsingError {
		List<Element> fieldElements = null ;
		boolean inFields = false ;
		DomainEntity domainEntity = new DomainEntity(entityName);
		for ( Element element : elements ) {
			if ( ! inFields ) {
				if ( element.equals("{") ) {
					// Fields start here
					inFields = true;
				}
				else {
					// ENTITY LEVEL ( NOT IN FIELDS )
					processElementAtEntityLevel(domainEntity, element, errors);
				}
//				r = processElementAtEntityLevel(domainEntity, element);
//				if ( r == OPENING_BRACE_IN_ENTITY ) {
//					inFields = true;
//				}
			}
			else {
				if ( element.equals("}") && fieldElements == null ) {
					// Closing brace and not in a field definition => End of fields
					inFields = false;
				}
				else {
					// FIELDS LEVEL 
					if ( fieldElements == null ) {
						fieldElements = new LinkedList<>();
					}
					if ( element.equals(";") ) {
						// End of field definition => process this field
						DomainField field = fieldElementsProcessor.processFieldElements(fieldElements, errors);
						if ( field != null ) {
							// Add the field in the current entity
							domainEntity.addField(field);
						}
						// Reset field elements
						fieldElements = null ;  // to start a new list of field elements
					}
					else {
						fieldElements.add(element);
					}
				}
			}
		}
		return domainEntity;
	}
	
	private void processElementAtEntityLevel(DomainEntity domainEntity, Element element, ParsingErrors errors) { //throws ParsingError {
		if ( element.startsWithAnnotationPrefix() ) {
			AnnotationParser annotationParser = new AnnotationParser(entityName);
			DomainAnnotation annotation;
			try {
				annotation = annotationParser.parseAnnotation(element.getContent());
				domainEntity.addAnnotation(annotation);
			} catch (ParsingError e) {
				errors.addError(e);
			}
		}
		else if ( element.startsWithTagPrefix() ) {
			TagParser tagParser = new TagParser(entityName);
			DomainTag tag;
			try {
				tag = tagParser.parseTag(element.getContent());
				domainEntity.addTag(tag);
			} catch (ParsingError e) {
				errors.addError(e);
			}
		}
		else {
			// Supposed to be the entity name 
			if ( ! entityNameChecked ) {
				if ( element.equals(entityName) ) {
					entityNameChecked = true ;
				}
				else {
					// ERROR : invalid entity name
//					throw new EntityParsingError(entityName, 
//							"Entity name '" + element.getContent()
//							+ "' different from file name '" + entityName +"' ");
					errors.addError( new EntityParsingError(entityName, 
							"Entity name '" + element.getContent()
							+ "' different from file name '" + entityName +"' "));
				}
			}
			else {
				// ERROR : unexpected element 
//				throw new EntityParsingError(entityName, 
//						"unexpected element '" + element.getContent()+"' ");
				errors.addError( new EntityParsingError(entityName, 
						"unexpected element '" + element.getContent()+"' "));
			}
		}
	}
	
}
