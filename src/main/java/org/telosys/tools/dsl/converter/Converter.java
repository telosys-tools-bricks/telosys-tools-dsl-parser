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
import java.util.LinkedList;

import org.telosys.tools.commons.logger.ConsoleLogger;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.Optional;

public class Converter {

	private static final boolean LOG = false;
	private static final ConsoleLogger logger = new ConsoleLogger();

	private void log(String msg) {
		if (LOG) {
			logger.log(this, msg);
		}
	}

	private final AnnotationsConverter annotationsConverter ;
	private int linkIdCounter = 0;

	
	/**
	 * Constructor
	 */
	public Converter() {
		super();
		this.annotationsConverter = new AnnotationsConverter();
	}

	/**
	 * Converts PARSER MODEL to DSL/Generic model <br>
	 * 
	 * @param domainModel
	 *            paser domain model
	 * @return DSL/Generic model
	 * @throws IllegalStateException
	 *             if an error occurs
	 */
	public Model convertToGenericModel(DomainModel domainModel) {

		// convert all entities
		DslModel dslModel = step1ConvertAllEntities(domainModel);

		step2CreateAllLinks(domainModel, dslModel);

		// Finally sort the entities by class name
		dslModel.sortEntitiesByClassName();

		return dslModel;
	}

	private void check(boolean expr, String errorMessage) {
		if (!expr) {
			throw new IllegalStateException(errorMessage);
		}
	}

	/**
	 * Returns TRUE if the given field can be considered as a "Pseudo Foreign
	 * Key"
	 * 
	 * @param domainEntityField
	 * @return
	 */
	private boolean isPseudoForeignKey(DomainField domainEntityField) {
		DomainType domainFieldType = domainEntityField.getType();
		if (domainFieldType.isEntity()) { // The field must reference an Entity
			if (domainEntityField.getCardinality() == 1) { // The field must
															// reference 1 and
															// only 1 Entity
				return true;
			}
			// If cardinality > 1 : not a FK, just a link "OneToMany"
		}
		return false;
	}

	/**
	 * Define all entities and attributes
	 * 
	 * @param domainModel
	 *            DSL model
	 * @param genericModel
	 *            Generic model
	 */
	protected DslModel step1ConvertAllEntities(DomainModel domainModel) {
		log("convertEntities()...");

		DslModel dslModel = new DslModel();
		dslModel.setName(voidIfNull(domainModel.getName()));
		dslModel.setDescription(voidIfNull(domainModel.getDescription()));

		// for each "DomainEntity" create a void "GenericEntity"
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			DslModelEntity genericEntity = createVoidEntity(domainEntity);
			dslModel.getEntities().add(genericEntity);
		}

		// STEP 2 : Convert basic attributes ( attributes with neutral type )
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			// Get the GenericEntity built previously
			DslModelEntity genericEntity = (DslModelEntity) dslModel.getEntityByClassName(domainEntity.getName());
			// Convert all attributes to "basic type" 
			// or "void pseudo FK attribute" (to keep the initial attributes order)
			convertAttributes(domainEntity, genericEntity);
		}

		return dslModel;
	}

	protected void step2CreateAllLinks(DomainModel domainModel, DslModel genericModel) {

		// STEP 2.1 : Create the links ( from attributes referencing an entity )
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			// Get the GenericEntity built previously
			DslModelEntity genericEntity = (DslModelEntity) genericModel.getEntityByClassName(domainEntity.getName());
			// Creates a link for each field referencing an entity
			createLinks(domainEntity, genericEntity, domainModel);
		}

		// STEP 2.2 : Build and set "pseudo Foreign Key Attributes"
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			// Get the GenericEntity built previously
			DslModelEntity genericEntity = (DslModelEntity) genericModel.getEntityByClassName(domainEntity.getName());
			// Replaces the "pseudo FK" attributes if any
			for (DomainField field : domainEntity.getFields()) {
				if (isPseudoForeignKey(field)) {
					// Build the "pseudo FK attribute"
					DslModelAttribute pseudoFKAttribute = convertAttributePseudoForeignKey(field, domainModel);

					// Search the original "void attribute" in the
					// "GenericEntity" and replace it by the "pseudo FK
					// attribute"
					String originalName = field.getName();
					Attribute old = genericEntity.replaceAttribute(originalName, pseudoFKAttribute);
					check(old != null, "Attribute '" + originalName + "' not found");
				}
			}
		}
	}

	private DslModelEntity createVoidEntity(DomainEntity domainEntity) {
		log("convertEntity(" + domainEntity.getName() + ")...");
		DslModelEntity genericEntity = new DslModelEntity();
		genericEntity.setClassName(notNull(domainEntity.getName()));
		genericEntity.setFullName(notNull(domainEntity.getName()));

		// --- NB : Database Table must be set in order to be able do
		// "getEntityByTableName()"
		genericEntity.setDatabaseTable(determineTableName(domainEntity)); // Same
																			// as
																			// "className"
																			// (unique)
		genericEntity.setDatabaseType("TABLE"); // Type is "TABLE" by default
		genericEntity.setDatabaseCatalog("");
		genericEntity.setDatabaseSchema("");
		genericEntity.setDatabaseForeignKeys(new LinkedList<ForeignKey>()); // Void
																			// list
																			// (No
																			// Foreign
																			// keys)

		return genericEntity;
	}

	/**
	 * Define all attributes - this method needs all entities defined in the
	 * generic model for links resolution
	 * 
	 * @param domainEntity
	 * @param genericEntity
	 */
	private void convertAttributes(DomainEntity domainEntity, DslModelEntity genericEntity) {
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
				genericEntity.getAttributes().add(genericAttribute);
			} else {
				// Not a "neutral type" ==> "entity reference" ?
				if (isPseudoForeignKey(domainEntityField)) {
					// Add the new attribute to the entity
					genericEntity.getAttributes().add(genericAttribute);
				}
			}
		}
	}

	private void createLinks(DomainEntity domainEntity, DslModelEntity genericEntity, DomainModel domainModel) {
		log("createLinks()...");
		if (domainEntity.getFields() == null) {
			return;
		}
		for (DomainField domainEntityField : domainEntity.getFields()) {

			if (domainEntityField.getType().isEntity()) { // If this field
															// references an
															// entity
				// REFERENCE TO AN ENTITY = LINK
				log("createLinks() : " + domainEntityField.getName() + " : entity type (link)");
				// Link type attribute (reference to 1 or N other entity )
				linkIdCounter++;
				DslModelLink genericLink = convertAttributeLink(domainEntityField, domainModel);
				// Add the new link to the entity
				genericEntity.getLinks().add(genericLink);
			}
		}
	}

	/**
	 * Converts a "neutral type" attribute <br>
	 * eg : id : integer {@Id}; <br>
	 * 
	 * @param domainEntityField
	 * @return
	 */
	private void convertAttributeNeutralType(DomainField domainEntityField, DslModelAttribute genericAttribute) {
		log("convertAttributeNeutralType() : name = " + domainEntityField.getName());

		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isNeutralType(), "Invalid field type. Neutral type expected");
		DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;

		// the "neutral type" is now the only type managed at this level
		genericAttribute.setNeutralType(domainNeutralType.getName());

		// If the attribute has a "binary" type
		if (domainEntityField.getType() == DomainNeutralTypes.getType(DomainNeutralTypes.BINARY_BLOB)) {
			// TODO : genericAttribute.setBinary(true);
		}

		initAttributeDefaultValues(genericAttribute, domainEntityField);

		// Populate field from annotations if any
		if (domainEntityField.getAnnotations() != null) {
			log("Converter : annotations found");
			Collection<DomainAnnotation> fieldAnnotations = domainEntityField.getAnnotations().values();
//			for (DomainAnnotation annotation : fieldAnnotations) {
//				log("Converter : annotation '" + annotation.getName() + "'");
//				// The annotation name is like "Id", "NotNull", "Max", etc
//				// without "@" at the beginning and without "#" at the end
//				if (AnnotationName.ID.equals(annotation.getName())) {
//					log("Converter : annotation @Id");
//					genericAttribute.setKeyElement(true);
//					// If "@Id" => "@NotNull"
//					genericAttribute.setNotNull(true);
//				}
//				if (AnnotationName.AUTO_INCREMENTED.equals(annotation.getName())) {
//					log("Converter : annotation @AutoIncremented");
//					genericAttribute.setAutoIncremented(true);
//				}
//			}
			annotationsConverter.applyAnnotationsForNeutralType(genericAttribute, fieldAnnotations);
		} else {
			log("Converter : no annotation");
		}
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

	/**
	 * Returns the entity referenced par the given field
	 * 
	 * @param domainField
	 * @param domainModel
	 * @return
	 */
	private DomainEntity getReferencedEntity(DomainField domainField, DomainModel domainModel) {
		DomainType domainFieldType = domainField.getType();
		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");
		DomainEntity domainEntityTarget = domainModel.getEntity(domainFieldType.getName());
		return domainEntityTarget;
	}

	/**
	 * Converts a "reference/link" attribute <br>
	 * eg : car : Car ; <br>
	 * 
	 * @param domainEntityField
	 *            the field to be converted
	 * @return
	 */
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

	/**
	 * Returns the '@Id' attribute for the given entity
	 * 
	 * @param domainEntity
	 * @return
	 */
	private DomainField getReferencedEntityIdField(DomainEntity domainEntity) {
		DomainField id = null;
		int idCount = 0;
		check(domainEntity.getFields().size() > 0, "No field in referenced entity '" + domainEntity.getName() + "'");
		for (DomainField field : domainEntity.getFields()) {
			if (isId(field)) {
				id = field;
				idCount++;
			}
		}
		if (idCount == 0) {
			throw new IllegalStateException("Entity '" + domainEntity.getName() + "' : no @Id");
		}
		if (idCount > 1) {
			throw new IllegalStateException("Entity '" + domainEntity.getName() + "' has more than 1 @Id");
		}
		return id;
	}

	private boolean isId(DomainField field) {
		for (String annotationName : field.getAnnotationNames()) {
			if (AnnotationName.ID.equals(annotationName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts a "LINK" attribute <br>
	 * eg : car : Car ; <br>
	 * 
	 * @param domainEntityField
	 * @param genericModel
	 * @return
	 */
	private DslModelLink convertAttributeLink(DomainField domainEntityField, DomainModel domainModel) {

		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");

		DomainEntity domainEntityTarget = domainModel.getEntity(domainFieldType.getName());

		// Check target existence
		check((domainEntityTarget != null),
				"No target entity for field '" + domainEntityField.getName() + "'. Cannot create Link");

		DslModelLink genericLink = new DslModelLink();

		genericLink.setId("Link" + linkIdCounter); // Link ID : generated (just
													// to ensure not null )
		// genericLink.setSelected(true); // nothing for link selection =>
		// selected by default

		// Set target entity info
		genericLink.setTargetEntityClassName(domainEntityField.getType().getName());
		genericLink.setTargetTableName(determineTableName(domainEntityTarget));

		// --- Cardinality
		Cardinality cardinality;
		if (domainEntityField.getCardinality() == 1) {
			cardinality = Cardinality.MANY_TO_ONE;
		} else {
			cardinality = Cardinality.ONE_TO_MANY;
		}
		genericLink.setCardinality(cardinality);

		// --- Field info based on cardinality
		genericLink.setFieldName(domainEntityField.getName());
		if (domainEntityField.getCardinality() == 1) {
			// Reference to only ONE entity => MANY TO ONE
			genericLink.setFieldType(domainEntityField.getType().getName()); // use
																				// the
																				// Entity
																				// name
			genericLink.setOwningSide(true);
			genericLink.setInverseSide(false);
			genericLink.setInverseSideLinkId(null);
		} else {
			// Reference to only MANY entities => ONE TO MANY
			genericLink.setFieldType("java.util.List"); // TODO : fix this Java dependency
			genericLink.setOwningSide(false);
			genericLink.setInverseSide(true);
			genericLink.setInverseSideLinkId(null);
		}
		genericLink.setCascadeOptions(new CascadeOptions()); // Void cascade
																// otions
																// (default
																// values)

		genericLink.setBasedOnForeignKey(false);
		genericLink.setBasedOnJoinTable(false);
		genericLink.setComparableString("");
		genericLink.setFetchType(FetchType.DEFAULT);
		genericLink.setForeignKeyName("");
		genericLink.setJoinColumns(null);
		genericLink.setJoinTable(null);
		genericLink.setJoinTableName(null);
		genericLink.setMappedBy(null);
		genericLink.setOptional(Optional.UNDEFINED);
		genericLink.setSourceTableName(null);

		// Annotation
		if (domainEntityField.getAnnotations() != null) {
//			for (DomainAnnotation annotation : domainEntityField.getAnnotations().values()) {
//				if ("@Embedded".equals(annotation.getName())) {
//					genericLink.setIsEmbedded(true);
//				}
//			}
			annotationsConverter.applyAnnotationsForLink(genericLink, domainEntityField.getAnnotations().values());
		}

		return genericLink;
	}

	/**
	 * Conversion rule to determine the table name for a given entity
	 * 
	 * @param domainEntity
	 * @return
	 */
	private String determineTableName(DomainEntity domainEntity) {
		if (domainEntity == null) {
			throw new IllegalStateException("DomainEntity is null");
		}
		if (domainEntity.getName() == null) {
			throw new IllegalStateException("DomainEntity name is null");
		}
		return domainEntity.getName();
	}

	private String notNull(String value) {
		if (value == null) {
			throw new IllegalStateException("Unexpected null value");
		}
		return value;
	}

	/**
	 * Returns a void string if the given value is null
	 * 
	 * @param value
	 * @return
	 */
	private String voidIfNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}

}
