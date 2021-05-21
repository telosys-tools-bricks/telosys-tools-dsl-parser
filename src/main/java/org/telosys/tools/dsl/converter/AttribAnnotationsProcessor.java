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

import org.telosys.tools.commons.logger.ConsoleLogger;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.generic.model.BooleanValue;

public class AttribAnnotationsProcessor {

	private static final ConsoleLogger logger = new ConsoleLogger();

	private final String entityName ;

	/**
	 * Constructor
	 */
	public AttribAnnotationsProcessor(String entityName) {
		super();
		this.entityName = entityName;
	}

	private void log(String msg) {
		if (ConverterLogStatus.LOG) {
			logger.log(this, msg);
		}
	}	

	/**
	 * Apply annotations for a basic attribute with a neutral type (string, int, etc)
	 * @param attribute
	 * @param annotations
	 */
	public void applyAnnotationsForNeutralType(DslModelAttribute attribute, Collection<DomainAnnotation> annotations) {
		for (DomainAnnotation annotation : annotations) {
			log("Converter : annotation '" + annotation.getName() + "'");
			// The annotation name is like "Id", "NotNull", "Max", etc
			// without "@" at the beginning and without "#" at the end
			if (AnnotationName.ID.equals(annotation.getName())) {
				log("Converter : annotation @Id");
				attribute.setKeyElement(true);
				// If "@Id" => "@NotNull"
				attribute.setNotNull(true);
			}
			if (AnnotationName.AUTO_INCREMENTED.equals(annotation.getName())) {
				log("Converter : annotation @AutoIncremented");
				attribute.setAutoIncremented(true);
			}
		}		
		applyAnnotationsAboutValue(attribute, annotations);
		applyAnnotationsAboutType(attribute, annotations);
		applyAnnotationsAboutDatabase(attribute, annotations);
		applyAnnotationsWithStringParameter(attribute, annotations);
		// Foreign Keys annotations @FK are processed in another step (after)
	}

//	/**
//	 * Apply annotations for a "Pseudo Foreign Key" attribute 
//	 * @param attribute
//	 * @param annotations
//	 */
//	public void applyAnnotationsForPseudoForeignKey(DslModelAttribute attribute, Collection<DomainAnnotation> annotations) {
//		applyAnnotationsAboutValue(attribute, annotations);
//		applyAnnotationsAboutType(attribute, annotations);
//		applyAnnotationsAboutDatabase(attribute, annotations);
//		applyAnnotationsWithStringParameter(attribute, annotations);
//	}
	
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
		}
	}

	private void applyAnnotationsAboutDatabase(DslModelAttribute genericAttribute,
			Collection<DomainAnnotation> fieldAnnotations) {
		final String msg = "Converter : annotations for databse : @" ;
		boolean fieldNotNull = false;
		Integer fieldSizeMax = null; // not set by default
		for (DomainAnnotation annotation : fieldAnnotations) {

			// Database annotations ( @DbXxxx ) :
			if (AnnotationName.DB_SIZE.equals(annotation.getName())) {
				log(msg + AnnotationName.DB_SIZE );
				genericAttribute.setDatabaseSize(annotation.getParameterAsString());
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
			if (AnnotationName.TRANSIENT.equals(annotation.getName())) { // Added in ver 3.3
				log(msg + AnnotationName.TRANSIENT );
				genericAttribute.setTransient(true);
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
			
			if (AnnotationName.INSERTABLE.equals(annotation.getName())) { // Added in ver 3.3
				processInsertable(this.entityName, genericAttribute, annotation);
			}
			if (AnnotationName.UPDATABLE.equals(annotation.getName())) { // Added in ver 3.3
				processUpdatable(this.entityName, genericAttribute, annotation);
			}

		}
		// Complete with other field annotations
		if ( genericAttribute.getDatabaseSize() == null && fieldSizeMax != null ) {
			genericAttribute.setDatabaseSize(""+fieldSizeMax);
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
				genericAttribute.setPattern(annotation.getParameterAsString());
			}

			// TODO : @After(DateISO/TimeISO)
			// TODO : @Before(DateISO/TimeISO)
		}
	}
	
	/**
	 * Process '@Insertable(true|false)' annotation <br>
	 * @param entityName
	 * @param attribute
	 * @param annotation
	 */
	private void processInsertable(String entityName, DslModelAttribute attribute, DomainAnnotation annotation) {
		BooleanValue v = Util.getBooleanValue(entityName, attribute.getName(), annotation );
		attribute.setInsertable(v);
	}
	
	/**
	 * Process '@Updatable(true|false)' annotation <br>
	 * @param entityName
	 * @param attribute
	 * @param annotation
	 */
	private void processUpdatable(String entityName, DslModelAttribute attribute, DomainAnnotation annotation) {
		BooleanValue v = Util.getBooleanValue(entityName, attribute.getName(), annotation );
		attribute.setUpdatable(v);
	}
	

}
