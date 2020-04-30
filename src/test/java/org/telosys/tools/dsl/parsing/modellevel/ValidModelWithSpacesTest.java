package org.telosys.tools.dsl.parsing.modellevel;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.ModelParsingError;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ValidModelWithSpacesTest {
	
	@Test
	public void test() throws ModelParsingError {
		File modelFile = new File("src/test/resources/model_test/valid/TwoEntitiesWithSpaces.model");
		
		Parser parser = new Parser();
		DomainModel model = parser.parseModel(modelFile);
		
		assertEquals(2, model.getNumberOfEntities());
		assertEquals(2, model.getEntityNames().size());
		DomainEntity entity = model.getEntity("Employee");
		
		assertEquals("Employee", entity.getName());
		assertEquals(4, entity.getNumberOfFields());
		
		DomainField field ;
		Map<String,DomainAnnotation> annotations ;
		Map<String,DomainTag> tags ;
		
		//---------- FIELD : id
		field = entity.getField("id") ;
		assertNotNull(field);
		assertEquals("int", field.getTypeName());
		// Field annotations
		annotations = field.getAnnotations();
		assertEquals(4, annotations.size() );
		assertNotNull(annotations.get("Id"));
		assertNotNull(annotations.get("Min"));
		assertEquals(new BigDecimal("1"), annotations.get("Min").getParameterAsBigDecimal() );
		assertNotNull(annotations.get("Max"));
		assertEquals(new BigDecimal("9999"), annotations.get("Max").getParameterAsBigDecimal() );
		assertNotNull(annotations.get("Label"));
		assertEquals("Identifier", annotations.get("Label").getParameterAsString());
		// Field tags
		tags = field.getTags();
		assertEquals(3, tags.size() );
		assertNotNull(tags.get("Id"));
		assertNotNull(tags.get("Foo"));
		assertEquals("abc ",tags.get("Foo").getParameterAsString());
		assertNull(tags.get("Foo").getParameterAsInteger());
		assertNotNull(tags.get("Bar"));
		assertEquals("12.34",tags.get("Bar").getParameterAsString());
		assertNull(tags.get("Bar").getParameterAsInteger());
		// Field errors
		assertFalse(field.hasErrors());
		
		//---------- FIELD : firstName
		field = entity.getField("firstName") ;
		assertNotNull(field);
		assertEquals("string", field.getTypeName());
		// Field annotations
		annotations = field.getAnnotations();
		assertEquals(2, annotations.size() );
		assertNotNull(annotations.get("DefaultValue"));
		assertTrue(field.hasAnnotation(annotations.get("DefaultValue")));
		assertEquals("Bart", annotations.get("DefaultValue").getParameterAsString());
		assertNotNull(annotations.get("InitialValue"));
		assertEquals("xxx", annotations.get("InitialValue").getParameterAsString());
		// Field tags
		tags = field.getTags();
		assertEquals(1, tags.size() );
		assertNotNull(tags.get("tag"));
		// Field errors
		assertFalse(field.hasErrors());

		//---------- FIELD : birthDate
		field = entity.getField("birthDate") ;
		assertNotNull(field);
		assertEquals("date", field.getTypeName());
		annotations = field.getAnnotations();
		assertEquals(1, annotations.size() );
		// Field errors
		assertFalse(field.hasErrors());

		//		List<String> annotNames = id.getAnnotationNames();
//		for ( DomainField field : entity.getFields() ) {
//			
//		}
		
		//---------- FIELD : birthDate
		field = entity.getField("country") ;
		assertNotNull(field);
		assertFalse(field.hasErrors());
		assertEquals("Country", field.getTypeName());
		annotations = field.getAnnotations();
		//--- Annotations
		assertEquals(2, annotations.size() );
		assertNotNull(annotations.get("Embedded"));
		assertTrue(field.hasAnnotation(annotations.get("Embedded")));
		assertNotNull(annotations.get("Max"));
		assertTrue(field.hasAnnotation(annotations.get("Max")));
		assertEquals(new BigDecimal("3"), annotations.get("Max").getParameterAsBigDecimal() );
		//--- Tags
		tags = field.getTags();
		assertEquals(2, tags.size() );

		assertTrue(field.hasTag(new DomainTag("Foo")));
		assertNotNull(tags.get("Foo"));
		assertNull(tags.get("Foo").getParameterAsString());
		assertNull(tags.get("Foo").getParameterAsInteger());
		
		assertTrue(field.hasTag(new DomainTag("MyTag")));
		assertNotNull(tags.get("MyTag"));
		assertTrue(tags.get("MyTag").hasParameter());
		assertEquals("123", tags.get("MyTag").getParameterAsString());
		assertNull(tags.get("MyTag").getParameterAsInteger()); // tag param is always STRING
		assertNull(tags.get("MyTag").getParameterAsBigDecimal()); // tag param is always STRING

	}

}
