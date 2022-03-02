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

import java.sql.Connection;
import java.sql.SQLException;

import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.TelosysToolsLogger;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DbConfigManager;
import org.telosys.tools.commons.dbcfg.DbConnectionManager;
import org.telosys.tools.db.model.DatabaseModelManager;
import org.telosys.tools.db.model.DatabaseTables;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.writer.ModelWriter;
import org.telosys.tools.generic.model.ForeignKeyPart;

/**
 * DB-MODEL to DSL-MODEL manager
 * 
 * @author Laurent GUERIN
 * 
 */

public class DbToModelManager {
	
	private final TelosysToolsCfg    telosysToolsCfg ;
	private final TelosysToolsLogger logger ;

	public DbToModelManager(TelosysToolsCfg telosysToolsCfg, TelosysToolsLogger logger) {
		super();
		this.telosysToolsCfg = telosysToolsCfg ;
		this.logger = logger;
	}
	
	public DslModel createModelFromDatabase(int databaseId, String modelName) throws TelosysToolsException {

		// STEP 1 : init model from database (in memory) 
		DslModel model = initModelFromDatabase(databaseId, modelName);
		// STEP 2 : write model in the model folder
		String modelDirectory = telosysToolsCfg.getModelFolderAbsolutePath(modelName);
		ModelWriter modelWriter = new ModelWriter();
		modelWriter.writeModel(model, modelDirectory);
		// return the model
		return model ;
	}
	
	protected DslModel initModelFromDatabase(int databaseId, String modelName) throws TelosysToolsException {
		
		// Get database configuration 
		DbConfigManager dbConfigManager = new DbConfigManager(telosysToolsCfg); 
		DatabaseConfiguration databaseConfiguration = dbConfigManager.load().getDatabaseConfiguration(databaseId);
		if (databaseConfiguration == null) {
			throw new TelosysToolsException("No configuration for database "+databaseId);
		}
		
		// Get a connection to the database
		DbConnectionManager dbConnectionManager = new DbConnectionManager(telosysToolsCfg);
		Connection connection = dbConnectionManager.getConnection(databaseId);
		
		//--- STEP 1 : Create the model (Entities, Attributes with Foreign Keys )
		DslModel model;
		try {
			model = createModelFromDatabase(modelName, connection, databaseConfiguration);
		} finally { // v 3.0.0 (finally added for connection closing)
			closeConnection(connection); 
		}
		
		//--- STEP 2 : Create the links between entities (based on FK)
		LinksBuilder linksBuilder = new LinksBuilder();
		linksBuilder.createLinks(model);
		
		return model ;
	}

//	/**
//	 * Returns a connection using the given DatabaseConfiguration <br>
//	 * and the ConnectionManager initialized in the constructor
//	 * 
//	 * @param databaseConfiguration
//	 * @return
//	 * @throws TelosysToolsException
//	 */
//	protected Connection getConnection(int databaseId) throws TelosysToolsException {
//		if ( this.dbConnectionManager != null ) {
//			return this.dbConnectionManager.getConnection( databaseId );
//		}
//		else {
//			throw new TelosysToolsException("Cannot get DB connection (no connection manager)");
//		}
//	}

	protected void closeConnection(Connection connection) throws TelosysToolsException {
		if ( connection != null ) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new TelosysToolsException("Cannot close DB connection (SQLException)", e);
			}
		}
	}
	
	//private RepositoryModel generateRepository(Connection con, DatabaseConfiguration databaseConfig) throws TelosysToolsException 
	private DslModel createModelFromDatabase(String modelName, Connection con, DatabaseConfiguration databaseConfig) throws TelosysToolsException {
		
		// Load all tables (DB-Model)
		DatabaseTables dbTables = getDatabaseTablesFromDb(con, databaseConfig);
		
		// Convert DB-Model to DSL-Model
		DbToModelConverter modelConverter = new DbToModelConverter(logger);
		ModelInfo modelInfo = new ModelInfo();
		modelInfo.setTitle("Model created from database " + databaseConfig.getDatabaseName() );

		DslModel model = modelConverter.createModel(modelName, modelInfo, dbTables);
		
		// Add additional information in model
		model.setDatabaseId(databaseConfig.getDatabaseId());
		model.setDatabaseProductName(databaseConfig.getTypeName());
		
		return model;
	}

	private DatabaseTables getDatabaseTablesFromDb(Connection con, DatabaseConfiguration databaseConfig) throws TelosysToolsException {
		
		String tableNamePattern = databaseConfig.getMetadataTableNamePattern();
		if (tableNamePattern == null) {
			tableNamePattern = "%";
		}

		logger.log("   ... Metadata parameters : ");
		logger.log("   ... * Catalog = " + databaseConfig.getMetadataCatalog());
		logger.log("   ... * Schema  = " + databaseConfig.getMetadataSchema());
		logger.log("   ... * Table Name Pattern  = " + tableNamePattern);
		StringBuilder sb = new StringBuilder();
		for (String s : databaseConfig.getMetadataTableTypesArray() ) {
			sb.append("[" + s + "] ");
		}
		logger.log("   ... * Table Types Array  = " + sb.toString());

		//--- Load the Database Model
		DatabaseModelManager manager = new DatabaseModelManager();
		try {
			return manager.getDatabaseTables(con, 
					databaseConfig.getMetadataCatalog(), 
					databaseConfig.getMetadataSchema(), 
					tableNamePattern, 
					databaseConfig.getMetadataTableTypesArray(), 
					databaseConfig.getMetadataTableNameInclude(), 
					databaseConfig.getMetadataTableNameExclude());
		} catch (SQLException e) {
			throw new TelosysToolsException("Cannot get DB-Model (SQLException)", e);
		}
	}
	
}
