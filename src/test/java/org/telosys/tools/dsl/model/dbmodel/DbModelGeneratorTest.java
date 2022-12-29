package org.telosys.tools.dsl.model.dbmodel;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.logger.ConsoleLogger;
import org.telosys.tools.commons.logger.GlobalLoggingConfig;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.ForeignKey;
import org.telosys.tools.generic.model.ForeignKeyPart;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.enums.Cardinality;
import org.telosys.tools.junit.utils.PrintUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DbModelGeneratorTest {
	
	private static final String SRC_TEST_RESOURCES = "src/test/resources/" ;
	private static final String PROJECT_FOLDER = "myproject" ;

	private void printSeparator(String s) {
		System.out.println("========== " + s );
	}
	
	private TelosysToolsCfg getTelosysToolsCfg(String projectName) {
		File projectFolder = new File(SRC_TEST_RESOURCES + projectName);
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder.getAbsolutePath());
		return cfgManager.loadTelosysToolsCfg();
	}

	private DslModel generateRepositoryModel(String dbId, String sqlFile, String modelName) throws TelosysToolsException {
		
		System.out.println("Get TelosysToolsCfg from project '"+ PROJECT_FOLDER+"'");
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg(PROJECT_FOLDER);

		System.out.println("Database initialization... ");
		System.out.println(telosysToolsCfg.getDatabasesDbCfgFile());
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(telosysToolsCfg, dbId);
		databaseInMemory.executeSqlFile(sqlFile);
		// do not close DB here (keep it in memory)
		
		System.out.println("DSL model creation from database... ");
		DbToModelManager manager = new DbToModelManager(telosysToolsCfg, new ConsoleLogger() );
		GlobalLoggingConfig.enableLog();
		DslModel model = manager.createModelFromDatabase(dbId, modelName);
		GlobalLoggingConfig.disableLog();

		databaseInMemory.close();
		
		PrintUtil.printModel(model);
		return model ;
	}
	
	@Test
	public void test1() throws TelosysToolsException {
		printSeparator("test1");
		// DB ID 1
		DslModel model = generateRepositoryModel("db1", "customers.sql", "customers");
		assertEquals("db1", model.getDatabaseId() );
		assertEquals(2, model.getEntities().size() );

		DslModelEntity customerEntity = (DslModelEntity) model.getEntityByTableName("CUSTOMER") ;
		assertNotNull( customerEntity );
		Attribute code = customerEntity.getAttributeByName("code");
		assertNotNull(code);
		assertEquals ("string", code.getNeutralType() );
		//assertEquals ("VARCHAR(5)", code.getDatabaseType()); // H2 ver 1.3.176
		assertEquals ("CHARACTER VARYING(5)", code.getDatabaseType()); // h2 ver 2.1.210 : VARCHAR(5) in SQL script
		
		assertTrue (code.isKeyElement());
		assertTrue (code.isNotNull());
		assertEquals ("5", code.getDatabaseSize());
		assertEquals ("5", code.getSize());
		assertEquals (5, code.getMaxLength().intValue() );
		
		Attribute firstName = customerEntity.getAttributeByName("firstName");
		assertNotNull( firstName );
		assertFalse (firstName.isKeyElement());
		assertFalse (firstName.isNotNull());
		assertEquals(40, firstName.getMaxLength().intValue() );
		assertEquals(" a zer", firstName.getDatabaseDefaultValue() );
		assertEquals("my comment", firstName.getDatabaseComment());
		
		assertNotNull( customerEntity.getAttributeByName("lastName") );

		Attribute login = customerEntity.getAttributeByName("login");
		assertNotNull(login);
		assertEquals ("string", login.getNeutralType() );
		assertTrue (login.isNotNull());

		Attribute age = customerEntity.getAttributeByName("age");
		assertNotNull(age);
		assertEquals ("int", age.getNeutralType() );

		Attribute score = customerEntity.getAttributeByName("score");
		assertNotNull(score);
		assertEquals ("decimal", score.getNeutralType() );
		assertEquals("", score.getDatabaseDefaultValue() );
		assertEquals("", score.getDatabaseComment());
		assertEquals("5,2", score.getDatabaseSize());
		assertEquals("5,2", score.getSize());

		DslModelEntity countryEntity = (DslModelEntity) model.getEntityByTableName("COUNTRY") ;
		assertNotNull( countryEntity );
		assertNotNull( countryEntity.getAttributeByName("code") );
		assertNotNull( countryEntity.getAttributeByName("name") );
	}

	@Test
	public void test2() throws TelosysToolsException {
		printSeparator("test2");
		
		DslModel model = generateRepositoryModel("db2", "students.sql", "students");
		assertEquals("db2", model.getDatabaseId() );
		assertEquals(4, model.getEntities().size());

		DslModelEntity studentEntity = (DslModelEntity) model.getEntityByTableName("STUDENT");
		assertNotNull(studentEntity);
		
		DslModelEntity teacherEntity = (DslModelEntity) model.getEntityByTableName("TEACHER");
		assertNotNull(teacherEntity);
		
		DslModelEntity courseEntity = (DslModelEntity) model.getEntityByTableName("COURSE");
		assertNotNull(courseEntity);
		
		//--- Check FK information stored in Attribute
		Attribute studentId = studentEntity.getAttributeByName("id");
		assertNotNull(studentId);
		assertFalse(studentId.isFK());
		assertFalse(studentId.isFKSimple());
		assertFalse(studentId.isFKComposite());
		assertNull (studentId.getReferencedEntityClassName() );

		Attribute studentTeacherCode1 = studentEntity.getAttributeByName("teacherCode1") ;
		assertNotNull(studentTeacherCode1);
		assertTrue(studentTeacherCode1.hasFKParts());
		assertEquals(1, studentTeacherCode1.getFKParts().size() );
		ForeignKeyPart fkPart = studentTeacherCode1.getFKParts().get(0);
		assertEquals ("Teacher", fkPart.getReferencedEntityName());
		assertEquals ("code", fkPart.getReferencedAttributeName());
		assertTrue(studentTeacherCode1.isFK());
		assertTrue(studentTeacherCode1.isFKSimple());
		assertFalse(studentTeacherCode1.isFKComposite());
		assertEquals ("Teacher", studentTeacherCode1.getReferencedEntityClassName() );

		Attribute studentTeacherCode2 = studentEntity.getAttributeByName("teacherCode2") ;
		assertNotNull(studentTeacherCode2);
		assertEquals(1, studentTeacherCode2.getFKParts().size() );
		fkPart = studentTeacherCode2.getFKParts().get(0);
		assertEquals ("Teacher", fkPart.getReferencedEntityName());
		assertEquals ("code", fkPart.getReferencedAttributeName());
		assertTrue(studentTeacherCode2.isFK());
		assertTrue(studentTeacherCode2.isFKSimple());
		assertFalse(studentTeacherCode2.isFKComposite());
		assertEquals ("Teacher", studentTeacherCode2.getReferencedEntityClassName() );
		
		//--- Foreign keys 
		assertEquals(2, studentEntity.getForeignKeys().size());
		for ( ForeignKey fk : studentEntity.getForeignKeys() ) {
			assertEquals ("Student", fk.getOriginEntityName() );
			assertEquals ("Teacher", fk.getReferencedEntityName() );
			assertEquals ( 1, fk.getAttributes().size() );
		}
		assertEquals(0, teacherEntity.getForeignKeys().size());
		
		//--- Links
		assertEquals(2, studentEntity.getLinks().size() );
		for ( Link link : studentEntity.getLinks() ) {
			assertEquals ("Teacher", link.getReferencedEntityName());
			assertEquals ( Cardinality.MANY_TO_ONE, link.getCardinality() );
		}
		assertEquals(3, teacherEntity.getLinks().size() );
		for ( Link link : teacherEntity.getLinks() ) {
			String ref = link.getReferencedEntityName();
			Cardinality cardinality = link.getCardinality();
			assertTrue ("Student".equals(ref) || "Course".equals(ref));
			assertTrue (cardinality == Cardinality.ONE_TO_MANY || cardinality == Cardinality.MANY_TO_MANY );
			if ( cardinality == Cardinality.MANY_TO_MANY ) {
				assertTrue ("Course".equals(ref) );
			}
		}
		assertEquals(1, courseEntity.getLinks().size() );
		Link link = courseEntity.getLinks().get(0);
		assertEquals ("Teacher", link.getReferencedEntityName());
		assertEquals ( Cardinality.MANY_TO_MANY, link.getCardinality() );
	}

}
