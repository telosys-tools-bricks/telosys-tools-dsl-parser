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
public class AttribConverter extends AbstractConverter {

	private final AttribAnnotationsProcessor annotationsConverter = new AttribAnnotationsProcessor();
	
	private final TagsConverter tagsConverter = new TagsConverter();
	
	/**
	 * Constructor
	 */
	public AttribConverter() {
		super();
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
			DslModelAttribute genericAttribute = new DslModelAttribute();
			genericAttribute.setName(notNull(domainEntityField.getName()));

			DomainType domainFieldType = domainEntityField.getType();
			if (domainFieldType.isNeutralType()) {
				// STANDARD NEUTRAL TYPE = BASIC ATTRIBUTE
				log("convertEntityAttributes() : " + domainEntityField.getName() + " : neutral type");
				// Simple type attribute
				convertAttributeNeutralType(domainEntityField, genericAttribute);
				// Add the new "basic attribute" to the entity
				dslEntity.getAttributes().add(genericAttribute);
			}
		}
	}

	/**
	 * Converts a basic "neutral type" attribute <br>
	 * eg : id : short {@Id}; <br>
	 * 
	 * @param domainEntityField
	 * @param genericAttribute
	 */
	private void convertAttributeNeutralType(DomainField domainEntityField, DslModelAttribute genericAttribute) {
		log("convertAttributeNeutralType() : name = " + domainEntityField.getName());

		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isNeutralType(), "Invalid field type. Neutral type expected");
		DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;

		// the "neutral type" is now the only type managed at this level
		genericAttribute.setNeutralType(domainNeutralType.getName());

		initAttributeDefaultValues(genericAttribute, domainEntityField);

		// Apply annotations if any
		if (domainEntityField.getAnnotations() != null) {
			log("Converter : annotations found");
			Collection<DomainAnnotation> fieldAnnotations = domainEntityField.getAnnotations().values();
			annotationsConverter.applyAnnotationsForNeutralType(genericAttribute, fieldAnnotations);
		} else {
			log("Converter : no annotation");
		}
		
		// Apply tags if any
		tagsConverter.applyTags(genericAttribute, domainEntityField);
	}

	/**
	 * Initializes default values according with the given attribute
	 * @param genericAttribute
	 * @param domainEntityField
	 */
	private void initAttributeDefaultValues(DslModelAttribute genericAttribute, DomainField domainEntityField) {

		// All the default attribute values are set in the attribute class
		// Here some default values can are set depending on other attribute information 
		
		// By default the database name is the attribute name 
		// it will be overridden by @DbName(xxx) if any
		genericAttribute.setDatabaseName(domainEntityField.getName()); 

		// By default the label is the attribute name 
		// it will be overridden by @Label(xxx) if any
		genericAttribute.setLabel(domainEntityField.getName());
		
		// genericAttribute.setSelected(true); // allready set in attribute class
	}

//	/**
//	 * Returns the entity referenced par the given field
//	 * 
//	 * @param domainField
//	 * @param domainModel
//	 * @return
//	 */
//	private DomainEntity getReferencedEntity(DomainField domainField, DomainModel domainModel) {
//		DomainType domainFieldType = domainField.getType();
//		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");
//		return domainModel.getEntity(domainFieldType.getName());
//	}

	/**
	 * Converts a "reference/link" attribute <br>
	 * eg : car : Car ; <br>
	 * 
	 * @param domainEntityField
	 *            the field to be converted
	 * @return
	 */
/*** 	// PSEUDO FOREIGN KEYS REMOVED IN V 3.3

	private DslModelAttribute convertAttributePseudoForeignKey(DomainField domainEntityField, DomainModel domainModel) {
		log("convertAttributePseudoForeignKey() : name = " + domainEntityField.getName());

		DomainEntity referencedEntity = getReferencedEntity(domainEntityField, domainModel);

		DomainField referencedEntityIdField = getReferencedEntityIdField(referencedEntity);

		// --- Attribute name (keep the same name to avoid potential naming collision) 
		String attributeName = domainEntityField.getName(); 

		// --- Attribute type
		check(referencedEntityIdField.getType().isNeutralType(),
				"Invalid referenced entity field type. Neutral type expected");
		String attributeType = referencedEntityIdField.getTypeName();

		// --- Create a new attribute to represent the FK referenciing the entity
		DslModelAttribute genericAttributeForFK = new DslModelAttribute();
		genericAttributeForFK.setName(attributeName);
		genericAttributeForFK.setNeutralType(attributeType);
		initAttributeDefaultValues(genericAttributeForFK, domainEntityField);

		// --- Use REFERENCED entity id field annotations
		Collection<DomainAnnotation> referencedFieldAnnotations = referencedEntityIdField.getAnnotations().values();
//		applyAnnotationsAboutValue(genericAttributeForFK, referencedFieldAnnotations);
//		applyAnnotationsAboutType(genericAttributeForFK, referencedFieldAnnotations);
//		applyAnnotationsAboutDatabase(genericAttributeForFK, referencedFieldAnnotations);
//		applyAnnotationsWithStringParameter(genericAttributeForFK, referencedFieldAnnotations);
		annotationsConverter.applyAnnotationsForPseudoForeignKey(genericAttributeForFK, referencedFieldAnnotations);

		// --- Set flag as "Pseudo Foreign Key" (Simple FK)
		genericAttributeForFK.setFKSimple(true);
		genericAttributeForFK.setReferencedEntityClassName(referencedEntity.getName());

		return genericAttributeForFK;
	}
***/
	


}
