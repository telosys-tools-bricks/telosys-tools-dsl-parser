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

import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

/**
 * Utility class to convert the "raw model" (built by DSL parser) to the standard "generic model"
 * 
 * @author L. Guerin
 *
 */
public class ModelConverter extends AbstractConverter {

	private final DslModelErrors  errors;

	/**
	 * Constructor
	 */
	public ModelConverter(DslModelErrors errors) {
		super();
		this.errors = errors;
	}

	public DslModelErrors getErrors() {
		return errors;
	}
	
	/**
	 * Re-throw the given exception by adding a prefix to the message 
	 * @param e
	 * @param messagePrefix
	 */
	private void rethrowException(Exception e, String messagePrefix) {
		String msg = e.getMessage();
		if ( msg == null ) { // eg NullPointerException
			msg = e.toString();
		}
		String newMessage = messagePrefix + msg ;
		throw new RuntimeException(newMessage, e);
	}

	/**
	 * Converts PARSER MODEL to DSL/Generic model <br>
	 * 
	 * @param domainModel
	 *            paser domain model
	 * @return DSL/Generic model
	 * @throws IllegalStateException
	 *             if an error occurs
	 */
	public Model convertModel(DomainModel domainModel) {

		// Create a new void DSL model 
		DslModel dslModel = new DslModel(domainModel.getModelName(), domainModel.getModelInfo()); // v 3.4.0

		// Create void entities (without attribute)
		step1CreateAllVoidEntities(domainModel, dslModel);

		// Create attributes : fields with basic neutral type (apply annotations and tags) 
		step2CreateAllAttributes(domainModel, dslModel);
		
		// Create explicit Foreign Keys defined in attributes ( with @FK(xx) annotation )
		step3CreateAllExplicitForeignKeys(domainModel, dslModel);
		
		// Create links : fields referencing entities (apply annotations and tags) 
		// Keep it AFTER FK creation (to be able to found Foreign Keys)
		step4CreateAllLinks(domainModel, dslModel); 
		
		// Create implicit Foreign Keys defined in links ( with @LinkByAttr(xx) annotation )
		// Keep it AFTER LINKS creation (to be able to found Link attributes)
		step5CreateAllImplicitForeignKeys(dslModel); 

		// Sort all entities by class name
		dslModel.sortEntitiesByClassName();

		// Finally check model
		step6CheckModel(dslModel);
		
		return dslModel;
	}

	/**
	 * Creates and returns a DSL model containing only void entities (without attributes or links)
	 * @param domainModel
	 * @param dslModel
	 */
	protected void step1CreateAllVoidEntities(DomainModel domainModel, DslModel dslModel) {
		// v 3.4.0
		EntityConverter entityConverter = new EntityConverter(dslModel, errors);
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			DslModelEntity dslEntity = entityConverter.convertEntity(domainEntity);
			dslModel.addEntity(dslEntity); 
		}
	}
	
	/**
	 * @param domainModel
	 * @param dslModel
	 */
	protected void step2CreateAllAttributes(DomainModel domainModel, DslModel dslModel) {
		AttributesConverter attribConverter = new AttributesConverter(dslModel, errors);
		// for each "DomainEntity" convert attributes 
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			String entityName = domainEntity.getName();
			try {
				// Get the GenericEntity built previously
				DslModelEntity genericEntity = (DslModelEntity) dslModel.getEntityByClassName(domainEntity.getName());
				// Convert all attributes to "basic type" 
				// or "void pseudo FK attribute" (to keep the initial attributes order)
				attribConverter.convertAttributes(domainEntity, genericEntity);
			}
			catch(Exception e) {
				rethrowException(e, "Entity " + entityName + " : ");
			}			
		}
	}

	/**
	 * @param domainModel
	 * @param dslModel
	 */
	protected void step4CreateAllLinks(DomainModel domainModel, DslModel dslModel) {

		LinksConverter linksConverter = new LinksConverter(dslModel, errors);
		
		// Create the links 
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			String entityName = domainEntity.getName();
			try {
				// Get the GenericEntity built previously
				DslModelEntity genericEntity = (DslModelEntity) dslModel.getEntityByClassName(entityName);
				// Creates a link for each field referencing an entity
				linksConverter.convertLinks(domainEntity, genericEntity);
			}
			catch(Exception e) {
				rethrowException(e,"Entity " + entityName + " : ");
			}
		}
	}
	
	/**
	 * Create all Foreign Keys declared with '@FK' annotations at field level
	 * @param domainModel
	 * @param dslModel
	 */
	protected void step3CreateAllExplicitForeignKeys(DomainModel domainModel, DslModel dslModel) {
		ForeignKeysBuilderV2 fkBuilder = new ForeignKeysBuilderV2(dslModel);
		// for each entity in the model
		for (DomainEntity entity : domainModel.getEntities()) {
			String entityName = entity.getName();
			try {
				fkBuilder.buildForeignKeys(entity);
			}
			catch(Exception e) {
				rethrowException(e,"Entity " + entityName + " : ");
			}
		}
	}

	/**
	 * Creates all implicit Foreign Keys (if any) for the given model 
	 * @param dslModel
	 */
	protected void step5CreateAllImplicitForeignKeys(DslModel dslModel) {
		ForeignKeysBuilderV2 fkBuilder = new ForeignKeysBuilderV2(dslModel);
		// for each entity in the model
		for ( Entity entity : dslModel.getEntities() ) {
			DslModelEntity dslModelEntity = (DslModelEntity)entity;
			fkBuilder.buildImplicitForeignKeys(dslModelEntity);
		}
	}

	/**
	 * Check model consistency
	 * @param dslModel
	 */
	protected void step6CheckModel(DslModel dslModel) {
		// Add consistency checking here
	}
}
