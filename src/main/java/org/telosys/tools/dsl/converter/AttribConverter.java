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
package org.telosys.tools.dsl.converter;

import java.util.Collection;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.annotation.AnnotationApplicator;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainType;

/**
 * Utility class to convert the "raw model" (built by DSL parser) to the standard "generic model"
 * 
 * @author L. Guerin
 *
 */
public class AttribConverter extends AbstractConverter {

	private final AnnotationApplicator annotationApplicator ;

	private final TagsConverter tagsConverter;
	
	/**
	 * Constructor
	 * @param model
	 */
	public AttribConverter(DslModel model) {
		super();
		this.annotationApplicator = new AnnotationApplicator(model);
		this.tagsConverter = new TagsConverter();
	}

	/**
	 * Converts all attributes for the given entity
	 * 
	 * @param domainEntity source of information 
	 * @param dslEntity entity to be popultated
	 */
	public void convertAttributes(DomainEntity domainEntity, DslModelEntity dslEntity) {
		log("convertEntityAttributes()...");
		if (domainEntity.getFields() == null) {
			return;
		}
		for (DomainField domainEntityField : domainEntity.getFields()) {

			// Init the new attribute with at least its name
			DslModelAttribute dslAttribute = new DslModelAttribute();
			dslAttribute.setName(notNull(domainEntityField.getName()));

			DomainType domainFieldType = domainEntityField.getType();
			if (domainFieldType.isNeutralType()) {
				// STANDARD NEUTRAL TYPE = BASIC ATTRIBUTE
				log("convertEntityAttributes() : " + domainEntityField.getName() + " : neutral type");
				// Simple type attribute
//				convertAttributeNeutralType(domainEntity, domainEntityField, genericAttribute);
				populateAttribute(dslEntity, dslAttribute, domainEntityField );
				// Add the new "basic attribute" to the entity
				dslEntity.getAttributes().add(dslAttribute);
			}
		}
	}

	/**
	 * Converts a basic "neutral type" attribute <br>
	 * eg : id : short {@Id}; <br>
	 * @param domainEntity
	 * @param domainField
	 * @param dslAttribute
	 */
//	private void convertAttributeNeutralType(DomainEntity domainEntity, 
//			DomainField domainEntityField, DslModelAttribute genericAttribute) {
//	private void convertAttributeNeutralType(DomainField domainEntityField, 
//			DslModelEntity dslEntity, DslModelAttribute dslAttribute) {
	private void populateAttribute(DslModelEntity dslEntity, DslModelAttribute dslAttribute, 
			DomainField domainField ) {	
		log("convertAttributeNeutralType() : name = " + domainField.getName());

		DomainType domainFieldType = domainField.getType();
		check(domainFieldType.isNeutralType(), "Invalid field type. Neutral type expected");
		DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;

		// the "neutral type" is now the only type managed at this level
		dslAttribute.setNeutralType(domainNeutralType.getName());

		initAttributeDefaultValues(dslAttribute, domainField);

		// Apply annotations if any
		if (domainField.getAnnotations() != null) {
			log("Converter : annotations found");
			Collection<DomainAnnotation> fieldAnnotations = domainField.getAnnotations().values();
		/***
			AttribAnnotationsProcessor annotationsConverter = new AttribAnnotationsProcessor(domainEntity.getName());
			annotationsConverter.applyAnnotationsForNeutralType(genericAttribute, fieldAnnotations);
		***/
			annotationApplicator.applyAnnotationsToField(dslEntity, dslAttribute, fieldAnnotations);
		} else {
			log("Converter : no annotation");
		}
		
		// Apply tags if any
		tagsConverter.applyTags(dslAttribute, domainField);
		
		// Finalize attribute state
		finalizeAttribute(dslAttribute);
	}

	/**
	 * Initializes default values according with the given attribute
	 * @param dslAttribute
	 * @param domainField
	 */
	private void initAttributeDefaultValues(DslModelAttribute dslAttribute, DomainField domainField) {

		// All the default attribute values are set in the attribute class
		// Here some default values can be set depending on other attribute information 
		
		// By default the database name is the attribute name 
		// it will be overridden by @DbName(xxx) if any
		dslAttribute.setDatabaseName(domainField.getName()); 

		// By default the label is the attribute name 
		// it will be overridden by @Label(xxx) if any
		dslAttribute.setLabel(domainField.getName());
	}
	
	private void finalizeAttribute(DslModelAttribute dslAttribute ) {	
		if ( dslAttribute.getDatabaseSize() == null && dslAttribute.getMaxLength() != null ) {
			dslAttribute.setDatabaseSize(dslAttribute.getMaxLength().toString());
		}
	}
}
