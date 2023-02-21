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
package org.telosys.tools.dsl.model.dbmodel;

import java.util.List;

import org.telosys.tools.commons.NamingStyleConverter;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKey;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.enums.Cardinality;

/**
 * DB-MODEL to DSL-MODEL : Links builder
 * 
 * @author Laurent GUERIN
 * 
 */

public class LinksBuilder {
	
	private final NamingStyleConverter nameConverter = new NamingStyleConverter();
	
	private final DatabaseDefinition databaseDefinition;

	/**
	 * Constructor
	 * @param databaseDefinition
	 */
	public LinksBuilder(DatabaseDefinition databaseDefinition) {
		super();
		this.databaseDefinition = databaseDefinition;
	}

	/**
	 * Create links from foreign keys for all entities
	 * @param model
	 * @return
	 */
	public void createLinks(DslModel model) {
		for ( Entity entity : model.getEntities() ) {
			if ( databaseDefinition.isLinksManyToMany()) {
				// Special processing for "join entities" 
				if ( entity.isJoinEntity() ) {
					createManyToManyLinks(model, (DslModelEntity) entity);
				}
				else {
					createBasicLinks(model, (DslModelEntity) entity);
				}
			}
			else {
				// Just create ManyToOne and OneToMany for each FK for all entities
				createBasicLinks(model, (DslModelEntity) entity);
			}
		}
	}

	/**
	 * Create links for each Foreign Key in the given entity (ManyToOne and inverse OneToMany)
	 * @param model
	 * @param entity
	 * @return
	 */
	protected void createBasicLinks(DslModel model, DslModelEntity entity) {
		for ( ForeignKey fk0 : entity.getForeignKeys() ) {
			DslModelForeignKey fk = (DslModelForeignKey) fk0;
			if ( databaseDefinition.isLinksManyToOne() ) {
				createLinkManyToOne(entity, fk);
			}
			if ( databaseDefinition.isLinksOneToMany() ) {
				createLinkOneToMany(model, fk);
			}
		}
	}

	/**
	 * Creates a ManyToOne owning side link for the given FK
	 * @param entity
	 * @param fk
	 */
	private void createLinkManyToOne(DslModelEntity entity, DslModelForeignKey fk) {
		String referencedEntityName = fk.getReferencedEntityName();
		String fieldName = buildFieldNameManyToOne(referencedEntityName, entity);
		// create link
		DslModelLink link = new DslModelLink(fieldName);
		link.setReferencedEntityName(referencedEntityName);
		link.setCardinality(Cardinality.MANY_TO_ONE);
		link.setForeignKeyName(fk.getName());
		link.setBasedOnForeignKey(true);
		// link.setOwningSide(true); // removed in v 4.1.0
		// link.setInverseSide(false); // removed in v 4.1.0
		// add link in entity
		entity.addLink(link);
	}
	private String buildFieldNameManyToOne(String referencedEntityName, DslModelEntity entity) {	
		// ref entity "Person" --> field "person"
		String basicFieldName = nameConverter.toCamelCase(referencedEntityName);
		return getNonDuplicateFieldName(basicFieldName, entity) ; 
	}

	/**
	 * Creates a OneToMany inverse side link for the given FK
	 * @param model
	 * @param fk
	 */
	private void createLinkOneToMany(DslModel model, DslModelForeignKey fk) {
		DslModelEntity referencedEntity = getReferencedEntity(model, fk);
		String originEntityName = fk.getOriginEntityName();
		String fieldName = buildCollectionFieldName(referencedEntity, originEntityName);		
		// create link
		DslModelLink link = new DslModelLink(fieldName);
		link.setReferencedEntityName(originEntityName);
		link.setCardinality(Cardinality.ONE_TO_MANY);
		// link.setOwningSide(false); // removed in v 4.1.0
		// link.setInverseSide(true); // removed in v 4.1.0
		// add link in entity
		referencedEntity.addLink(link);
	}

	private String getNonDuplicateFieldName(String basicFieldName, DslModelEntity entity) {
		String fieldName = basicFieldName ;
		int n = 1;
		while ( fieldExistsInEntity(fieldName, entity) ) {
			n++;
			fieldName = basicFieldName + n;
		}
		return fieldName;
	}
	
	private boolean fieldExistsInEntity(String fieldName, DslModelEntity entity) {
		return ( entity.getAttributeByName(fieldName) != null ) || ( entity.getLinkByFieldName(fieldName) != null ) ;
	}
	
	/**
	 * Creates 2 ManyToMany links, one in each entity referenced by the given join entity
	 * @param model
	 * @param joinEntity
	 */
	protected void createManyToManyLinks(DslModel model, DslModelEntity joinEntity) {
		String joinEntityName = joinEntity.getClassName();
		List<ForeignKey> foreignKeys = joinEntity.getForeignKeys();
		if ( foreignKeys.size() == 2 ) {
			DslModelForeignKey fk1 = (DslModelForeignKey) foreignKeys.get(0);
			DslModelForeignKey fk2 = (DslModelForeignKey) foreignKeys.get(1);
			DslModelEntity referencedEntity1 = getReferencedEntity(model, fk1);
			DslModelEntity referencedEntity2 = getReferencedEntity(model, fk2);
			createLinkManyToMany(referencedEntity1, referencedEntity2.getClassName(), joinEntityName) ;
			createLinkManyToMany(referencedEntity2, referencedEntity1.getClassName(), joinEntityName) ;
		}
	}
	
	/**
	 * Creates a ManyToMany link in the given entity 
	 * @param entity
	 * @param referencedEntityName
	 * @param joinEntityName
	 */
	private void createLinkManyToMany(DslModelEntity entity, String referencedEntityName, String joinEntityName) {
		String linkFieldName = buildCollectionFieldName(entity, referencedEntityName);
		// create link
		DslModelLink link = new DslModelLink(linkFieldName);
		link.setReferencedEntityName(referencedEntityName);
		link.setCardinality(Cardinality.MANY_TO_MANY);
		// based on a "join entity"
		link.setJoinEntityName(joinEntityName);
		link.setBasedOnJoinEntity(true);
		// no owning or inverse side for this link
		// link.setOwningSide(false); // no matter // removed in v 4.1.0
		// link.setInverseSide(false); // no matter // removed in v 4.1.0
		// add link in entity
		entity.addLink(link);
	}
	
	private DslModelEntity getReferencedEntity(DslModel model, DslModelForeignKey fk) {
		String referencedEntityName = fk.getReferencedEntityName();
		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(referencedEntityName);
		if ( referencedEntity == null ) {
			throw new IllegalStateException("FK "+fk.getName()+ ": invalid referenced entity " + referencedEntityName);
		}
		return referencedEntity;
	}
	
	/**
	 * Build the link field name for a collection
	 * @param entity the entity in which the field will be added
	 * @param entityInCollection eg "Person"
	 * @return the collection name, eg "personList"
	 */
	private String buildCollectionFieldName(DslModelEntity entity, String entityInCollection) {
		// entity "Person" --ref--> Other entity => inverse side field = "personList"
		String basicFieldName = nameConverter.toCamelCase(entityInCollection)+"List";
		return getNonDuplicateFieldName(basicFieldName, entity) ; 
	}
}
