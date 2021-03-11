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

import java.util.LinkedList;

import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.Model;

/**
 * Utility class to convert the "raw model" (built by DSL parser) to the standard "generic model"
 * 
 * @author L. Guerin
 *
 */
public class Converter extends AbstractConverter {

	/**
	 * Constructor
	 */
	public Converter() {
		super();
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
	public Model convertToGenericModel(DomainModel domainModel) {

		// Create a new void DSL model 
		// DslModel dslModel = new DslModel();
		DslModel dslModel = new DslModel(domainModel.getModelNameFromFile()); // v 3.3.0
		dslModel.setTitle(voidIfNull(domainModel.getTitle()));
		dslModel.setDescription(voidIfNull(domainModel.getDescription()));

		// Create void entities (without attribute)
		createAllVoidEntities(domainModel, dslModel);

		// Create attributes (only basic fields with neutral type) 
		createAllAttributes(domainModel, dslModel);
		
		// Create Foreign Keys defined in attributes ( with @FK(xx) annotation )
		createAllForeignKeys(domainModel, dslModel); // Added in v 3.3
		
		// Create links (fields referencing entities) 
		createAllLinks(domainModel, dslModel); // Keep at the end (to be able to found Foreign Keys)
		
		// Finally sort the entities by class name
		dslModel.sortEntitiesByClassName();

		return dslModel;
	}

	/**
	 * Creates and returns a DSL model containing only void entities (without attributes or links)
	 * @param domainModel
	 * @param dslModel
	 */
	protected void createAllVoidEntities(DomainModel domainModel, DslModel dslModel) {
		// for each "DomainEntity" create a void "DslModelEntity"
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			DslModelEntity genericEntity = createVoidEntity(domainEntity);
			dslModel.getEntities().add(genericEntity);
		}
	}
	
	/**
	 * @param domainModel
	 * @param dslModel
	 */
	protected void createAllAttributes(DomainModel domainModel, DslModel dslModel) {
		AttribConverter attribConverter = new AttribConverter();
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
				rethrowException(e,"Entity " + entityName + " : ");
			}			
		}
	}

	/**
	 * @param domainModel
	 * @param dslModel
	 */
	protected void createAllLinks(DomainModel domainModel, DslModel dslModel) {

		LinksConverter linksConverter = new LinksConverter(dslModel);
		
		// Create the links 
		for (DomainEntity domainEntity : domainModel.getEntities()) {
			String entityName = domainEntity.getName();
			try {
				// Get the GenericEntity built previously
				DslModelEntity genericEntity = (DslModelEntity) dslModel.getEntityByClassName(entityName);
				// Creates a link for each field referencing an entity
				linksConverter.createLinks(domainEntity, genericEntity);
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
	protected void createAllForeignKeys(DomainModel domainModel, DslModel dslModel) {
		ForeignKeysBuilder fkBuilder = new ForeignKeysBuilder(dslModel);
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
	 * Creates a void entity (without attribute) <br>
	 * initialized with only class name and default database information 
	 * @param domainEntity
	 * @return
	 */
	private DslModelEntity createVoidEntity(DomainEntity domainEntity) {
		log("convertEntity(" + domainEntity.getName() + ")...");
		
		DslModelEntity dslEntity = new DslModelEntity( notNull(domainEntity.getName()) ); // v 3.3.0
		
		dslEntity.setFullName(notNull(domainEntity.getName()));

		//--- init database information 
		dslEntity.setDatabaseTable(domainEntity.getDatabaseTable()); // v 3.3.0
		// Type is "TABLE" by default
		dslEntity.setDatabaseType("TABLE"); 
		dslEntity.setDatabaseCatalog("");
		dslEntity.setDatabaseSchema("");
		// No Foreign Keys => void list
		dslEntity.setDatabaseForeignKeys(new LinkedList<ForeignKey>()); 

		return dslEntity;
	}

}
