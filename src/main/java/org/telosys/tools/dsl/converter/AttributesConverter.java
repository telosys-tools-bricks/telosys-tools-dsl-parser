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
public class AttributesConverter extends AbstractConverter {

	private final AnnotationsApplicator annotationsApplicator ;

	private final TagsConverter tagsConverter;
	
	/**
	 * Constructor
	 * @param model
	 */
	public AttributesConverter(DslModel model) {
		super();
		this.annotationsApplicator = new AnnotationsApplicator(model);
		this.tagsConverter = new TagsConverter();
	}

	/**
	 * Converts all attributes for the given entity
	 * 
	 * @param domainEntity source of information 
	 * @param dslEntity entity to be popultated
	 */
	public void convertAttributes(DomainEntity domainEntity, DslModelEntity dslEntity) {
		log("convertAttributes()...");
		if (domainEntity.getFields() == null) {
			return;
		}
		for (DomainField domainField : domainEntity.getFields()) {
			// If the field is a "neutral type" = "basic attribute"
			if (domainField.getType().isNeutralType()) { 
				log("convert field : " + domainField.getName() + " (neutral type => basic attribute)");
				// New "basic attribute"
//				DslModelAttribute dslAttribute = createAttribute(domainField);
//				// Populate attribute with parsed attribute information
//				populateAttribute(dslEntity, dslAttribute, domainField );
				
				DslModelAttribute dslAttribute = convertAttribute(domainField, dslEntity);
				// Add the new "basic attribute" to the entity
				//dslEntity.getAttributes().add(dslAttribute);
				dslEntity.addAttribute(dslAttribute);  // v 3.4.0
			}
		}
	}
	
	protected DslModelAttribute convertAttribute(DomainField domainField, DslModelEntity dslEntity) { // v 3.4.0
		String attributeName = notNull(domainField.getName());
		String attributeType = ((DomainNeutralType) domainField.getType()).getName();
		
		// New attribute 
		DslModelAttribute dslAttribute = new DslModelAttribute(attributeName, attributeType);
		
		// Init attribute state
		step1InitAttributeDefaultValues(dslAttribute, domainField);

		// Apply annotations if any
		step2ApplyAnnotations(dslEntity, dslAttribute, domainField);
		
		// Apply tags if any
		step3ApplyTags(dslAttribute, domainField);
		
		// Finalize attribute state
		step4FinalizeAttribute(dslAttribute);
		
		return dslAttribute;
	}
	
//	private DslModelAttribute createAttribute( DomainField domainField ) {	
////		DslModelAttribute dslAttribute = new DslModelAttribute();
////		// Init the new attribute with at least its name
////		dslAttribute.setName(notNull(domainField.getName()));
////		return dslAttribute;
//		return new DslModelAttribute(notNull(domainField.getName())); // v 3.4.0
//	}
	
//	/**
//	 * Converts a basic "neutral type" attribute <br>
//	 * eg : id : short {@Id}; <br>
//	 * @param domainEntity
//	 * @param domainField
//	 * @param dslAttribute
//	 */
////	private void convertAttributeNeutralType(DomainEntity domainEntity, 
////			DomainField domainEntityField, DslModelAttribute genericAttribute) {
////	private void convertAttributeNeutralType(DomainField domainEntityField, 
////			DslModelEntity dslEntity, DslModelAttribute dslAttribute) {
//	private void populateAttribute(DslModelEntity dslEntity, DslModelAttribute dslAttribute, 
//			DomainField domainField ) {	
//		log("convertAttributeNeutralType() : name = " + domainField.getName());
//
//		DomainType domainFieldType = domainField.getType();
//		check(domainFieldType.isNeutralType(), "Invalid field type. Neutral type expected");
//		DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;
//
//		// the "neutral type" is now the only type managed at this level
//		dslAttribute.setNeutralType(domainNeutralType.getName());
//
//		step1InitAttributeDefaultValues(dslAttribute, domainField);
//
//		// Apply annotations if any
//		step2ApplyAnnotations(dslEntity, dslAttribute, domainField);
//		
//		// Apply tags if any
//		step3ApplyTags(dslAttribute, domainField);
//		
//		// Finalize attribute state
//		step4FinalizeAttribute(dslAttribute);
//	}

	/**
	 * Initializes default values according with the given attribute
	 * @param dslAttribute
	 * @param domainField
	 */
	private void step1InitAttributeDefaultValues(DslModelAttribute dslAttribute, DomainField domainField) {

		// All the default attribute values are set in the attribute class
		// Here some default values can be set depending on other attribute information 
		
		// By default the database name is the attribute name 
		// it will be overridden by @DbName(xxx) if any
		dslAttribute.setDatabaseName(domainField.getName()); 

		// By default the label is the attribute name 
		// it will be overridden by @Label(xxx) if any
		dslAttribute.setLabel(domainField.getName());
	}
	
	/**
	 * Apply annotations to the given attribute
	 * @param dslEntity
	 * @param dslAttribute
	 * @param domainField
	 */
	private void step2ApplyAnnotations(DslModelEntity dslEntity, DslModelAttribute dslAttribute, DomainField domainField) {
		if (domainField.getAnnotations() != null) {
			log("Converter : annotations found");
			Collection<DomainAnnotation> fieldAnnotations = domainField.getAnnotations().values();
		/***
			AttribAnnotationsProcessor annotationsConverter = new AttribAnnotationsProcessor(domainEntity.getName());
			annotationsConverter.applyAnnotationsForNeutralType(genericAttribute, fieldAnnotations);
		***/
			annotationsApplicator.applyAnnotationsToAttribute(dslEntity, dslAttribute, fieldAnnotations);
		} else {
			log("Converter : no annotation");
		}
	}
	
	/**
	 * Apply tags to the given attribute
	 * @param dslAttribute
	 * @param domainField
	 */
	private void step3ApplyTags(DslModelAttribute dslAttribute, DomainField domainField) {
		tagsConverter.applyTags(dslAttribute, domainField);
	}
	
	/**
	 * Finalize the given attribute
	 * @param dslAttribute
	 */
	private void step4FinalizeAttribute(DslModelAttribute dslAttribute ) {	
		// If attribute is KEY ELEMENT ( @Id ) => NOT NULL
		if ( dslAttribute.isKeyElement() ) {
			dslAttribute.setNotNull(true);
		}
		// If attribute is NOT NULL ( @NotNull ) => Database NOT NULL
		if ( dslAttribute.isNotNull() ) {
			dslAttribute.setDatabaseNotNull(true);
		}
		// If database size is not defined use max length if any 
		if ( dslAttribute.getDatabaseSize() == null && dslAttribute.getMaxLength() != null ) {
			dslAttribute.setDatabaseSize(dslAttribute.getMaxLength().toString());
		}
	}
}
