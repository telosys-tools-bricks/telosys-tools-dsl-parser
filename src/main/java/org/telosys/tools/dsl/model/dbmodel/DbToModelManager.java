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

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseConnectionProvider;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitions;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitionsLoader;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.writer.ModelWriter;

/**
 * DATABASE-SCHEMA to DSL-MODEL manager
 * 
 * @author Laurent GUERIN
 * 
 */

public class DbToModelManager {
	
	private final TelosysToolsCfg    telosysToolsCfg ;
	private final TelosysToolsLogger logger ;

	/**
	 * Constructor
	 * @param telosysToolsCfg
	 * @param logger
	 */
	public DbToModelManager(TelosysToolsCfg telosysToolsCfg, TelosysToolsLogger logger) {
		super();
		if ( telosysToolsCfg == null ) {
			throw new IllegalArgumentException("TelosysToolsCfg is null");
		}
		if ( logger == null ) {
			throw new IllegalArgumentException("TelosysToolsLogger is null");
		}
		this.telosysToolsCfg = telosysToolsCfg ;
		this.logger = logger;
	}
	
	/**
	 * Creates a new DSL model from the given database
	 * @param databaseId
	 * @param modelName
	 * @return
	 * @throws TelosysToolsException
	 */
	public DslModel createModelFromDatabase(String databaseId, String modelName) throws TelosysToolsException {

		// STEP 1 : init model from database (in memory) 
		DslModel model = initModelFromDatabase(databaseId, modelName);
		// STEP 2 : write model in the model folder
		String modelDirectory = telosysToolsCfg.getModelFolderAbsolutePath(modelName);
		ModelWriter modelWriter = new ModelWriter();
		modelWriter.writeModel(model, modelDirectory);
		// return the model
		return model ;
	}
	
	protected DslModel initModelFromDatabase(String databaseId, String modelName) throws TelosysToolsException {
		
		DatabaseDefinition databaseDefinition = getDatabaseDefinition(databaseId);
		
		Connection connection = openConnection(databaseDefinition);
		
		//--- STEP 1 : Create the model (Entities, Attributes with Foreign Keys )
		DslModel model;
		try {
			model = createModelFromDatabase(modelName, connection, databaseDefinition);
		} finally { // v 3.0.0 (finally added for connection closing)
			closeConnection(connection); 
		}
		
		//--- STEP 2 : Create the links between entities (based on FK)
		LinksBuilder linksBuilder = new LinksBuilder(databaseDefinition);
		linksBuilder.createLinks(model);
		
		return model ;
	}

	protected DatabaseDefinition getDatabaseDefinition(String databaseId) throws TelosysToolsException {
		// Get database definitions file 
		File dbDefinitionsFile = new File(telosysToolsCfg.getDatabasesDbCfgFileAbsolutePath());
		// Load databases definitions
		DatabaseDefinitionsLoader loader = new DatabaseDefinitionsLoader();
		DatabaseDefinitions databaseDefinitions = loader.load(dbDefinitionsFile);
		DatabaseDefinition databaseDefinition = databaseDefinitions.getDatabaseDefinition(databaseId);
		if ( databaseDefinition != null ) {
			return databaseDefinition;
		}
		else {
			throw new TelosysToolsException("Unknown database '" + databaseId + "'");
		}
	}
	
	protected Connection openConnection(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		// Get a connection to the database
		DatabaseConnectionProvider databaseConnectionProvider = new DatabaseConnectionProvider(telosysToolsCfg); 
		return databaseConnectionProvider.getConnection(databaseDefinition);
	}
	
	protected void closeConnection(Connection connection) throws TelosysToolsException {
		if ( connection != null ) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new TelosysToolsException("Cannot close DB connection (SQLException)", e);
			}
		}
	}
	
	private DslModel createModelFromDatabase(String modelName, Connection con, DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		
		// Load all tables (DB-Model)
		DatabaseTables dbTables = getDatabaseTablesFromDb(con, databaseDefinition);
		
		// Convert DB-Model to DSL-Model
		DbToModelConverter modelConverter = new DbToModelConverter(logger);
		ModelInfo modelInfo = new ModelInfo();
		modelInfo.setTitle("Model created from database " + databaseDefinition.getName() );

		DslModel model = modelConverter.createModel(modelName, modelInfo, dbTables);
		
		// Add additional information in model
		model.setDatabaseId(databaseDefinition.getId());
		model.setDatabaseName(databaseDefinition.getName());
		model.setDatabaseType(databaseDefinition.getType());
		
		return model;
	}

	private DatabaseTables getDatabaseTablesFromDb(Connection con, DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		
		String tableNamePattern = databaseDefinition.getTableNamePattern();
		if ( StrUtil.nullOrVoid(tableNamePattern) ) {
			// Not set => use "%" by default
			tableNamePattern = "%";
		}

		StringBuilder sb = new StringBuilder();
		for (String s : databaseDefinition.getTableTypesArray() ) {
			sb.append("[" + s + "] ");
		}
		logger.log("   Metadata parameters : ");
		logger.log("    . Catalog = " + databaseDefinition.getCatalog());
		logger.log("    . Schema  = " + databaseDefinition.getSchema());
		logger.log("    . Table Name Pattern = " + tableNamePattern);
		logger.log("    . Table Types Array  = " + sb.toString());
		logger.log("    . Table Name Include = " + databaseDefinition.getTableNameInclude());
		logger.log("    . Table Name Exclude = " + databaseDefinition.getTableNameExclude());

		//--- Load the Database Model
		DatabaseModelManager manager = new DatabaseModelManager();
		try {
			return manager.getDatabaseTables(con, 
					databaseDefinition.getCatalog(), 
					databaseDefinition.getSchema(), 
					tableNamePattern, 
					databaseDefinition.getTableTypesArray(),
					databaseDefinition.getTableNameInclude(), 
					databaseDefinition.getTableNameExclude());
		} catch (SQLException e) {
			throw new TelosysToolsException("Cannot get DB-Model (SQLException)", e);
		}
	}
	
}
