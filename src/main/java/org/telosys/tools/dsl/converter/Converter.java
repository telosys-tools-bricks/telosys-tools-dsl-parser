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

	private int linkIdCounter = 0;

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
			// Convert all attributes to "basic type" or "void pseudo FK
			// attribute" (to keep the initial attributes order)
			convertAttributes(domainEntity, genericEntity, dslModel);
		}

		return dslModel;
	}

	protected void step2CreateAllLinks(DomainModel domainModel, DslModel genericModel) {

		// STEP x : Create the links ( from attributes referencing an entity )
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			// Get the GenericEntity built previously
			DslModelEntity genericEntity = (DslModelEntity) genericModel.getEntityByClassName(domainEntity.getName());
			// Creates a link for each field referencing an entity
			// createLinks(domainEntity, genericEntity, genericModel);
			createLinks(domainEntity, genericEntity, domainModel);
		}

		// STEP 3 : Build and set "pseudo Foreign Key Attributes"
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			// Get the GenericEntity built previously
			DslModelEntity genericEntity = (DslModelEntity) genericModel.getEntityByClassName(domainEntity.getName());
			// Replaces the "pseudo FK" attributes if any
			for (DomainField field : domainEntity.getFields()) {
				if (isPseudoForeignKey(field)) {
					// Build the "pseudo FK attribute"
					// DslModelAttribute pseudoFKAttribute =
					// convertAttributePseudoForeignKey(field);
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
	 *            DSL entity
	 * @param genericEntity
	 *            Generic entity
	 * @param genericModel
	 *            Generic model
	 */
	private void convertAttributes(DomainEntity domainEntity, DslModelEntity genericEntity, DslModel genericModel) {
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
				// DslModelAttribute genericAttribute =
				// convertAttributeNeutralType( domainEntityField );
				convertAttributeNeutralType(domainEntityField, genericAttribute);
				// check(genericAttribute != null, "convertAttributeNeutralType
				// returns null");
				// Add the new "basic attribute" to the entity
				genericEntity.getAttributes().add(genericAttribute);
			} else {
				// Not a "neutral type" ==> "entity reference" ?
				if (isPseudoForeignKey(domainEntityField)) {
					// // Add a "temporary void attribute" at the expected
					// position in the attributes list
					// DslModelAttribute genericAttribute = new
					// DslModelAttribute();
					// genericAttribute.setName(
					// notNull(domainEntityField.getName()) );
					// Add the new attribute to the entity
					genericEntity.getAttributes().add(genericAttribute);
				}
			}
		}
	}

	// private void createLinks(DomainEntity domainEntity, DslModelEntity
	// genericEntity, DslModel genericModel) {
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
				// DslModelLink genericLink = convertAttributeLink(
				// domainEntityField, genericModel );
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

		// DslModelAttribute genericAttribute = new DslModelAttribute();
		// genericAttribute.setName( notNull(domainEntityField.getName()) );

		// the "neutral type" is now the only type managed at this level
		// genericAttribute.setSimpleType(convertNeutralTypeToSimpleType(domainNeutralType)
		// );
		// genericAttribute.setFullType(convertNeutralTypeToFullType(domainNeutralType)
		// );
		genericAttribute.setNeutralType(domainNeutralType.getName());

		// If the attribute has a "binary" type
		if (domainEntityField.getType() == DomainNeutralTypes.getType(DomainNeutralTypes.BINARY_BLOB)) {
			// TODO
			// genericAttribute.setBinary(true);
		}

		initAttributeDefaultValues(genericAttribute, domainEntityField);

		// Populate field from annotations if any
		if (domainEntityField.getAnnotations() != null) {
			log("Converter : annotations found");
			Collection<DomainAnnotation> fieldAnnotations = domainEntityField.getAnnotations().values();
			for (DomainAnnotation annotation : fieldAnnotations) {
				log("Converter : annotation '" + annotation.getName() + "'");
				// The annotation name is like "Id", "NotNull", "Max", etc
				// without "@" at the beginning and without "#" at the end
				if (AnnotationName.ID.equals(annotation.getName())) {
					log("Converter : annotation @Id");
					genericAttribute.setKeyElement(true);
					// If "@Id" => "@NotNull"
					genericAttribute.setNotNull(true);
				}
				if (AnnotationName.AUTO_INCREMENTED.equals(annotation.getName())) {
					log("Converter : annotation @AutoIncremented");
					genericAttribute.setAutoIncremented(true);
				}
			}
//			applyAnnotationsAboutValue(genericAttribute, fieldAnnotations);
//			applyAnnotationsAboutType(genericAttribute, fieldAnnotations);
//			applyAnnotationsAboutDatabase(genericAttribute, fieldAnnotations);
			applyAnnotations(genericAttribute, fieldAnnotations);
		} else {
			log("Converter : no annotation");
		}
		// return genericAttribute;
	}

	private void initAttributeDefaultValues(DslModelAttribute genericAttribute, DomainField domainEntityField) {
		// genericAttribute.setBooleanFalseValue(booleanFalseValue);
		// genericAttribute.setBooleanTrueValue(booleanTrueValue);
		genericAttribute.setDatabaseComment(""); // TODO with @DbComment(xxx)
		genericAttribute.setDatabaseName(domainEntityField.getName()); // TODO
																		// with
																		// @DbColumn(xxx)
		genericAttribute.setDatabaseDefaultValue(null); // TODO with
														// @DbDefaultValue(xxx)
		// genericAttribute.setDatabaseType(databaseType);
		// genericAttribute.setDateAfter(isDateAfter);
		// genericAttribute.setDateAfterValue(dateAfterValue);
		// genericAttribute.setDateBefore(isDateBefore);
		// genericAttribute.setDateBeforeValue(dateBeforeValue);
		// genericAttribute.setDateType(dateType);
		// genericAttribute.setDefaultValue(defaultValue); // only set by
		// annotation
		genericAttribute.setLabel(domainEntityField.getName()); // overridden by
																// @Label(xxx)
																// if any
		// genericAttribute.setInputType(inputType); // only set by annotation
		genericAttribute.setSelected(true);
		// genericAttribute.setPattern(pattern); // only set by annotation

	}

	private void applyAnnotations(DslModelAttribute attribute, Collection<DomainAnnotation> annotations) {
		applyAnnotationsAboutValue(attribute, annotations);
		applyAnnotationsAboutType(attribute, annotations);
		applyAnnotationsAboutDatabase(attribute, annotations);
		applyAnnotationsWithStringParameter(attribute, annotations);
	}

	/**
	 * Apply annotations about field value constraints 
	 * 
	 * @param genericAttribute
	 * @param fieldAnnotations
	 */
	private void applyAnnotationsAboutValue(DslModelAttribute genericAttribute,
			Collection<DomainAnnotation> fieldAnnotations) {
		for (DomainAnnotation annotation : fieldAnnotations) {

			if (AnnotationName.NOT_NULL.equals(annotation.getName())) {
				log("Converter : annotation @NotNull ");
				genericAttribute.setNotNull(true);
				// genericAttribute.setDatabaseNotNull(true);
			}
			if (AnnotationName.NOT_EMPTY.equals(annotation.getName())) {
				log("Converter : annotation @NotEmpty ");
				genericAttribute.setNotEmpty(true);
			}
			if (AnnotationName.NOT_BLANK.equals(annotation.getName())) {
				log("Converter : annotation @NotBlank ");
				genericAttribute.setNotBlank(true);
			}
			if (AnnotationName.MIN.equals(annotation.getName())) {
				log("Converter : annotation @Min ");
				genericAttribute.setMinValue(annotation.getParameterAsBigDecimal());
			}
			if (AnnotationName.MAX.equals(annotation.getName())) {
				log("Converter : annotation @Max ");
				genericAttribute.setMaxValue(annotation.getParameterAsBigDecimal());
			}
			if (AnnotationName.SIZE_MIN.equals(annotation.getName())) {
				log("Converter : annotation @SizeMin ");
				genericAttribute.setMinLength(annotation.getParameterAsInteger());
			}
			if (AnnotationName.SIZE_MAX.equals(annotation.getName())) {
				log("Converter : annotation @SizeMax ");
				genericAttribute.setMaxLength(annotation.getParameterAsInteger());
			}
			if (AnnotationName.PAST.equals(annotation.getName())) {
				log("Converter : annotation @Past ");
				genericAttribute.setDatePast(true);
			}
			if (AnnotationName.FUTURE.equals(annotation.getName())) {
				log("Converter : annotation @Future ");
				genericAttribute.setDateFuture(true);
			}
			if (AnnotationName.LONG_TEXT.equals(annotation.getName())) {
				log("Converter : annotation @LongText");
				genericAttribute.setLongText(true);
			}

		}
	}

	/**
	 * Populates generic attribute type information from the given annotations
	 * 
	 * @param genericAttribute
	 * @param fieldAnnotations
	 */
	private void applyAnnotationsAboutType(DslModelAttribute genericAttribute,
			Collection<DomainAnnotation> fieldAnnotations) {
		for (DomainAnnotation annotation : fieldAnnotations) {

			if (AnnotationName.PRIMITIVE_TYPE.equals(annotation.getName())) {
				log("Converter : annotation @PrimitiveType");
				genericAttribute.setPrimitiveTypeExpected(true);
			}
			if (AnnotationName.UNSIGNED_TYPE.equals(annotation.getName())) {
				log("Converter : annotation @UnsignedType");
				genericAttribute.setUnsignedTypeExpected(true);
			}
			if (AnnotationName.OBJECT_TYPE.equals(annotation.getName())) {
				log("Converter : annotation @ObjectType");
				genericAttribute.setObjectTypeExpected(true);
			}
			if (AnnotationName.SQL_TYPE.equals(annotation.getName())) {
				log("Converter : annotation @SqlType");
				genericAttribute.setSqlTypeExpected(true);
			}
		}
	}

	private void applyAnnotationsAboutDatabase(DslModelAttribute genericAttribute,
			Collection<DomainAnnotation> fieldAnnotations) {
		final String msg = "Converter : annotations for databse : @" ;
		boolean fieldNotNull = false;
		Integer fieldSizeMax = 0;
		for (DomainAnnotation annotation : fieldAnnotations) {

			// Database annotations ( @DbXxxx ) :
			if (AnnotationName.DB_SIZE.equals(annotation.getName())) {
				log(msg + AnnotationName.DB_SIZE );
				genericAttribute.setDatabaseSize(annotation.getParameterAsInteger());
			}
			if (AnnotationName.DB_NAME.equals(annotation.getName())) {
				log(msg + AnnotationName.DB_NAME );
				genericAttribute.setDatabaseName(annotation.getParameterAsString());
			}
			if (AnnotationName.DB_TYPE.equals(annotation.getName())) {
				log(msg + AnnotationName.DB_TYPE );
				genericAttribute.setDatabaseType(annotation.getParameterAsString());
			}
			if (AnnotationName.DB_COMMENT.equals(annotation.getName())) {
				log(msg + AnnotationName.DB_COMMENT );
				genericAttribute.setDatabaseComment(annotation.getParameterAsString());
			}
			if (AnnotationName.DB_DEFAULT_VALUE.equals(annotation.getName())) {
				log(msg + AnnotationName.DB_DEFAULT_VALUE );
				genericAttribute.setDatabaseDefaultValue(annotation.getParameterAsString());
			}
			
			// Other annotations :
			if (AnnotationName.ID.equals(annotation.getName())) {
				log(msg + AnnotationName.ID );
				fieldNotNull = true;
			}
			if (AnnotationName.NOT_NULL.equals(annotation.getName())) {
				log(msg + AnnotationName.NOT_NULL );
				fieldNotNull = true;
			}
			if (AnnotationName.SIZE_MAX.equals(annotation.getName())) {
				log(msg + AnnotationName.SIZE_MAX );
				fieldSizeMax = annotation.getParameterAsInteger();
			}
		}
		// Complete with other field annotations
		if ( genericAttribute.getDatabaseSize() == null && fieldSizeMax != null ) {
			genericAttribute.setDatabaseSize(fieldSizeMax);
		}
		if ( fieldNotNull ) {
			genericAttribute.setDatabaseNotNull(true);
		}
	}

	/**
	 * Apply annotations having a string parameter (new annotations added in
	 * version 3.2.0)
	 * 
	 * @param genericAttribute
	 * @param annotations
	 */
	private void applyAnnotationsWithStringParameter(DslModelAttribute genericAttribute,
			Collection<DomainAnnotation> annotations) {
		for (DomainAnnotation annotation : annotations) {
			log("Converter / populateAttributeDbInfo : annotation '" + annotation.getName() + "'");
			// --- Added in ver 3.2.0
			if (AnnotationName.DEFAULT_VALUE.equals(annotation.getName())) {
				log("Converter : annotation @DefaultValue");
				genericAttribute.setDefaultValue(annotation.getParameterAsString());
			}
			if (AnnotationName.INITIAL_VALUE.equals(annotation.getName())) {
				log("Converter : annotation @InitialValue");
				genericAttribute.setInitialValue(annotation.getParameterAsString());
			}
			if (AnnotationName.LABEL.equals(annotation.getName())) {
				log("Converter : annotation @Label");
				genericAttribute.setLabel(annotation.getParameterAsString());
			}
			if (AnnotationName.INPUT_TYPE.equals(annotation.getName())) {
				log("Converter : annotation @InputType");
				genericAttribute.setInputType(annotation.getParameterAsString());
			}
			if (AnnotationName.PATTERN.equals(annotation.getName())) {
				log("Converter : annotation @Pattern");
				genericAttribute.setInputType(annotation.getParameterAsString());
			}

			// TODO :
			// @After(DateISO/TimeISO)
			// @Before(DateISO/TimeISO)
			// @Pattern(xxx) or @RegExp ???
		}
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

	private DomainEntity getReferencedEntity(DomainType domainFieldType, DomainModel domainModel) {
		return domainModel.getEntity(domainFieldType.getName());
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

		// DomainType domainFieldType = domainEntityField.getType();
		// check(domainFieldType.isEntity(), "Invalid field type. Entity type
		// expected");
		// // DomainEntity referencedEntity = (DomainEntity) domainFieldType;
		DomainEntity referencedEntity = getReferencedEntity(domainEntityField, domainModel);

		DomainField referencedEntityIdField = getReferencedEntityIdField(referencedEntity);

		// --- Attribute name
		String attributeName = domainEntityField.getName(); // Keep the same
															// name to avoid
															// potential naming
															// collision

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
//		applyAnnotationsAboutValue(genericAttribute, fieldAnnotations);
//		applyAnnotationsAboutType(genericAttribute, fieldAnnotations);
		
//		applyAnnotations(genericAttribute, fieldAnnotations);
		applyAnnotationsAboutValue(genericAttributeForFK, referencedFieldAnnotations);
		applyAnnotationsAboutType(genericAttributeForFK, referencedFieldAnnotations);
		applyAnnotationsAboutDatabase(genericAttributeForFK, referencedFieldAnnotations);
		applyAnnotationsWithStringParameter(genericAttributeForFK, referencedFieldAnnotations);

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
	// private DslModelLink convertAttributeLink( DomainField domainEntityField,
	// DslModel genericModel ) {
	private DslModelLink convertAttributeLink(DomainField domainEntityField, DomainModel domainModel) {

		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");

		// DomainEntity domainEntityTarget = (DomainEntity) domainFieldType;
		DomainEntity domainEntityTarget = domainModel.getEntity(domainFieldType.getName());

		// Check target existence
		// DslModelEntity genericEntityTarget =
		// (DslModelEntity)
		// genericModel.getEntityByClassName(domainEntityTarget.getName());
		// check( ( genericEntityTarget != null ), "No target entity for field
		// '" + domainEntityField.getName() + "'. Cannot create Link");
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
			genericLink.setFieldType("java.util.List"); // use a COLLECTION type
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
			for (DomainAnnotation annotation : domainEntityField.getAnnotations().values()) {
				if ("@Embedded".equals(annotation.getName())) {
					genericLink.setIsEmbedded(true);
				}
			}
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
