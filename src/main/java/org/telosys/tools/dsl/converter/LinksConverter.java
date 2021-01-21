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

import java.util.List;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainType;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Cardinality;
import org.telosys.tools.generic.model.CascadeOptions;
import org.telosys.tools.generic.model.FetchType;
import org.telosys.tools.generic.model.JoinColumn;
import org.telosys.tools.generic.model.Optional;

public class LinksConverter extends AbstractConverter {

	private final DomainModel domainModel;
	private final DslModel    dslModel;
	
	private int linkIdCounter = 0;

	/**
	 * Constructor
	 * @param domainModel
	 * @param dslModel
	 */
	public LinksConverter(DomainModel domainModel, DslModel dslModel) {
		super();
		this.domainModel = domainModel;
		this.dslModel = dslModel;
	}

	/**
	 * Create all the 'links' for the given entity
	 * @param domainEntity
	 * @param dslEntity
	 */
	public void createLinks(DomainEntity domainEntity, DslModelEntity dslEntity) {
		log("createLinks()...");
		if (domainEntity.getFields() == null) {
			return;
		}
		for (DomainField domainEntityField : domainEntity.getFields()) {
			// If this field references an entity then it's a LINK
			if (domainEntityField.getType().isEntity()) { 
				log("createLinks() : " + domainEntityField.getName() + " : entity type (link)");
				linkIdCounter++;
				DslModelLink link = convertAttributeLink(dslEntity, domainEntityField);
				// Add the new link to the entity
				dslEntity.getLinks().add(link);
			}
		}
	}

	private DslModelLink convertAttributeLink(DslModelEntity dslEntity, DomainField domainEntityField) {

		DomainType domainFieldType = domainEntityField.getType();
		check(domainFieldType.isEntity(), "Invalid field type. Entity type expected");

		DomainEntity domainEntityTarget = domainModel.getEntity(domainFieldType.getName());

		// Check target existence
		check((domainEntityTarget != null),
				"No target entity for field '" + domainEntityField.getName() + "'. Cannot create Link");

		DslModelLink dslLink = new DslModelLink();

		// Link ID : generated (just to ensure not null )
		dslLink.setId("Link" + linkIdCounter); 

		dslLink.setFieldName(domainEntityField.getName());

		dslLink.setSourceTableName(dslEntity.getDatabaseTable()); 

		// Set target entity info
		dslLink.setTargetEntityClassName(domainEntityField.getType().getName());
		dslLink.setTargetTableName(notNull(domainEntityTarget.getDatabaseTable())); // v 3.3.0

//		// --- Cardinality
//		Cardinality cardinality;
//		if (domainEntityField.getCardinality() == 1) {
//			cardinality = Cardinality.MANY_TO_ONE;
//		} else {
//			cardinality = Cardinality.ONE_TO_MANY;
//		}
//		dslLink.setCardinality(cardinality);

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
		

		// Apply annotations usable for link ( @Embedded @Optional @FetchTypeLazy @FetchTypeEager etc ) 
		if (domainEntityField.getAnnotations() != null) {
			LinksAnnotationsProcessor annotationsConverter = new LinksAnnotationsProcessor();
			annotationsConverter.applyAnnotationsForLink(dslEntity, dslLink, domainEntityField.getAnnotations().values());
		}
		
		// Join columns already determined from annotations @LinkByFK or @LinkByCol ?
		if ( ! dslLink.hasJoinColumns() ) {
			// No join columns defined by annotations => try to infer join columns from FK
			String referencedTableName = dslLink.getTargetTableName(); 
			List<JoinColumn> joinColumns = JoinColumnsUtil.tryToInferJoinColumns(dslEntity, referencedTableName);
			if ( joinColumns != null ) {
				dslLink.setJoinColumns(joinColumns);
			}
		}
		checkJoinColumns(dslLink);
		return dslLink;
	}	
	
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
