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
import java.util.List;

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.converter.link.JoinAttributesUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.LinkAttribute;
import org.telosys.tools.generic.model.enums.Cardinality;
import org.telosys.tools.generic.model.enums.FetchType;
import org.telosys.tools.generic.model.enums.Optional;

public class LinksConverter extends AbstractConverter {

	private final DslModel       dslModel;
	private final DslModelErrors  errors;
	
	private final TagsConverter tagsConverter;
	
	/**
	 * Constructor
	 * @param dslModel
	 * @param errors
	 */
	public LinksConverter(DslModel dslModel, DslModelErrors errors) {
		super();
		this.dslModel = dslModel;
		this.errors = errors ;
		this.tagsConverter = new TagsConverter(errors);
	}

	/**
	 * Converts all the 'links' for the given entity
	 * @param domainEntity
	 * @param dslEntity
	 */
	public void convertLinks(DomainEntity domainEntity, DslModelEntity dslEntity) {
		log("convertLinks()...");
		if (domainEntity.getFields() == null) {
			return;
		}
		for (DomainField domainField : domainEntity.getFields()) {
			// If the field references an entity then it's a LINK
			if (domainField.getType().isEntity()) { 
				log("convert field : " + domainField.getName() + " (entity type => link)");
				// create a new link
				DslModelLink dslLink = createLink(domainField);
				
				// 1) init link default values
				step1InitLink(dslLink, domainField);
				// 2) apply annotations on the link
				step2ApplyAnnotations(dslEntity, dslLink, domainField);
				// 3) apply tags on the link
				step3ApplyTags(dslEntity, dslLink, domainField);				
				// 4) try to infer undefined join attributes
				step4InferJoinAttributes(dslEntity, dslLink);
				// 5) finalize the link
				step5FinalizeLink(dslLink);
				
				// Add the new link to the entity
				dslEntity.addLink(dslLink); // v 3.4.0
			}
		}
	}
	
	private DslModelLink createLink(DomainField domainField) {
		Entity referencedEntity = dslModel.getEntityByClassName(domainField.getType().getName());
		
		check((referencedEntity != null),
				"No target entity for field '" + domainField.getName() + "' "
				+ " : " + domainField.getType().getName() + " - Cannot create Link");

		DslModelLink dslLink = new DslModelLink(notNull(domainField.getName())); // v 3.4.0

		// v 3.4.0
		dslLink.setReferencedEntityName(domainField.getType().getName());

		return dslLink;
	}
	
	private void step1InitLink(DslModelLink dslLink, DomainField domainEntityField) {

		// Cardinality and owning/inverse side
		if (domainEntityField.getCardinality() == DomainCardinality.ONE ) {
			// Reference to only ONE entity => "MANY TO ONE"
			dslLink.setCardinality(Cardinality.MANY_TO_ONE);
			// The type is a single entity => OWNING SIDE by default
			dslLink.setOwningSide(true);
			dslLink.setInverseSide(false);
		} else {
			// Reference to MANY entities => "ONE TO MANY"
			dslLink.setCardinality(Cardinality.ONE_TO_MANY);
			// The type is a collection of entity => INVERSE SIDE by default
			dslLink.setOwningSide(false);
			dslLink.setInverseSide(true);
		}
		
		// void "cascade options"  (default values)
		dslLink.setCascadeOptions(new CascadeOptions()); 

		// Link based on FK : @LinkByFK or implicit FK
		dslLink.setBasedOnForeignKey(false);
		dslLink.setForeignKeyName("");
		
		dslLink.setFetchType(FetchType.DEFAULT); // Default value set after by annotation 
		dslLink.setOptional(Optional.UNDEFINED); // Default value set after by annotation 
		dslLink.setMappedBy(null); // Default value set after by annotation
		
		// For "MANY TO MANY" --> No "JOIN TABLE" in DSL ?
		dslLink.setBasedOnJoinEntity(false); // v 3.4.0

		dslLink.setJoinEntityName(null); // v 3.4.0
	}
	
	private void step2ApplyAnnotations(DslModelEntity dslEntity, DslModelLink dslLink, DomainField domainField) {
		log(dslLink.getFieldName() + " : apply annotations" );
		// Apply annotations usable for link ( @Embedded @Optional @FetchTypeLazy @FetchTypeEager etc ) 
		if (domainField.getAnnotations() != null) {
			log(domainField.getAnnotations().size() + " annotation(s) found");

			Collection<DomainAnnotation> annotations = domainField.getAnnotations().values();
			for (DomainAnnotation annotation : annotations) {
				try {
					annotation.applyToLink(dslModel, dslEntity, dslLink);
				} catch (Exception e) {
					errors.addError(
						new DslModelError( dslEntity.getClassName(), dslLink.getFieldName(), e.getMessage() ) );
				}
			}
		} else {
			log("no annotation");
		}
	}
	
	/**
	 * Apply tags to the given link
	 * @param dslEntity
	 * @param dslLink
	 * @param domainField
	 */
	private void step3ApplyTags(DslModelEntity dslEntity, DslModelLink dslLink, DomainField domainField) {
		tagsConverter.applyTagsToLink(dslEntity, dslLink, domainField); // new in v 3.4.0
	}
	
	/**
	 * Try to infer link attributes not defined by annotations
	 * @param dslEntity
	 * @param dslLink
	 */
	private void step4InferJoinAttributes(DslModelEntity dslEntity, DslModelLink dslLink) {
		// Join attributes not already determined from annotations @LinkByFK or @LinkByAttr ?
		if ( ! dslLink.hasAttributes() && dslLink.isOwningSide() ) {
			// No join columns defined by annotations => try to infer join columns from FK
			String referencedEntityName = dslLink.getReferencedEntityName();
			List<LinkAttribute> linkAttributes = JoinAttributesUtil.tryToInferJoinAttributes(dslEntity, referencedEntityName);
			if ( linkAttributes != null ) {
				dslLink.setAttributes(linkAttributes);
			}
		}
		checkJoinAttributes(dslLink); // v 3.4.0
	}
	
	private void step5FinalizeLink(DslModelLink dslLink) {
		// If link based on a Join Table => owning side
		if ( dslLink.isBasedOnJoinEntity() ) {
			dslLink.setOwningSide(true);
			dslLink.setInverseSide(false);
		}
		// If link is "mapped by" another entity => inverse side
		if ( dslLink.getMappedBy() != null ) {
			dslLink.setInverseSide(true);
			dslLink.setOwningSide(false);
		}
		// TODO : Check MappedBy validity AFTER all "apply"  (next step) (?)
		// check existence :
		// . referenced entity
		//     dslModel.getEntityByClassName(targetEntityName);
		// . owning side link existence in the target entity
		//     targetEntity.getLinkByFieldName(attributeName) 
		
	}
	
	private void checkJoinAttributes(DslModelLink dslLink) {
		if ( dslLink.getAttributes() != null ) {
			//--- Check number of attributes expected
			int nbJoinAttrib = dslLink.getAttributes().size() ;
			String referencedEntityName = dslLink.getReferencedEntityName();
			int nbKeyAttributesExpected = getNbKeyAttributes(referencedEntityName);
			if ( nbJoinAttrib != nbKeyAttributesExpected ) {
				throw new IllegalStateException("Link error : " + nbJoinAttrib + " join attribute(s), "
						+ nbKeyAttributesExpected + " expected for a reference to '" + referencedEntityName + "' entity ");
			}
			//--- Check duplicates
			int n = JoinAttributesUtil.numberOfDuplicates(dslLink.getAttributes());
			if ( n > 0 ) {
				throw new IllegalStateException("Link error : "
						+ n + " duplicated join attribute(s)");
			}
		}
	}
	
	private int getNbKeyAttributes(String entityClassName) {
		DslModelEntity e = (DslModelEntity) dslModel.getEntityByClassName(entityClassName);
		if ( e != null ) {
			List<Attribute> keyAttributes = e.getKeyAttributes();
			if ( keyAttributes != null ) {
				return keyAttributes.size();
			}
			else {
				return 0;
			}
		}
		else {
			throw new IllegalStateException("Link error : unknown entity '" + entityClassName + "'");
		}
	}
}
