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

import org.telosys.tools.db.model.DatabaseColumn;
import org.telosys.tools.db.model.DatabaseForeignKey;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelForeignKey;

/**
 * DB-MODEL to DSL-MODEL : Entity converter
 * 
 * @author Laurent GUERIN
 * 
 */

public class DbToEntityConverter {
	
	private final DbToAttributeConverter  attributeConverter  = new DbToAttributeConverter();
	private final DbToForeignKeyConverter foreignKeyConverter = new DbToForeignKeyConverter();

	/**
	 * Creates an new entity from the given database table 
	 * @param dbTable
	 * @return
	 */
//	protected EntityInDbModel addEntity(RepositoryModel repositoryModel, DatabaseTable dbTable)
	public DslModelEntity createEntity(DatabaseTable dbTable) {

		//--- Get the VO Bean class name from the Table Name
//		String beanClassName = repositoryRules.getEntityClassName(entity.getDatabaseTable() );
		String entityName = NameConverter.tableNameToEntityName(dbTable.getTableName());

		//--- Create Entity from the Database TABLE
//		EntityInDbModel entity = new EntityInDbModel();
		DslModelEntity entity = new DslModelEntity(entityName);
		entity.setDatabaseTable( dbTable.getTableName() );

//		entity.setClassName(beanClassName);		
		entity.setDatabaseCatalog( dbTable.getCatalogName() ); 
		entity.setDatabaseSchema( dbTable.getSchemaName() ); 
		entity.setDatabaseComment( dbTable.getComment() );
//		entity.setDatabaseType( dbTable.getTableType() ) ; 
		if ( "VIEW".equalsIgnoreCase(dbTable.getTableType()) ) {
			entity.setDatabaseView(true);
		}
		
		//--- Create attributes from table columns
		createAttributes(entity, dbTable) ;
				
		//--- Create Foreign Keys defined in the table
		createForeignKeys(entity, dbTable);
		
		return entity ;
	}
	
	private void createAttributes(DslModelEntity entity, DatabaseTable dbTable) {
		//--- For each column of the table ...
		for ( DatabaseColumn dbCol : dbTable.getColumns() ) {
			// Create a new attribute from the database model
			DslModelAttribute attribute = attributeConverter.createAttribute(dbCol);
			// Attach the attribute to the entity
			entity.addAttribute(attribute);
		}
	}
	
	private void createForeignKeys(DslModelEntity entity, DatabaseTable dbTable) {
		//--- For each foreign key of the table ...
		for ( DatabaseForeignKey dbFK : dbTable.getForeignKeys() ) {
			// Create a new FK from the database model
			DslModelForeignKey fk = foreignKeyConverter.createForeignKey(dbFK);
			// Attach the FK to the entity
			entity.addForeignKey(fk);
		}
	}

}