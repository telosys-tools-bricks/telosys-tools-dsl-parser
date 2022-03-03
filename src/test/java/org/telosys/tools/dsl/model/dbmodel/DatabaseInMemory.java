package org.telosys.tools.dsl.model.dbmodel;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;
import org.telosys.tools.commons.dbcfg.DatabasesConfigurations;
import org.telosys.tools.commons.dbcfg.DbConfigManager;
import org.telosys.tools.commons.jdbc.ConnectionManager;
import org.telosys.tools.commons.jdbc.SqlScriptRunner;

public class DatabaseInMemory {
	
	private static final String DBCFG_FILE = "/dbcfg/databases-test-H2.dbcfg" ;
	
//	private final int databaseId ;
	private final DatabaseConfiguration databaseConfiguration ;
	private Connection connection = null ;
	
//	/**
//	 * Constructor for default database
//	 * @throws TelosysToolsException
//	 */
//	public DatabaseInMemory() throws TelosysToolsException {
//		super();
//		DatabasesConfigurations databasesConfigurations = getDatabaseConfigurations();
////		this.databaseId = databasesConfigurations.getDatabaseDefaultId() ;
//		this.databaseConfiguration = databasesConfigurations.getDatabaseConfiguration() ;
//		ConnectionManager connectionManager = new ConnectionManager();
//		this.connection = connectionManager.getConnection(databaseConfiguration);
//	}
	
//	/**
//	 * Constructor for a specific database id
//	 * @param databaseId
//	 * @throws TelosysToolsException
//	 */
//	public DatabaseInMemory(int databaseId) throws TelosysToolsException {
//		super();
//		DatabasesConfigurations databasesConfigurations = getDatabaseConfigurations();
////		this.databaseId = databaseId;
//		this.databaseConfiguration = databasesConfigurations.getDatabaseConfiguration(databaseId);
//		ConnectionManager connectionManager = new ConnectionManager();
//		this.connection = connectionManager.getConnection(databaseConfiguration);
//	}
	
	public DatabaseInMemory(DatabaseConfiguration databaseConfiguration) throws TelosysToolsException {
		super();
		this.databaseConfiguration = databaseConfiguration;
		ConnectionManager connectionManager = new ConnectionManager();
		this.connection = connectionManager.getConnection(databaseConfiguration);
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
	
	private DatabaseConfiguration getDatabaseConfigurations(int dbId) throws TelosysToolsException {
		DbConfigManager dbConfigManager = new DbConfigManager( FileUtil.getFileByClassPath(DBCFG_FILE) );
		DatabasesConfigurations databasesConfigurations = dbConfigManager.load();
		return databasesConfigurations.getDatabaseConfiguration(dbId);
	}
	
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
//	public int getDatabaseId() {
//		return this.databaseId;
//	}
	
	public DatabaseConfiguration getDatabaseConfiguration() {
		return this.databaseConfiguration ;
	}
	
	public Connection getCurrentConnection() {
		return this.connection;
	}
	
}
