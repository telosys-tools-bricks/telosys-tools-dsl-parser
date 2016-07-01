/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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
package org.telosys.tools.dsl.generic.converter;

import java.util.LinkedList;

import org.telosys.tools.commons.ConsoleLogger;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.generic.model.GenericAttribute;
import org.telosys.tools.dsl.generic.model.GenericEntity;
import org.telosys.tools.dsl.generic.model.GenericLink;
import org.telosys.tools.dsl.generic.model.GenericModel;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.Optional;

public class Converter {
	
	private final static boolean log = true ;
	private final static TelosysToolsLogger logger = new ConsoleLogger();
	private void log(String msg) {
		if ( log ) {
			logger.log(this, msg);
		}
	}

	private int linkIdCounter = 0 ;

	/**
	 * Converts the DSL model to the Generic model <br>
	 * 
	 * @param domainModel DSL model
	 * @return Generic model
	 * @throws IllegalStateException if an error occurs
	 */
	public Model convertToGenericModel(DomainModel domainModel) {
		
//		checkTypeMapping();
		
		GenericModel genericModel = new GenericModel();
//		genericModel.setType( ModelType.DOMAIN_SPECIFIC_LANGUAGE );
		genericModel.setName( voidIfNull(domainModel.getName()) );
//		genericModel.setVersion( GenericModelVersion.VERSION );
		genericModel.setDescription( voidIfNull(domainModel.getDescription() ) );

		// convert all entities
		convertEntities(domainModel, genericModel);
		
		// Finally sort the entities by class name 
		genericModel.sortEntitiesByClassName();
		
		return genericModel;
	}
	
//	private void checkTypeMapping() {
//		
//		if ( typeMapping.size() != DomainNeutralTypes.getNames().size() ) {
//			throw new IllegalStateException("Inconsistant type mapping in converter ("+
//					typeMapping.size() + " entries, " + DomainNeutralTypes.getNames().size() + " expected)");
//		}
//	}
	
	private void check(boolean expr, String errorMessage ) {
		if ( ! expr ) {
			throw new IllegalStateException(errorMessage);
		}
	}
	
	/**
	 * Define all entities and attributes
	 * @param domainModel DSL model
	 * @param genericModel Generic model
	 */
	private void convertEntities(DomainModel domainModel, GenericModel genericModel) {
		log("convertEntities()...");
		if(domainModel.getEntities() == null) {
			return;
		}

		// STEP 1 : Convert all the existing entities
		for(DomainEntity domainEntity : domainModel.getEntities()) {
//			GenericEntity genericEntity = new GenericEntity();
////			genericEntity.setClassName(notNullOrVoidValue(domainEntity.getName(), EMPTY_STRING));
////			genericEntity.setFullName(notNullOrVoidValue(domainEntity.getName(), EMPTY_STRING));			
//			genericEntity.setClassName(voidIfNull(domainEntity.getName()));
//			genericEntity.setFullName(voidIfNull(domainEntity.getName()));
			
			GenericEntity genericEntity = convertEntity( domainEntity );
			genericModel.getEntities().add(genericEntity);
		}
		
		// STEP 2 : Convert the attributes ( basic attributes and link attributes ) 
		for(DomainEntity domainEntity : domainModel.getEntities()) {
			GenericEntity genericEntity = (GenericEntity) genericModel.getEntityByClassName(domainEntity.getName());
			convertAttributes(domainEntity, genericEntity, genericModel);
		}
		
//		// STEP 3 : Build "keyAttributes"
//		for(DomainEntity domainEntity : domainModel.getEntities()) {
//			GenericEntity genericEntity = (GenericEntity) genericModel.getEntityByClassName(domainEntity.getName());
//			// TODO 
//		}
		
	}
	
	private GenericEntity convertEntity( DomainEntity domainEntity ) {
		log("convertEntity("+ domainEntity.getName() +")...");
		GenericEntity genericEntity = new GenericEntity();
		genericEntity.setClassName(notNull(domainEntity.getName()));
		genericEntity.setFullName(notNull(domainEntity.getName()));
		
		//--- NB : Database Table must be set in order to be able do "getEntityByTableName()"
		genericEntity.setDatabaseTable(determineTableName(domainEntity)); // Same as "className" (unique)
		genericEntity.setDatabaseType("TABLE"); // Type is "TABLE" by default
		genericEntity.setDatabaseCatalog("");
		genericEntity.setDatabaseSchema("");
		genericEntity.setDatabaseForeignKeys(new LinkedList<ForeignKey>()); // Void list (No Foreign keys)
		
		return genericEntity ;
	}
	
	/**
	 * Define all attributes - this method needs all entities defined in the generic model for links resolution
	 * @param domainEntity DSL entity
	 * @param genericEntity Generic entity
	 * @param genericModel Generic model
	 */
	private void convertAttributes(DomainEntity domainEntity, GenericEntity genericEntity, GenericModel genericModel) {
		log("convertEntityAttributes()...");
		if(domainEntity.getFields() == null) {
			return;
		}
		for ( DomainEntityField domainEntityField : domainEntity.getFields() ) {

            DomainType domainFieldType = domainEntityField.getType();
            if (domainFieldType.isNeutralType() ) {
            	// STANDARD NEUTRAL TYPE = BASIC ATTRIBUTE
        		log("convertEntityAttributes() : " + domainEntityField.getName() + " : neutral type");
            	// Simple type attribute
            	GenericAttribute genericAttribute = convertAttributeNeutralType( domainEntityField );
                // Add the new attribute to the entity 
                if ( genericAttribute != null ) {
                    genericEntity.getAttributes().add(genericAttribute);
                }
            }
            else if (domainFieldType.isEntity() ) {
            	// REFERENCE TO AN ENTITY = LINK
        		log("convertEntityAttributes() : " + domainEntityField.getName() + " : entity type (link)");
            	// Link type attribute (reference to 1 or N other entity )
        		linkIdCounter++;
            	GenericLink genericLink = convertAttributeLink( domainEntityField, genericModel );
                if ( genericLink != null ) {
                	genericEntity.getLinks().add(genericLink);
                }
            }
		}
	}
	
	/**
	 * Converts a "neutral type" attribute <br>
	 * eg : id : integer {@Id}; <br>
	 * @param domainEntityField
	 * @return
	 */
	private GenericAttribute convertAttributeNeutralType( DomainEntityField domainEntityField ) {
		log("convertAttributeNeutralType() : name = " + domainEntityField.getName() );

		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isNeutralType(), "Invalid field type. Neutral type expected");
        DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;
		
        GenericAttribute genericAttribute = new GenericAttribute();
        genericAttribute.setName( notNull(domainEntityField.getName()) );
        
        // the "neutral type" is now the only type managed at this level
//        genericAttribute.setSimpleType(convertNeutralTypeToSimpleType(domainNeutralType) );
//        genericAttribute.setFullType(convertNeutralTypeToFullType(domainNeutralType) );
        genericAttribute.setNeutralType( domainNeutralType.getName() );
        
        // If the attribute has a "longtext" type
//        if ( domainEntityField.getType() == DomainNeutralTypes.getType(DomainNeutralTypes.LONGTEXT_CLOB) ) {
//            genericAttribute.setLongText(true);
//        }
//        else {
//            genericAttribute.setLongText(false);
//        }
        genericAttribute.setLongText(false); // TODO with @LongText
        
        // If the attribute has a "binary" type 
        if ( domainEntityField.getType() == DomainNeutralTypes.getType(DomainNeutralTypes.BINARY_BLOB) ) {
        	// TODO
            //genericAttribute.setBinary(true);
        }
        
        genericAttribute.setAutoIncremented(false); // TODO with @AutoIncremented
        // 
        //genericAttribute.setBooleanFalseValue(booleanFalseValue);
        //genericAttribute.setBooleanTrueValue(booleanTrueValue);
        genericAttribute.setDatabaseComment("");                       // TODO with @DbComment(xxx)
        genericAttribute.setDatabaseName(domainEntityField.getName()); // TODO with @DbColumn(xxx)
        genericAttribute.setDatabaseDefaultValue(null);                // TODO with @DbDefaultValue(xxx)
        // genericAttribute.setDatabaseType(databaseType);
        // genericAttribute.setDateAfter(isDateAfter);
        // genericAttribute.setDateAfterValue(dateAfterValue);
        // genericAttribute.setDateBefore(isDateBefore);
        // genericAttribute.setDateBeforeValue(dateBeforeValue);
        // genericAttribute.setDateType(dateType);
        // genericAttribute.setDefaultValue(defaultValue); // TODO
        genericAttribute.setLabel(domainEntityField.getName()); // TODO with @Label(xxx)
        // genericAttribute.setInputType(inputType); // TODO ???
        // genericAttribute.setNotBlank(notBlank);  // TODO
        // genericAttribute.setNotEmpty(notEmpty); // TODO
        genericAttribute.setSelected(true);
        // genericAttribute.setPattern(pattern); // TODO
        
        
        // Populate field from annotations if any
        if(domainEntityField.getAnnotations() != null) {
    		log("convertAttributeNeutralType() : annotations found" );
            for(DomainEntityFieldAnnotation annotation : domainEntityField.getAnnotations().values()) {
        		log("convertAttributeNeutralType() : annotation '"+ annotation.getName() + "'");
        		// The annotation name is like "Id", "NotNull", "Max", etc
        		// without "@" at the beginning and without "#" at the end
                if(AnnotationName.ID.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @Id => setKeyElement(true)" );
                    genericAttribute.setKeyElement(true);
                }
                if(AnnotationName.NOT_NULL.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @NotNull " );
                    genericAttribute.setNotNull(true);
                    genericAttribute.setDatabaseNotNull(true);
                }
                if(AnnotationName.MIN.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @Min " );
                    genericAttribute.setMinValue(annotation.getParameterAsBigDecimal() ); 
                }
                if(AnnotationName.MAX.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @Max " );
                    genericAttribute.setMaxValue(annotation.getParameterAsBigDecimal());
                }
                if(AnnotationName.SIZE_MIN.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @SizeMin " );
                    genericAttribute.setMinLength(annotation.getParameterAsInteger() );
                }
                if(AnnotationName.SIZE_MAX.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @SizeMax " );
                    Integer parameterValue = annotation.getParameterAsInteger();
                    genericAttribute.setMaxLength(parameterValue);
                    genericAttribute.setDatabaseSize(parameterValue);
                }
                if(AnnotationName.PAST.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @Past " );
                    genericAttribute.setDatePast(true);
                }
                if(AnnotationName.FUTURE.equals(annotation.getName())) {
            		log("convertAttributeNeutralType() : @Future " );
                    genericAttribute.setDateFuture(true);
                }
                // TODO 
                // @DefaultValue(xxx)
                // @Comment(xxx) --> used as DbComment ?
                // @AutoIncremented
                // @After(DateISO)
                // @Before(DateISO)
                //
                // @Label(xxx)
                // @InputType(xxx) or config ???
                // @Pattern(xxx) or @RegExp ???
                //
                // @DbColumn(xxx)
                // @DbType(xxx)
                // @DbDefaultValue(xxx)
            }
        }
        else {
    		log("convertAttributeNeutralType() : no annotation" );
        }
        return genericAttribute;
	}
	
	/**
	 * Converts a "LINK" attribute <br>
	 * eg : car : Car ; <br>
	 * @param domainEntityField
	 * @param genericModel
	 * @return
	 */
	private GenericLink convertAttributeLink( DomainEntityField domainEntityField, GenericModel genericModel ) {
		
		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");
//        DomainNeutralType domainNeutralType = (DomainNeutralType) domainFieldType;
//		
//        GenericAttribute genericAttribute = new GenericAttribute();
		
		DomainEntity domainEntityTarget = (DomainEntity) domainFieldType;
		
		// Check target existence
		GenericEntity genericEntityTarget =
				(GenericEntity) genericModel.getEntityByClassName(domainEntityTarget.getName());
		check( ( genericEntityTarget != null ), "No target entity for field '" + domainEntityField.getName() + "'. Cannot create Link");

		GenericLink genericLink = new GenericLink();

		genericLink.setId("Link"+linkIdCounter); // Link ID : generated (just to ensure not null )
		genericLink.setSelected(true); // nothing for link selection => selected by default
		
		// Set target entity info
		//genericLink.setTargetEntityClassName(notNull(domainEntityTarget.getName()));
		genericLink.setTargetEntityClassName(domainEntityField.getType().getName());
		genericLink.setTargetTableName(determineTableName(domainEntityTarget));
		
		//--- Cardinality
		Cardinality cardinality;
		if(domainEntityField.getCardinality() == 1) {
			cardinality = Cardinality.MANY_TO_ONE;
		} else {
			cardinality = Cardinality.ONE_TO_MANY;
		}
		genericLink.setCardinality(cardinality);
		
		//--- Field info based on cardinality
		genericLink.setFieldName(domainEntityField.getName());
		if(domainEntityField.getCardinality() == 1) {
			// Reference to only ONE entity => MANY TO ONE
			genericLink.setFieldType(domainEntityField.getType().getName()); // use the Entity name
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
		genericLink.setCascadeOptions(new CascadeOptions()); // Void cascade otions (default values)
		
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
        if(domainEntityField.getAnnotations() != null) {
            for(DomainEntityFieldAnnotation annotation : domainEntityField.getAnnotations().values()) {
                if("@Embedded".equals(annotation.getName())) {
                    genericLink.setIsEmbedded(true);
                }
            }
        }

        return genericLink;
	}
	
	/**
	 * Conversion des types
	 */
/*****
//	private static final Map<String, String> mapFullTypeConversion = new HashMap<String, String>();
//	private static final Map<String, String> mapSimpleTypeConversion = new HashMap<String, String>();
	private static final Map<String, Class<?>> typeMapping = new HashMap<String, Class<?>>();
	static {
//		//--- Full type
//		mapFullTypeConversion.put(DomainNeutralTypes.BOOLEAN,   Boolean.class.getCanonicalName());
//		
//		mapFullTypeConversion.put(DomainNeutralTypes.DECIMAL,   BigDecimal.class.getCanonicalName());
//		mapFullTypeConversion.put(DomainNeutralTypes.INTEGER,   Integer.class.getCanonicalName());
//		
//		mapFullTypeConversion.put(DomainNeutralTypes.STRING,    String.class.getCanonicalName());
//		
//		mapFullTypeConversion.put(DomainNeutralTypes.DATE,      Date.class.getCanonicalName());
//		mapFullTypeConversion.put(DomainNeutralTypes.TIME,      Date.class.getCanonicalName());
//		mapFullTypeConversion.put(DomainNeutralTypes.TIMESTAMP, Date.class.getCanonicalName());
//		
//		
//		//--- Simple type
//		mapSimpleTypeConversion.put(DomainNeutralTypes.BOOLEAN,   Boolean.class.getSimpleName());
//		
//		
//		mapSimpleTypeConversion.put(DomainNeutralTypes.DECIMAL,   BigDecimal.class.getSimpleName());
//		mapSimpleTypeConversion.put(DomainNeutralTypes.INTEGER,   Integer.class.getSimpleName());
//		
//		mapSimpleTypeConversion.put(DomainNeutralTypes.STRING,    String.class.getSimpleName());
//		
//		mapSimpleTypeConversion.put(DomainNeutralTypes.DATE,      Date.class.getSimpleName());
//		mapSimpleTypeConversion.put(DomainNeutralTypes.TIME,      Date.class.getSimpleName());
//		mapSimpleTypeConversion.put(DomainNeutralTypes.TIMESTAMP, Date.class.getSimpleName());

		typeMapping.put(DomainNeutralTypes.BOOLEAN,   Boolean.class );
		
		typeMapping.put(DomainNeutralTypes.BYTE,      Byte.class );
		typeMapping.put(DomainNeutralTypes.SHORT,     Short.class );
		typeMapping.put(DomainNeutralTypes.INTEGER,   Integer.class );
		typeMapping.put(DomainNeutralTypes.LONG,      Long.class );

		typeMapping.put(DomainNeutralTypes.FLOAT,     Float.class );
		typeMapping.put(DomainNeutralTypes.DOUBLE,    Double.class );
		typeMapping.put(DomainNeutralTypes.DECIMAL,   BigDecimal.class );
		
		typeMapping.put(DomainNeutralTypes.STRING,        String.class );
		typeMapping.put(DomainNeutralTypes.LONGTEXT_CLOB, String.class );
		
		typeMapping.put(DomainNeutralTypes.DATE,      Date.class );
		typeMapping.put(DomainNeutralTypes.TIME,      Date.class );
		typeMapping.put(DomainNeutralTypes.TIMESTAMP, Date.class );

		typeMapping.put(DomainNeutralTypes.BINARY_BLOB, byte[].class ); 
	}
*****/
//	private Class<?> getJavaTypeClass(DomainNeutralType domainNeutralType) {
//		if (domainNeutralType == null) {
//			throw new IllegalStateException("Neutral type is null");
//		}
//		Class<?> javaClass = typeMapping.get(domainNeutralType.getName());
//		if (javaClass == null) {
//			throw new IllegalStateException("Unknown type '" + domainNeutralType.getName() +"'");
//		}
//		return javaClass;
//	}
	
//	/**
//	 * Convert DSL types (string, integer, etc.) to Generic model types.
//	 * @param domainNeutralType DSL type
//	 * @return Generic model type
//	 */
//	private String convertNeutralTypeToSimpleType(DomainNeutralType domainNeutralType) {
////		if (domainNeutralType == null) {
////			throw new IllegalStateException("Neutral type is null");
////		}
////		String genericModelType = mapSimpleTypeConversion.get(domainNeutralType.getName());
////		if (genericModelType == null) {
////			throw new IllegalStateException("Unknown type '" + domainNeutralType.getName() +"'");
////		}		
////		return genericModelType;
//		return getJavaTypeClass(domainNeutralType).getSimpleName();
//	}

//	/**
//	 * Convert DSL types (string, integer, etc.) to Generic model types.
//	 * @param domainNeutralType
//	 * @return Generic model type
//	 */
//	private String convertNeutralTypeToFullType(DomainNeutralType domainNeutralType) {
////		if(domainNeutralType == null) {
////			//return defaultValue;
////			throw new IllegalStateException("Neutral type is null");
////		}
////		String genericModelType = mapFullTypeConversion.get(domainNeutralType.getName());
////		return genericModelType;
//		return getJavaTypeClass(domainNeutralType).getCanonicalName();
//	}

//	/**
//	 * Convert String value
//	 * @param value String value
//	 * @param defaultValue Default value
//	 * @return Value for Generic Model
//	 */
//	private String notNullOrVoidValue(String value, String defaultValue) {
//		if(isDefined(value)) {
//			return value;
//		}
//		return defaultValue;
//	}
	
	/**
	 * Conversion rule to determine the table name for a given entity
	 * @param domainEntity
	 * @return
	 */
	private String determineTableName(DomainEntity domainEntity) {
		if ( domainEntity == null ) {
			throw new IllegalStateException("DomainEntity is null");
		}
		if ( domainEntity.getName() == null ) {
			throw new IllegalStateException("DomainEntity name is null");
		}
		return domainEntity.getName();
	}
	
	private String notNull(String value) {
		if ( value == null ) {
			throw new IllegalStateException("Unexpected null value");
		}
		return value;
	}

	/**
	 * Returns a void string if the given value is null
	 * @param value
	 * @return
	 */
	private String voidIfNull(String value) {
		if ( value == null ) {
			return "";
		}
		return value;
	}

//	/**
//	 * Convert String value to Integer
//	 * @param value String value
//	 * @param defaultValue Default Integer value
//	 * @return Integer value
//	 */
//	private Integer convertStringToInteger(String value, Integer defaultValue) {
//		if(!isDefined(value)) {
//			return defaultValue;
//		}
//		Integer integerValue;
//		try {
//			integerValue = Integer.valueOf(value);
//		} catch(NumberFormatException e) {
//			integerValue = defaultValue;
//		}
//		return integerValue;
//	}
	
//	/**
//	 * Check if the String value is defined
//	 * @param value String value
//	 * @return is defined
//	 */
//	private boolean isDefined(String value) {
//		if(value == null) {
//			return false;
//		}
//		if(value.trim().equals(EMPTY_STRING)) {
//			return false;
//		}
//		return true;
//	}
	
}
