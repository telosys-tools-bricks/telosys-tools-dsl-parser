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

import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.db.model.DatabaseTable;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.dsl.commons.AttributeFKUtil;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.ForeignKey;

/**
 * DB-MODEL to DSL-MODEL : Model converter
 * 
 * @author Laurent GUERIN
 * 
 */
public class DbToModelConverter {
	
	private static final String SEPARATOR = "   --------------------------------------------------------------";
	
	private final TelosysToolsLogger  logger ;

	private final DbToEntityConverter  entityConverter  = new DbToEntityConverter();

	/**
	 * Constructor
	 * @param logger
	 */
	public DbToModelConverter(TelosysToolsLogger logger) {
		this.logger = logger;
	}

	/**
	 * Create a DSL model from the given database tables metadata
	 * @param modelName
	 * @param modelInfo
	 * @param dbTables
	 * @param databaseDefinition
	 * @return
	 */
	public DslModel createModel(String modelName, ModelInfo modelInfo, DatabaseTables dbTables, DatabaseDefinition databaseDefinition) {
		
		DslModel model = new DslModel(modelName, modelInfo); 
		
		//--- For each table add an Entity in the repository
		int tablesCount = 0;
		for ( DatabaseTable dbTable : dbTables ) {
			tablesCount++;
			logger.log(SEPARATOR);
			logger.log("   Table '" + dbTable.getTableName() 
					+ "' ( catalog = '" + dbTable.getCatalogName() 
					+ "', schema = '"+ dbTable.getSchemaName() + "' )");
			//--- Create a new entity from the database table
			DslModelEntity entity = entityConverter.createEntity(dbTable, databaseDefinition);
			//--- Add the entity in the model
			model.addEntity(entity);
			logger.log("   --> Entity '" + entity.getClassName() + "'" );
		}
		logger.log(SEPARATOR);
		logger.log("   " + tablesCount + " table(s) converted.");
		logger.log(SEPARATOR);

		applyFkToAttributes(model);
		
		return model ;
	}
	
	/**
	 * Apply all model Foreign Keys to attributes involved in it 
	 * @param model
	 */
	private void applyFkToAttributes(DslModel model) {
		for ( Entity entity : model.getEntities() ) {
			for ( ForeignKey fk : entity.getForeignKeys() ) {
				AttributeFKUtil.applyFKToAttributes(fk, model);
			}
		}
	}	
}
