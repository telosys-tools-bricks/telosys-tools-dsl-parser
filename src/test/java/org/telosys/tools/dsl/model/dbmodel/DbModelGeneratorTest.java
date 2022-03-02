package org.telosys.tools.dsl.model.dbmodel;

import java.io.File;

import org.junit.Test;
import org.telosys.tools.commons.TelosysToolsException;
import org.telosys.tools.commons.cfg.TelosysToolsCfg;
import org.telosys.tools.commons.cfg.TelosysToolsCfgManager;
import org.telosys.tools.commons.logger.ConsoleLogger;
import org.telosys.tools.dsl.model.DslModel;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.ForeignKeyPart;
import org.telosys.tools.junit.utils.PrintUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DbModelGeneratorTest {
	
	private static final String SRC_TEST_RESOURCES = "src/test/resources/" ;
	private static final String PROJECT_FOLDER = "myproject" ;
	private static final int    DEFAULT_DATABASE_ID  = 1 ;

	private void printSeparator(String s) {
		System.out.println("========== " + s );
	}
	
	private TelosysToolsCfg getTelosysToolsCfg(String projectName) {
		File projectFolder = new File(SRC_TEST_RESOURCES + projectName);
		TelosysToolsCfgManager cfgManager = new TelosysToolsCfgManager(projectFolder.getAbsolutePath());
		return cfgManager.loadTelosysToolsCfg();
	}

	private DslModel generateRepositoryModel(String sqlFile, String modelName) throws TelosysToolsException {
		
		System.out.println("Database initialization... ");
		DatabaseInMemory databaseInMemory = new DatabaseInMemory(DEFAULT_DATABASE_ID);
		databaseInMemory.executeSqlFile(sqlFile);
		// do not close DB here (keep it in memory)
		
		System.out.println("Get configuration... ");
		TelosysToolsCfg telosysToolsCfg = getTelosysToolsCfg(PROJECT_FOLDER);
//		DbConfigManager dbConfigManager = new DbConfigManager(telosysToolsCfg); 
//		DatabaseConfiguration databaseConfiguration = dbConfigManager.load().getDatabaseConfiguration(DEFAULT_DATABASE_ID);
//		DbConnectionManager dbConnectionManager = new DbConnectionManager(telosysToolsCfg);
		
		System.out.println("DSL model creation from database... ");
		DbToModelManager manager = new DbToModelManager(telosysToolsCfg, new ConsoleLogger() );
		DslModel model = manager.createModelFromDatabase(DEFAULT_DATABASE_ID, modelName);

		databaseInMemory.close();
		
		PrintUtil.printModel(model);
		return model ;
	}
	
	@Test
	public void test1() throws TelosysToolsException {
		printSeparator("test1");

		DslModel repositoryModel = generateRepositoryModel("customers.sql", "customers");
		assertTrue(repositoryModel.getDatabaseId() == DEFAULT_DATABASE_ID );
		assertEquals(2, repositoryModel.getEntities().size() );

		DslModelEntity customerEntity = (DslModelEntity) repositoryModel.getEntityByTableName("CUSTOMER") ;
		assertNotNull( customerEntity );
		Attribute code = customerEntity.getAttributeByName("code");
		assertNotNull(code);
		assertEquals ("string", code.getNeutralType() );
		assertEquals ("VARCHAR(5)", code.getDatabaseType());
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

		DslModelEntity countryEntity = (DslModelEntity) repositoryModel.getEntityByTableName("COUNTRY") ;
		assertNotNull( countryEntity );
		assertNotNull( countryEntity.getAttributeByName("code") );
		assertNotNull( countryEntity.getAttributeByName("name") );
	}

	@Test
	public void test2() throws TelosysToolsException {
		printSeparator("test2");
		
		DslModel repositoryModel = generateRepositoryModel("students.sql", "students");
		assertEquals(DEFAULT_DATABASE_ID, repositoryModel.getDatabaseId().intValue() );
		assertEquals(2, repositoryModel.getEntities().size());

		DslModelEntity studentEntity = (DslModelEntity) repositoryModel.getEntityByTableName("STUDENT");
		assertNotNull(studentEntity);
		
		DslModelEntity teacherEntity = (DslModelEntity) repositoryModel.getEntityByTableName("TEACHER");
		assertNotNull(teacherEntity);
		
		//assertEquals(2, studentEntity.getLinks().size() );
		//assertEquals(2, teacherEntity.getLinks().size() );

//		checkJavaName(studentLinks[0].getFieldName(),studentLinks[1].getFieldName(), "teacher", "teacher2" );
//		checkJavaName(teacherLinks[0].getFieldName(),teacherLinks[1].getFieldName(), "listOfStudent", "listOfStudent2" );	
		
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
		assertEquals ("Teacher", fkPart.getReferencedEntity());
		assertEquals ("code", fkPart.getReferencedAttribute());
		assertTrue(studentTeacherCode1.isFK());
		assertTrue(studentTeacherCode1.isFKSimple());
		assertFalse(studentTeacherCode1.isFKComposite());
		assertEquals ("Teacher", studentTeacherCode1.getReferencedEntityClassName() );

		Attribute studentTeacherCode2 = studentEntity.getAttributeByName("teacherCode2") ;
		assertNotNull(studentTeacherCode2);
		assertEquals(1, studentTeacherCode2.getFKParts().size() );
		fkPart = studentTeacherCode2.getFKParts().get(0);
		assertEquals ("Teacher", fkPart.getReferencedEntity());
		assertEquals ("code", fkPart.getReferencedAttribute());
		assertTrue(studentTeacherCode2.isFK());
		assertTrue(studentTeacherCode2.isFKSimple());
		assertFalse(studentTeacherCode2.isFKComposite());
		assertEquals ("Teacher", studentTeacherCode2.getReferencedEntityClassName() );
	}

/***
	@Test
	public void test3() throws TelosysToolsException {
		printSeparator("test3");
		
		RepositoryModel repositoryModel = generateRepositoryModel(3);
		printModel(repositoryModel);
		assertTrue(repositoryModel.getDatabaseId() == DEFAULT_DATABASE_ID );
		assertEquals(2, repositoryModel.getNumberOfEntities() );

		EntityInDbModel studentEntity = repositoryModel.getEntityByTableName("STUDENT");
		assertNotNull(studentEntity);
		
		EntityInDbModel teacherEntity = repositoryModel.getEntityByTableName("TEACHER");
		assertNotNull(teacherEntity);
		
		LinkInDbModel[] studentLinks = studentEntity.getLinksArray();
		System.out.println("STUDENT links : " + studentLinks.length);
		assertTrue(studentLinks.length == 2);

		LinkInDbModel[] teacherLinks = teacherEntity.getLinksArray();
		System.out.println("TEACHER links : " + teacherLinks.length);
		assertTrue(teacherLinks.length == 2);
		
		checkJavaName(studentLinks[0].getFieldName(),studentLinks[1].getFieldName(), "teacher2", "teacher3" );
		checkJavaName(teacherLinks[0].getFieldName(),teacherLinks[1].getFieldName(), "listOfStudent2", "listOfStudent3" );		
	}

	@Test
	public void test5() throws TelosysToolsException {
		printSeparator("test5");
		
		RepositoryModel repositoryModel = generateRepositoryModel(5);
		printModel(repositoryModel);
		assertEquals(2, repositoryModel.getNumberOfEntities() );

		checkStudentEntity(repositoryModel.getEntityByTableName("STUDENT"));
		checkTeacherEntity(repositoryModel.getEntityByTableName("TEACHER"));		
		
		Document doc = convertToXml(repositoryModel);
		
		RepositoryModel model2 = convertToModel(doc);
		assertEquals(repositoryModel.getNumberOfEntities(), model2.getNumberOfEntities());
		
//		checkStudentEntity(model2.getEntityByTableName("STUDENT"));
//		checkTeacherEntity(model2.getEntityByTableName("TEACHER"));
	}
	
	private void checkStudentEntity(EntityInDbModel studentEntity) {
		System.out.println("Check STUDENT entity");

		assertNotNull(studentEntity);
		
		assertFalse( studentEntity.isJoinTable() );
		
		//--- Attributes (columns)
		assertEquals(4, studentEntity.getAttributesCount() ) ;

		AttributeInDbModel studentId = studentEntity.getAttributeByColumnName("ID");
		assertNotNull(studentId);
		AttributeInDbModel studentFirstName = studentEntity.getAttributeByColumnName("FIRST_NAME");
		assertNotNull(studentFirstName);
		AttributeInDbModel studentLastName = studentEntity.getAttributeByColumnName("LAST_NAME");
		assertNotNull(studentLastName);
		
		//--- Foreign Keys
//		assertEquals(1, studentEntity.getForeignKeys().length ) ;
		assertEquals(1, studentEntity.getForeignKeys().size() ) ;
//		assertEquals("STUDENT", studentEntity.getForeignKeys()[0].getTableName() ) ;
		assertEquals("Student", studentEntity.getForeignKeys().get(0).getOriginEntityName() ) ;
//		assertEquals("TEACHER", studentEntity.getForeignKeys()[0].getReferencedTableName() ) ;
		assertEquals("Teacher", studentEntity.getForeignKeys().get(0).getReferencedEntityName() ) ;
		
		//--- Links
		LinkInDbModel[] studentLinks = studentEntity.getLinksArray();
		System.out.println("Number of links : " + studentLinks.length);
		assertEquals(1, studentLinks.length);

		System.out.println("Student link : ");
		LinkInDbModel studentLink = studentLinks[0] ;
		
		System.out.println(" . getCardinality : " + studentLink.getCardinality()  );
		assertEquals( Cardinality.MANY_TO_ONE, studentLink.getCardinality() ); 
		
		System.out.println(" . getMappedBy : '" + studentLink.getMappedBy() +"'" );
		assertNull( studentLink.getMappedBy() ); 
		
		System.out.println(" . isOwningSide  : " + studentLink.isOwningSide() );
		assertTrue(studentLink.isOwningSide() );
		System.out.println(" . isInverseSide : " + studentLink.isInverseSide() );
		assertFalse(studentLink.isInverseSide() );

		System.out.println(" . getForeignKeyName : " + studentLink.getForeignKeyName() );
		assertNotNull(studentLink.getForeignKeyName() );

//		assertNotNull(studentLink.getJoinColumns() );
		assertNotNull(studentLink.getAttributes() );
		
//		List<JoinColumn> joinColumns = studentLink.getJoinColumns();
		List<LinkAttribute> joinColumns = studentLink.getAttributes();
		System.out.println(" . getJoinColumns / size : " + joinColumns.size() );
		assertEquals(1, joinColumns.size() );
	}
	
	private void checkTeacherEntity(EntityInDbModel teacherEntity) {
		System.out.println("Check TEACHER entity");
		
		assertNotNull(teacherEntity);
		assertFalse( teacherEntity.isJoinTable() );
//		assertEquals(0, teacherEntity.getForeignKeys().length ) ; // No FK
		assertEquals(0, teacherEntity.getForeignKeys().size() ) ; // No FK
		
//		LinkInDbModel[] teacherLinks = teacherEntity.getLinksArray();
//		System.out.println("Number of  links : " + teacherLinks.length);
//		assertEquals(1, teacherLinks.length);
		assertEquals(1, teacherEntity.getLinksCount());
	}
***/
}
