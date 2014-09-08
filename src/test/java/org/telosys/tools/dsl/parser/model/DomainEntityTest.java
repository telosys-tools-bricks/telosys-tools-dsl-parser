package org.telosys.tools.dsl.parser.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.telosys.tools.dsl.EntityParserException;

public class DomainEntityTest {

	@Test
	public void testEntity() {
		//DomainModel model = new DomainModel("mymodel");
		
		DomainEntity entity = new DomainEntity("Book") ;
		assertEquals ( entity.getName(), "Book" );
		
		assertTrue ( entity.getNature() == DomainTypeNature.ENTITY );
		
		assertTrue ( entity.isEntity() );		
		assertFalse ( entity.isEnumeration() );
		assertFalse ( entity.isNeutralType() );
	}
	
	@Test
	public void testFieldNeutralType() {
		DomainEntity entity = new DomainEntity("Student") ;
		assertTrue ( entity.getNumberOfFields() == 0 ) ;
		
		entity.addField( new DomainEntityField("firstName", DomainNeutralTypes.getType(DomainNeutralTypes.STRING) ) );
		assertTrue ( entity.getNumberOfFields() == 1 ) ;

		entity.addField( new DomainEntityField("lastName", DomainNeutralTypes.getType(DomainNeutralTypes.STRING) ) );
		assertTrue ( entity.getNumberOfFields() == 2 ) ;
		
		DomainEntityField field = null ;
		
		field = entity.getField("firstName");
		assertNotNull ( field ) ;
		assertTrue ( field.isNeutralType() ) ;

		field = entity.getField("lastName");
		assertNotNull ( field ) ;
		assertTrue ( field.isNeutralType() ) ;

		field = entity.getField("xxx");
		assertNull ( field ) ;
	}
	
	@Test
	public void testFieldEntityReference() {
		
		DomainEntity teacher = new DomainEntity("Teacher") ;
		assertTrue ( teacher.getNumberOfFields() == 0 ) ;

		DomainEntity student = new DomainEntity("Student") ;
		assertTrue ( student.getNumberOfFields() == 0 ) ;
		
		student.addField( new DomainEntityField("teacher", teacher ) );
		assertTrue ( student.getNumberOfFields() == 1 ) ;
		
		DomainEntityField field = student.getField("teacher");
		assertTrue ( field.isEntity() );
		assertFalse ( field.isEnumeration() );
		assertFalse ( field.isNeutralType() );
	}
	
	@Test
	public void testFieldEnumerationReference() {
		
		DomainEntity student = new DomainEntity("Student") ;
		assertTrue ( student.getNumberOfFields() == 0 ) ;
		
		student.addField( new DomainEntityField("studentType", new DomainEnumerationForString("StudentType") ) );
		assertTrue ( student.getNumberOfFields() == 1 ) ;
		
		DomainEntityField field = student.getField("studentType");
		assertTrue ( field.isEnumeration() );
		assertFalse ( field.isEntity() );
		assertFalse ( field.isNeutralType() );
	}
	
	@Test ( expected = EntityParserException.class )
	public void testFieldDuplicated1() {
		DomainEntity entity = new DomainEntity("Book") ;
		entity.addField( new DomainEntityField("lastName", DomainNeutralTypes.getType(DomainNeutralTypes.STRING) ) );
		entity.addField( new DomainEntityField("lastName", DomainNeutralTypes.getType(DomainNeutralTypes.STRING) ) );
	}
	
	@Test ( expected = EntityParserException.class )
	public void testFieldDuplicated2() {
		DomainEntity entity = new DomainEntity("Student") ;
		entity.addField( new DomainEntityField("teacher", new DomainEntity("Teacher") ) );
		entity.addField( new DomainEntityField("teacher", new DomainEntity("Teacher") ) );
	}
	
	@Test ( expected = EntityParserException.class )
	public void testFieldDuplicated3() {
		DomainEntity entity = new DomainEntity("Student") ;
		entity.addField( new DomainEntityField("studentType", new DomainEnumerationForString("StudentType") ) );
		entity.addField( new DomainEntityField("studentType", new DomainEnumerationForString("StudentType") ) );
	}
	
}
