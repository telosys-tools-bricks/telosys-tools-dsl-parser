package org.telosys.tools.dsl.parser.model;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.generic.model.types.NeutralType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DomainEntityTest {

	private void declareField(DomainEntity entity, String fieldName, String fieldType) {
		try {
			entity.addField( buildField(fieldName, fieldType) );
		} catch (DslModelError e) {
			throw new IllegalStateException(e.getErrorMessage());
		}
	}
	private DomainField buildField(String fieldName, String fieldType) {
		int lineNumber = 1 ;
		DomainType neutralType = DomainNeutralTypes.getType(fieldType);
		if ( neutralType != null) {
			// Field with basic type ( string, int, etc )
			return new DomainField(lineNumber, fieldName, neutralType );
		}
		else {
			// Field with entity type ( Car, Student, etc )
			DomainType entityType = new DomainEntityType(fieldType, DomainCardinality.ONE) ;
			return new DomainField(lineNumber, fieldName, entityType );
		}
	}
	
	@Test
	public void testEntity() {
		DomainEntity entity = new DomainEntity("Book") ;
		assertEquals ( entity.getName(), "Book" );
		
		assertEquals(0, entity.getNumberOfFields());
		assertNotNull(entity.getFields());
	}
	
	@Test
	public void testFieldNeutralType() {
		DomainEntity entity = new DomainEntity("Student") ;
		assertTrue ( entity.getNumberOfFields() == 0 ) ;
		
		declareField(entity,"firstName", NeutralType.STRING);
		
		assertTrue ( entity.getNumberOfFields() == 1 ) ;

		declareField(entity,"lastName", NeutralType.STRING);
		assertTrue ( entity.getNumberOfFields() == 2 ) ;
		
		DomainField field = null ;
		
		field = entity.getField("firstName");
		assertNotNull ( field ) ;
		assertTrue ( field.isAttribute() ) ;

		field = entity.getField("lastName");
		assertNotNull ( field ) ;
		assertTrue ( field.isAttribute() ) ;

		field = entity.getField("xxx");
		assertNull ( field ) ;
	}
	
	@Test
	public void testFieldEntityReference() {
		
		DomainEntity teacher = new DomainEntity("Teacher") ;
		assertTrue ( teacher.getNumberOfFields() == 0 ) ;

		DomainEntity student = new DomainEntity("Student") ;
		assertTrue ( student.getNumberOfFields() == 0 ) ;
		
		declareField(student, "teacher", "Teacher");
		assertTrue ( student.getNumberOfFields() == 1 ) ;
		
		DomainField field = student.getField("teacher");
		assertTrue ( field.isLink() );
		assertFalse ( field.isAttribute() );
	}
	
	@Test ( expected = Exception.class )
	public void testFieldDuplicated1() {
		DomainEntity entity = new DomainEntity("Book") ;
		declareField(entity,"lastName", NeutralType.STRING);
		declareField(entity,"lastName", NeutralType.STRING);
	}
	
	@Test ( expected = Exception.class )
	public void testFieldDuplicated2() {
		DomainEntity entity = new DomainEntity("Student") ;
		declareField(entity,"teacher", "Teacher");
		declareField(entity,"teacher", "Teacher");
	}
	
	@Test ( expected = Exception.class )
	public void testFieldDuplicated3() {
		DomainEntity entity = new DomainEntity("Student") ;
		declareField(entity,"foo", NeutralType.STRING);
		declareField(entity,"foo", NeutralType.INTEGER);
	}
	
}
