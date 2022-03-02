package org.telosys.tools.dsl.model.dbmodel;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.TelosysToolsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DatabaseInMemoryTest { 

	protected static final int DEFAULT_DATABASE_ID  = 1 ;

	private void printSeparator(String s) {
		System.out.println("========== " + s );
	}
	
	@Test
	public void testExecSqlscript() throws TelosysToolsException, SQLException {
		printSeparator("testExecSqlscript");
		
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
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
		final int sqlScriptId = 1 ;
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
		Connection conn = databaseInMemory.getCurrentConnection(); 
		assertNotNull( conn );
		assertFalse( conn.isClosed() );
			
		databaseInMemory.executeSqlInit(sqlScriptId); // CREATE MODEL : VERSION 1

		databaseInMemory.executeSqlAlter(sqlScriptId); // CHANGE MODEL : VERSION 2
		
		databaseInMemory.close();
	}

	@Test
	public void test2() throws TelosysToolsException {
		
		printSeparator("test2");
		final int sqlScriptId = 2 ;

		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
		assertEquals(DEFAULT_DATABASE_ID, databaseInMemory.getDatabaseId() );
		
		databaseInMemory.executeSqlInit(sqlScriptId); // CREATE MODEL : VERSION 1

		databaseInMemory.executeSqlAlter(sqlScriptId); // CHANGE MODEL : VERSION 2

		databaseInMemory.close();
	}

	@Test
	public void test5() throws TelosysToolsException {
		printSeparator("test5");
		System.out.println();
		int scriptId = 5;
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
		
		databaseInMemory.executeSqlInit(scriptId);
		
		databaseInMemory.close();
	}
}
