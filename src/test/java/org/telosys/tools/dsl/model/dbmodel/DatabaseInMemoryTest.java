package org.telosys.tools.dsl.model.dbmodel;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinition;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitions;
import org.telosys.tools.commons.dbcfg.yaml.DatabaseDefinitionsLoader;
import org.telosys.tools.commons.exception.TelosysYamlException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DatabaseInMemoryTest { 

	protected static final String DATABASE_CFG_FILE  = "/myproject/TelosysTools/databases.yaml" ;
	protected static final String DATABASE_ID  = "db0" ;

	private void printSeparator(String s) {
		System.out.println("========== " + s );
	}
	
	private DatabaseDefinition getDatabaseDefinition() throws TelosysToolsException {
		// Load databases definitions
		DatabaseDefinitionsLoader loader = new DatabaseDefinitionsLoader();
		DatabaseDefinitions databaseDefinitions;
		try {
			databaseDefinitions = loader.load(FileUtil.getFileByClassPath(DATABASE_CFG_FILE));
		} catch (TelosysYamlException e) {
			throw new TelosysToolsException("Cannot load databases definitions (YAML error)");
		}
		DatabaseDefinition databaseDefinition = databaseDefinitions.getDatabaseDefinition(DATABASE_ID);
		if ( databaseDefinition != null ) {
			return databaseDefinition;
		}
		else {
			throw new TelosysToolsException("Unknown database '" + DATABASE_ID + "'");
		}
	}
	
	@Test
	public void testExecSqlscript() throws TelosysToolsException, SQLException {
		printSeparator("testExecSqlscript");
		
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseDefinition());
		Connection conn = databaseInMemory.getCurrentConnection(); 
		assertNotNull( conn );
		assertFalse( conn.isClosed() );
		databaseInMemory.executeSqlScript(FileUtil.getFileByClassPath("/sql/initdb1.sql"));
		databaseInMemory.executeSqlScript(FileUtil.getFileByClassPath("/sql/alterdb1.sql"));
		databaseInMemory.close();
	}

	@Test
	public void test1() throws TelosysToolsException, SQLException {
		
		printSeparator("test1");

		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseDefinition() );
		Connection conn = databaseInMemory.getCurrentConnection(); 
		assertNotNull( conn );
		assertFalse( conn.isClosed() );
			
		final int sqlScriptId = 1 ;

		databaseInMemory.executeSqlInit(sqlScriptId); // CREATE MODEL : VERSION 1

		databaseInMemory.executeSqlAlter(sqlScriptId); // CHANGE MODEL : VERSION 2
		
		databaseInMemory.close();
	}

	@Test
	public void test2() throws TelosysToolsException {
		
		printSeparator("test2");
		final int sqlScriptId = 2 ;

		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseDefinition() );
		
		databaseInMemory.executeSqlInit(sqlScriptId); // CREATE MODEL : VERSION 1

		databaseInMemory.executeSqlAlter(sqlScriptId); // CHANGE MODEL : VERSION 2

		databaseInMemory.close();
	}

	@Test
	public void test5() throws TelosysToolsException {
		printSeparator("test5");
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseDefinition() );
		
		databaseInMemory.executeSqlInit(5); // script 5
		
		databaseInMemory.close();
	}
}
