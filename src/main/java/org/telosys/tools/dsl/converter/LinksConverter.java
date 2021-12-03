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

import org.telosys.tools.dsl.commons.JoinColumnsBuilder;
import org.telosys.tools.dsl.converter.link.JoinColumnsUtil;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.Optional;

public class LinksConverter extends AbstractConverter {

	private final DslModel    dslModel;
	
	private int linkIdCounter = 0;

	private final AnnotationsApplicator annotationsApplicator ;

	private final TagsConverter tagsConverter;
	
	
	/**
	 * Constructor
	 * @param dslModel
	 */
	public LinksConverter(DslModel dslModel) {
		super();
		this.dslModel = dslModel;
		this.annotationsApplicator = new AnnotationsApplicator(dslModel);
		this.tagsConverter = new TagsConverter();
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
				linkIdCounter++;
				// create a new link
				DslModelLink dslLink = createLink(dslEntity, domainField);
				// init link default values
				step1InitLink(dslLink, domainField);
				// apply link annotations
				step2ApplyAnnotations(dslEntity, dslLink, domainField);
				// apply tags : TODO
				step3ApplyTags(dslLink, domainField); 
				// try to infer undefined join columns
				step4InferJoinColumns(dslEntity, dslLink);

				// Add the new link to the entity
				dslEntity.getLinks().add(dslLink);

				//DslModelLink link = convertAttributeLink(dslEntity, domainField);
				// Add the new link to the entity
				//dslEntity.getLinks().add(link);
			}
		}
	}
	
	private DslModelLink createLink(DslModelEntity dslEntity, DomainField domainField) {
		Entity referencedEntity = dslModel.getEntityByClassName(domainField.getType().getName());
		
		check((referencedEntity != null),
				"No target entity for field '" + domainField.getName() + "'. Cannot create Link");

		DslModelLink dslLink = new DslModelLink();
		// Init the new attribute with at least its name
		dslLink.setFieldName(notNull(domainField.getName()));

		// Link ID : generated (just to ensure not null )
		dslLink.setId("Link" + linkIdCounter); 

		dslLink.setSourceTableName(notNull(dslEntity.getDatabaseTable())); 
		
		// Set target entity info
		dslLink.setTargetEntityClassName(domainField.getType().getName());
		dslLink.setTargetTableName(notNull(referencedEntity.getDatabaseTable())); // v 3.3.0

		return dslLink;
	}
	
	private void step1InitLink(DslModelLink dslLink, DomainField domainEntityField) {

		// Cardinality and owning/inverse side
		if (domainEntityField.getCardinality() == 1) {
			// Reference to only ONE entity => "MANY TO ONE"
			dslLink.setCardinality(Cardinality.MANY_TO_ONE);
			// The type is a single entity => OWNING SIDE by default
			dslLink.setOwningSide(true);
			dslLink.setInverseSide(false);
			dslLink.setInverseSideLinkId(null);
		} else {
			// Reference to MANY entities => "ONE TO MANY"
			dslLink.setCardinality(Cardinality.ONE_TO_MANY);
			// The type is a collection of entity => INVERSE SIDE by default
			dslLink.setOwningSide(false);
			dslLink.setInverseSide(true);
			dslLink.setInverseSideLinkId(null);
		}
		
		// void "cascade options"  (default values)
		dslLink.setCascadeOptions(new CascadeOptions()); 

		// Link based on FK : @LinkByFK or implicit FK
		dslLink.setBasedOnForeignKey(false);
		dslLink.setForeignKeyName("");
		
		dslLink.setComparableString("");
		dslLink.setFetchType(FetchType.DEFAULT); // Default value set after by annotation 
		dslLink.setOptional(Optional.UNDEFINED); // Default value set after by annotation 
		dslLink.setMappedBy(null); // Default value set after by annotation

		dslLink.setJoinColumns(null);
		
		// For "MANY TO MANY" --> No "JOIN TABLE" in DSL ?
		dslLink.setBasedOnJoinTable(false);
		dslLink.setJoinTable(null);
		dslLink.setJoinTableName(null);
	}
	
	private void step2ApplyAnnotations(DslModelEntity dslEntity, DslModelLink dslLink, DomainField domainField) {
		log(dslLink.getFieldName() + " : apply annotations" );
		// Apply annotations usable for link ( @Embedded @Optional @FetchTypeLazy @FetchTypeEager etc ) 
		if (domainField.getAnnotations() != null) {
			log(domainField.getAnnotations().size() + " annotation(s) found");
//			LinksAnnotationsProcessor annotationsConverter = new LinksAnnotationsProcessor(this.dslModel);
//			annotationsConverter.applyAnnotationsForLink(dslEntity, dslLink, domainEntityField.getAnnotations().values());
			Collection<DomainAnnotation> fieldAnnotations = domainField.getAnnotations().values();
			annotationsApplicator.applyAnnotationsToLink(dslEntity, dslLink, fieldAnnotations);
		} else {
			log("no annotation");
		}
	}
	
	/**
	 * Apply tags to the given attribute
	 * @param dslAttribute
	 * @param domainField
	 */
	private void step3ApplyTags(DslModelLink dslLink, DomainField domainField) {
		// TODO
		// tagsConverter.applyTags(dslLink, domainField);
	}
	
	/**
	 * Try to infer join columns not defined by annotations
	 * @param dslEntity
	 * @param dslLink
	 */
	private void step4InferJoinColumns(DslModelEntity dslEntity, DslModelLink dslLink) {
		// Join columns not already determined from annotations @LinkByFK or @LinkByCol ?
		if ( ! dslLink.hasJoinColumns() && dslLink.isOwningSide() ) {
			// No join columns defined by annotations => try to infer join columns from FK
			String referencedTableName = dslLink.getTargetTableName(); 
			JoinColumnsBuilder jcb = new JoinColumnsBuilder("Infer Join Columns");
			List<JoinColumn> joinColumns = jcb.tryToInferJoinColumns(dslEntity, referencedTableName);
			if ( joinColumns != null ) {
				dslLink.setJoinColumns(joinColumns);
			}
		}
		checkJoinColumns(dslLink);
	}

//	private DslModelLink convertAttributeLink(DslModelEntity dslEntity, DomainField domainEntityField) {

//		DomainType domainFieldType = domainEntityField.getType();
//		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");
//		Entity referencedEntity = dslModel.getEntityByClassName(domainFieldType.getName());

//		Entity referencedEntity = dslModel.getEntityByClassName(domainEntityField.getType().getName());
//		
//		check((referencedEntity != null),
//				"No target entity for field '" + domainEntityField.getName() + "'. Cannot create Link");
//
//		DslModelLink dslLink = new DslModelLink();

//--------------------------------------------------------------
		
//		// Link ID : generated (just to ensure not null )
//		dslLink.setId("Link" + linkIdCounter); 
//
//		dslLink.setFieldName(domainEntityField.getName());
//
//		dslLink.setSourceTableName(dslEntity.getDatabaseTable()); 
//
//		// Set target entity info
//		dslLink.setTargetEntityClassName(domainEntityField.getType().getName());
//		dslLink.setTargetTableName(notNull(referencedEntity.getDatabaseTable())); // v 3.3.0
//
//		// Cardinality and owning/inverse side
//		if (domainEntityField.getCardinality() == 1) {
//			// Reference to only ONE entity => "MANY TO ONE"
//			dslLink.setCardinality(Cardinality.MANY_TO_ONE);
//			// The type is a single entity => OWNING SIDE by default
//			dslLink.setOwningSide(true);
//			dslLink.setInverseSide(false);
//			dslLink.setInverseSideLinkId(null);
//		} else {
//			// Reference to MANY entities => "ONE TO MANY"
//			dslLink.setCardinality(Cardinality.ONE_TO_MANY);
//			// The type is a collection of entity => INVERSE SIDE by default
//			dslLink.setOwningSide(false);
//			dslLink.setInverseSide(true);
//			dslLink.setInverseSideLinkId(null);
//		}
//		
//		// void "cascade options"  (default values)
//		dslLink.setCascadeOptions(new CascadeOptions()); 
//
//		// Link based on FK : @LinkByFK or implicit FK
//		dslLink.setBasedOnForeignKey(false);
//		dslLink.setForeignKeyName("");
//		
//		dslLink.setComparableString("");
//		dslLink.setFetchType(FetchType.DEFAULT); // Default value set after by annotation 
//		dslLink.setOptional(Optional.UNDEFINED); // Default value set after by annotation 
//		dslLink.setMappedBy(null); // Default value set after by annotation
//
//		dslLink.setJoinColumns(null);
//		
//		// For "MANY TO MANY" --> No "JOIN TABLE" in DSL ?
//		dslLink.setBasedOnJoinTable(false);
//		dslLink.setJoinTable(null);
//		dslLink.setJoinTableName(null);
		

//		// Apply annotations usable for link ( @Embedded @Optional @FetchTypeLazy @FetchTypeEager etc ) 
//		if (domainEntityField.getAnnotations() != null) {
//			LinksAnnotationsProcessor annotationsConverter = new LinksAnnotationsProcessor(this.dslModel);
//			annotationsConverter.applyAnnotationsForLink(dslEntity, dslLink, domainEntityField.getAnnotations().values());
//		}
		
//		// Join columns not already determined from annotations @LinkByFK or @LinkByCol ?
//		if ( ! dslLink.hasJoinColumns() && dslLink.isOwningSide() ) {
//			// No join columns defined by annotations => try to infer join columns from FK
//			String referencedTableName = dslLink.getTargetTableName(); 
//			JoinColumnsBuilder jcb = new JoinColumnsBuilder("Infer Join Columns");
//			List<JoinColumn> joinColumns = jcb.tryToInferJoinColumns(dslEntity, referencedTableName);
//			if ( joinColumns != null ) {
//				dslLink.setJoinColumns(joinColumns);
//			}
//		}
//		checkJoinColumns(dslLink);
//		return dslLink;
//	}	
	
	private void checkJoinColumns(DslModelLink dslLink) {
		if ( dslLink.getJoinColumns() != null ) {
			//--- Check number of columns expected
			int nbJoinColumns = dslLink.getJoinColumns().size() ;
			String targetEntityName = dslLink.getTargetEntityClassName();
			int nbKeyAttributesExpected = getNbKeyAttributes(targetEntityName);
			if ( nbJoinColumns != nbKeyAttributesExpected ) {
				throw new IllegalStateException("Link error : " + nbJoinColumns + " join columns, "
						+ nbKeyAttributesExpected + " expected for a reference to '" + targetEntityName + "' entity ");
			}
			//--- Check duplicates
			int n = JoinColumnsUtil.numberOfDuplicates(dslLink.getJoinColumns());
			if ( n > 0 ) {
				throw new IllegalStateException("Link error : "
						+ n + " duplicated join column(s)");
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
