package org.telosys.tools.dsl.parsing.modellevel;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainTag;
import org.telosys.tools.junit.utils.ModelUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ValidModelTwoEntitiesTest {

	@Test
	public void test() {
		DomainModel model = ModelUtil.parseValidModel("src/test/resources/model_test/valid/TwoEntities.model");
		
		assertEquals(2, model.getNumberOfEntities());
		assertEquals(2, model.getEntityNames().size());
		DomainEntity entity = model.getEntity("Employee");
		
		assertEquals("Employee", entity.getName());
		assertEquals(4, entity.getNumberOfFields());
		
		DomainField field ;
		Map<String,DomainAnnotation> annotations ;
		DomainTag tag ;
		Map<String,DomainTag> tags ;

		//--- TAGS at ENTITY LEVEL
		assertNotNull(entity.getTagNames());
		assertEquals(2, entity.getTagNames().size());
		assertEquals(2, entity.getTags().size());
		assertTrue(entity.hasTag(new DomainTag("SpecialClass")));
		assertTrue(entity.hasTag(new DomainTag("FooBar")));
		tag = entity.getTags().get("SpecialClass");
		assertNotNull(tag);
		assertNull(tag.getParameter());
		tag = entity.getTags().get("FooBar");
		assertNotNull(tag);
		assertEquals("abc", tag.getParameter());
		
		//---------- FIELD : id
		field = entity.getField("id") ;
		assertNotNull(field);
		assertEquals("int", field.getTypeName());
		// Field annotations
		annotations = field.getAnnotations();
		assertEquals(2, annotations.size() );
		assertNotNull(annotations.get("Id"));
		assertNotNull(annotations.get("Min"));
		assertEquals(new BigDecimal("1"), annotations.get("Min").getParameterAsBigDecimal() );
		// Field tags
		tags = field.getTags();
		assertEquals(2, tags.size() );
		assertNotNull(tags.get("Foo"));
		assertEquals(" abc ",tags.get("Foo").getParameter());
		assertNotNull(tags.get("Bar"));
		assertEquals("12.34",tags.get("Bar").getParameter());
		
		//---------- FIELD : firstName
		field = entity.getField("firstName") ;
		assertNotNull(field);
		assertEquals("string", field.getTypeName());
		// Field annotations
		annotations = field.getAnnotations();
		assertEquals(1, annotations.size() );
		assertNotNull(annotations.get("DefaultValue"));
		assertEquals("Bart", annotations.get("DefaultValue").getParameterAsString());

		//---------- FIELD : birthDate
		field = entity.getField("birthDate") ;
		assertNotNull(field);
		assertEquals("date", field.getTypeName());

		//---------- LINK : country
		field = entity.getField("country") ;
		assertNotNull(field);
		assertEquals("Country", field.getTypeName());
		annotations = field.getAnnotations();
		//--- Annotations
		assertEquals(1, annotations.size() );
		assertNotNull(annotations.get("Transient"));
		assertTrue(field.hasAnnotation(annotations.get("Transient")));
		//--- Tags
		tags = field.getTags();
		assertEquals(2, tags.size() );

		assertTrue(field.hasTag(new DomainTag("Foo")));
		assertNotNull(tags.get("Foo"));
		
		assertTrue(field.hasTag(new DomainTag("MyTag")));
		assertNotNull(tags.get("MyTag"));
		assertTrue(tags.get("MyTag").hasParameter());
		assertEquals("123", tags.get("MyTag").getParameter());
	}

}
