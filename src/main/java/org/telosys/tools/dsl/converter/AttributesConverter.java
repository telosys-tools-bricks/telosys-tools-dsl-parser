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

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;

/**
 * Utility class to convert the "raw model" (built by DSL parser) to the standard "generic model"
 * 
 * @author L. Guerin
 *
 */
public class AttributesConverter extends AbstractConverter {

	private final DslModel dslModel ;
	private final DslModelErrors errors;
	
	private final TagsConverter tagsConverter;
	
	/**
	 * Constructor
	 * @param dslModel
	 * @param errors
	 */
	public AttributesConverter(DslModel dslModel, DslModelErrors errors) {
		super();
		this.dslModel = dslModel;
		this.errors = errors;
		this.tagsConverter = new TagsConverter(errors);
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
				DslModelAttribute dslAttribute = convertAttribute(domainField, dslEntity);
				// Add the new "basic attribute" to the entity
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
		step3ApplyTags(dslEntity, dslAttribute, domainField);
		
		// Finalize attribute state
		step4FinalizeAttribute(dslAttribute);
		
		return dslAttribute;
	}
	
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
		// v 3.4.0 : no default DB name
		// dslAttribute.setDatabaseName(domainField.getName()); 

		// By default the label is the attribute name 
		// it will be overridden by @Label(xxx) if any
		// v 3.4.0 : no default label
		// dslAttribute.setLabel(domainField.getName());
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
			Collection<DomainAnnotation> annotations = domainField.getAnnotations().values();
			for (DomainAnnotation annotation : annotations) {
				try {
					annotation.applyToAttribute(dslModel, dslEntity, dslAttribute);
				} catch (Exception e) {
					errors.addError(
							new DslModelError( dslEntity.getClassName(), dslAttribute.getName(), e.getMessage() ) );
				}
			}		
			
		} else {
			log("Converter : no annotation");
		}
	}
	
	/**
	 * Apply tags to the given attribute
	 * @param dslEntity
	 * @param dslAttribute
	 * @param domainField
	 */
	private void step3ApplyTags(DslModelEntity dslEntity, DslModelAttribute dslAttribute, DomainField domainField) {
		tagsConverter.applyTagsToAttribute(dslEntity, dslAttribute, domainField);
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
