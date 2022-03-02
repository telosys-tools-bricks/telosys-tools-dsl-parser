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

import org.telosys.tools.commons.NamingStyleConverter;
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

	/**
	 * Create links from foreign keys for all entities
	 * @param model
	 * @return
	 */
	public int createLinks(DslModel model) {
		int count = 0 ;
		for ( Entity entity : model.getEntities() ) {
			count = count + createLinks(model, (DslModelEntity) entity);
		}
		return count ;
	}

	/**
	 * Create links from foreign keys for the given entity
	 * @param model
	 * @param entity
	 * @return
	 */
	protected int createLinks(DslModel model, DslModelEntity entity) {
		int count = 0 ;
		for ( ForeignKey fk : entity.getForeignKeys() ) {
			count = count + createLinks(model, entity, (DslModelForeignKey) fk);
		}
		return count ;
	}

	protected int createLinks(DslModel model, DslModelEntity entity, DslModelForeignKey fk) {
		createLinkManyToOne(entity, fk);
		createLinkOneToMany(model, fk);
		return 1;
	}

	/**
	 * Creates a ManyToOne owning side link for the given FK
	 * @param entity
	 * @param fk
	 */
	protected void createLinkManyToOne(DslModelEntity entity, DslModelForeignKey fk) {
		String referencedEntityName = fk.getReferencedEntityName();
		String fieldName = buildFieldNameManyToOne(referencedEntityName, entity);
		// create link
		DslModelLink link = new DslModelLink(fieldName);
		link.setReferencedEntityName(referencedEntityName);
		link.setCardinality(Cardinality.MANY_TO_ONE);
		link.setForeignKeyName(fk.getName());
		link.setBasedOnForeignKey(true);
		link.setOwningSide(true);
		link.setInverseSide(false);
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
	 * @param entity
	 * @param fk
	 */
//	protected void createLinkOneToMany(DslModel model, DslModelEntity entity, DslModelForeignKey fk) {
	protected void createLinkOneToMany(DslModel model, DslModelForeignKey fk) {
		String referencedEntityName = fk.getReferencedEntityName();
		DslModelEntity referencedEntity = (DslModelEntity) model.getEntityByClassName(fk.getReferencedEntityName());
		if ( referencedEntity == null ) {
			throw new IllegalStateException("FK "+fk.getName()+ ": invalid referenced entity " + referencedEntityName);
		}
		String originEntityName = fk.getOriginEntityName();
		String fieldName = buildFieldNameOneToMany(originEntityName, referencedEntity);
		// create link
		DslModelLink link = new DslModelLink(fieldName);
		link.setReferencedEntityName(originEntityName);
		link.setCardinality(Cardinality.ONE_TO_MANY);
		link.setOwningSide(false);
		link.setInverseSide(true);
		// add link in entity
		referencedEntity.addLink(link);
	}

	private String buildFieldNameOneToMany(String originEntityName, DslModelEntity entity) {
		// entity "Person" --ref--> Other entity => inverse side field = "personList"
		String basicFieldName = nameConverter.toCamelCase(originEntityName)+"List";
		return getNonDuplicateFieldName(basicFieldName, entity) ; 
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
		if ( entity.getAttributeByName(fieldName) != null ) {
			return true;
		}
		if ( entity.getLinkByFieldName(fieldName) != null ) {
			return true;
		}
		return false;
	}
}
