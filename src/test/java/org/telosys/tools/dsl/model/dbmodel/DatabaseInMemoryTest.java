package org.telosys.tools.dsl.model.dbmodel;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.dbcfg.DatabaseConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DatabaseInMemoryTest { 

	protected static final int    DATABASE_ID  = 0 ;
	protected static final String DATABASE_CFG_FILE  = "/myproject/TelosysTools/databases.dbcfg" ;

	private void printSeparator(String s) {
		System.out.println("========== " + s );
	}
	
	private DatabaseConfiguration getDatabaseConfiguration() throws TelosysToolsException {
		DbConfigUtil dbConfigUtil = new DbConfigUtil();
		DatabaseConfiguration cfg = dbConfigUtil.getDatabaseConfigurations(DATABASE_CFG_FILE, DATABASE_ID);
		if ( cfg == null) {
			throw new IllegalStateException("Cannot get db config #" + DATABASE_ID + " from " + DATABASE_CFG_FILE);
		}
		return cfg;
	}
	
	
	@Test
	public void testExecSqlscript() throws TelosysToolsException, SQLException {
		printSeparator("testExecSqlscript");
		
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseConfiguration());
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

		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseConfiguration() );
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

		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseConfiguration() );
		
		databaseInMemory.executeSqlInit(sqlScriptId); // CREATE MODEL : VERSION 1

		databaseInMemory.executeSqlAlter(sqlScriptId); // CHANGE MODEL : VERSION 2

		databaseInMemory.close();
	}

	@Test
	public void test5() throws TelosysToolsException {
		printSeparator("test5");
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(getDatabaseConfiguration() );
		
		databaseInMemory.executeSqlInit(5); // script 5
		
		databaseInMemory.close();
	}
}
