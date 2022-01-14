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

import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.generic.model.ForeignKey;

/**
 * Utility class to convert the "raw model" (built by DSL parser) to the standard "generic model"
 * 
 * @author L. Guerin
 *
 */
public class EntityConverter extends AbstractConverter {

	private final DslModel dslModel ;
	private final DslModelErrors errors;
	
	private final TagsConverter tagsConverter;
	
	/**
	 * Constructor
	 * @param dslModel
	 * @param errors
	 */
	public EntityConverter(DslModel dslModel, DslModelErrors errors) {
		super();
		this.dslModel = dslModel;
		this.errors = errors;
		this.tagsConverter = new TagsConverter(errors);
	}
	
	protected DslModelEntity convertEntity(DomainEntity domainEntity) {
		String entityName = notNull(domainEntity.getName());
		
		// New entity 
		DslModelEntity dslEntity = new DslModelEntity(entityName);
		
		// Init entity state
		step1InitEntity(dslEntity, domainEntity);

		// Apply annotations if any
		step2ApplyAnnotations(dslEntity, domainEntity);
		
		// Apply tags if any
		step3ApplyTags(dslEntity, domainEntity);
		
		// Finalize attribute state
		step4FinalizeEntity(dslEntity);
		
		return dslEntity;
	}
	
	/**
	 * Init the given entity
	 * @param dslEntity
	 * @param domainEntity
	 */
	private void step1InitEntity(DslModelEntity dslEntity, DomainEntity domainEntity) {

		dslEntity.setFullName(notNull(domainEntity.getName()));
		
		//--- init database information 
//		dslEntity.setDatabaseTable(domainEntity.getDatabaseTable()); // v 3.3.0
		dslEntity.setDatabaseTable(notNull(domainEntity.getName())); // v 3.4.0
		
//		dslEntity.setDatabaseType("TABLE");  // Type is "TABLE" by default
//		dslEntity.setTableType(true);  // Type is "TABLE" by default
//		dslEntity.setViewType(false);  // Type is "TABLE" by default
//		dslEntity.setDatabaseCatalog("");
//		dslEntity.setDatabaseSchema("");
//		dslEntity.setDatabaseTablespace(xx);
//		dslEntity.setTableType(true);
//		dslEntity.setViewType(false);
		
		// No Foreign Keys => void list
		dslEntity.setDatabaseForeignKeys(new LinkedList<ForeignKey>()); 
	}
	
	/**
	 * Apply annotations to the given entity
	 * @param dslEntity
	 * @param domainField
	 */
	private void step2ApplyAnnotations(DslModelEntity dslEntity, DomainEntity domainEntity) {
		if (domainEntity.getAnnotations() != null) {
			log("Converter : annotations found");
			Collection<DomainAnnotation> annotations = domainEntity.getAnnotations().values();
			for (DomainAnnotation annotation : annotations) {
				try {
					annotation.applyToEntity(dslModel, dslEntity);
				} catch (Exception e) {
					errors.addError(
							new DslModelError( dslEntity.getClassName(), e.getMessage() ) );
				}
			}		
			
		} else {
			log("Converter : no annotation");
		}
	}
	
	/**
	 * Apply tags to the given entity
	 * @param dslEntity
	 * @param domainEntity
	 */
	private void step3ApplyTags(DslModelEntity dslEntity, DomainEntity domainEntity) {
		tagsConverter.applyTagsToEntity(dslEntity, domainEntity);  // new in v 3.4.0
	}
	
	/**
	 * Finalize the given entity
	 * @param dslEntity
	 */
	private void step4FinalizeEntity(DslModelEntity dslEntity) {	
	}
}
