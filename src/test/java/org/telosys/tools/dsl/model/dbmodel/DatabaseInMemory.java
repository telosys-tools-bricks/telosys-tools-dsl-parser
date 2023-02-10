package org.telosys.tools.dsl.model.dbmodel;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseConnectionProvider;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitions;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitionsLoader;
import org.telosys.tools.commons.exception.TelosysYamlException;
import org.telosys.tools.commons.jdbc.SqlScriptRunner;

public class DatabaseInMemory {
	
	private final DatabaseDefinition databaseDefinition ;
	private Connection connection = null ;
		
	/**
	 * @param databaseDefinition
	 * @throws TelosysToolsException
	 */
	public DatabaseInMemory(DatabaseDefinition databaseDefinition) throws TelosysToolsException {
		super();
		this.databaseDefinition = databaseDefinition;
		// Get a connection to the database
		DatabaseConnectionProvider databaseConnectionProvider = new DatabaseConnectionProvider(); 
		this.connection = databaseConnectionProvider.getConnection(databaseDefinition);
	}

	public DatabaseInMemory(TelosysToolsCfg telosysToolsCfg, String databaseId) throws TelosysToolsException {
		super();
		this.databaseDefinition = getDatabaseDefinition(telosysToolsCfg, databaseId);
		// Get a connection to the database
		DatabaseConnectionProvider databaseConnectionProvider = new DatabaseConnectionProvider(); 
		this.connection = databaseConnectionProvider.getConnection(databaseDefinition);
	}
	
	protected DatabaseDefinition getDatabaseDefinition(TelosysToolsCfg telosysToolsCfg, String databaseId) throws TelosysToolsException {
		// Get database definitions file 
		File dbDefinitionsFile = new File(telosysToolsCfg.getDatabasesDbCfgFileAbsolutePath());
		// Load databases definitions
		DatabaseDefinitionsLoader loader = new DatabaseDefinitionsLoader();
		DatabaseDefinitions databaseDefinitions;
		try {
			databaseDefinitions = loader.load(dbDefinitionsFile);
		} catch (TelosysYamlException e) {
			throw new TelosysToolsException("Cannot load databases definitions (YAML error)");
		}
		DatabaseDefinition databaseDefinition = databaseDefinitions.getDatabaseDefinition(databaseId);
		if ( databaseDefinition != null ) {
			return databaseDefinition;
		}
		else {
			throw new TelosysToolsException("Unknown database '" + databaseId + "'");
		}
	}

	public void close() {
		log("DatabaseInMemory : close()....");
		if ( connection != null ) {
			try {
				if ( ! connection.isClosed() ) {
					connection.close();
					connection = null ;
					log("DatabaseInMemory : closed.");
					return;
				}
			}
			catch ( SQLException ex) {
				throw new RuntimeException("SQLException : " + ex.getMessage());
			}
		}
		throw new RuntimeException("Connection null or already closed ");
	}
	
//	private DatabaseConfiguration getDatabaseConfigurations(int dbId) throws TelosysToolsException {
//		DbConfigManager dbConfigManager = new DbConfigManager( FileUtil.getFileByClassPath(DBCFG_FILE) );
//		DatabasesConfigurations databasesConfigurations = dbConfigManager.load();
//		return databasesConfigurations.getDatabaseConfiguration(dbId);
//	}
	
	private void log(String s) {
		System.out.println("[LOG] " + s );
	}
	
	/**
	 * Execute the given SQL script file using the current connection <br>
	 * The current connection is not closed
	 * 
	 * @param sqlFile
	 * @throws TelosysToolsException
	 */
	public void executeSqlScript(File sqlFile) {
		log("EXECUTE SQL SCRIPT : " + sqlFile );
		Connection conn = this.connection ;
		
		SqlScriptRunner scriptRunner = new SqlScriptRunner(conn);
		log("Script file : " + sqlFile );
		
		try {
			log("Running SQL script ...");
			scriptRunner.runScript(sqlFile);
			log("SQL script executed.");
		} catch (Exception e) {
			throw new RuntimeException("SQL script error " + e.getMessage(), e);
		} 
		// Do not close connection here
	}

	public void executeSqlFile(String sqlScriptFile) {
		String sqlFileName = "/sql/" + sqlScriptFile ;
		File sqlFile = FileUtil.getFileByClassPath(sqlFileName);
		executeSqlScript(sqlFile);
	}
	
	public void executeSqlInit(int sqlScriptId) {
		String sqlFileName = "/sql/initdb" + sqlScriptId + ".sql";
		File sqlFile = FileUtil.getFileByClassPath(sqlFileName);
		executeSqlScript(sqlFile);
	}
	
	public void executeSqlAlter(int sqlScriptId) {
		String sqlFileName = "/sql/alterdb" + sqlScriptId + ".sql";
		File sqlFile = FileUtil.getFileByClassPath(sqlFileName);
		executeSqlScript(sqlFile);
	}
	
	//=====================================================================================================
	public DatabaseDefinition getDatabaseDefinition() {
		return this.databaseDefinition ;
	}	
	public Connection getCurrentConnection() {
		return this.connection;
	}
}
