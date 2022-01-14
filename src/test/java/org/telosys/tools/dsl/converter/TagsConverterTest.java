package org.telosys.tools.dsl.converter;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.model.DslModelAttribute;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.model.DslModelLink;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TagsConverterTest {

	@Test
	public void testTagsAtEntityLevel() {
		DslModelErrors errors = new DslModelErrors();
		TagsConverter tagsConverter = new TagsConverter(errors);
		
		// Origin ENTITY  
		DomainEntity entity = new DomainEntity("MyEntity") ;
		entity.addTag(new DomainTag("Foo", "abc"));
		assertEquals(1, entity.getTags().size() );
		// Destination ENTITY  
		DslModelEntity dslEntity = new DslModelEntity("MyEntity");

		// Conversion 
		tagsConverter.applyTagsToEntity(dslEntity, entity);
		// Result 		
		assertFalse( dslEntity.getTagContainer().isEmpty());
		assertTrue( dslEntity.getTagContainer().containsTag("Foo") );
		assertEquals("abc", dslEntity.getTagContainer().getTagValue("Foo"));
	}
	
	@Test
	public void testTagsAtAttributeLevel() {
		DslModelErrors errors = new DslModelErrors();
		TagsConverter tagsConverter = new TagsConverter(errors);
		
		// Origin ATTRIBUTE (field) 
		DomainField field = new DomainField(12, "firstName", new DomainNeutralType("string") );
		field.addTag(new DomainTag("Foo", "abc"));
		assertEquals(1, field.getTags().size() );
		// Destination ATTRIBUTE  
		DslModelEntity dslEntity = new DslModelEntity("MyEntity");
		DslModelAttribute dslAttribute = new DslModelAttribute("firstName", "string");
		
		// Conversion 
		tagsConverter.applyTagsToAttribute(dslEntity, dslAttribute, field);
		// Result 		
		assertFalse( dslAttribute.getTagContainer().isEmpty());
		assertTrue( dslAttribute.getTagContainer().containsTag("Foo") );
		assertEquals("abc", dslAttribute.getTagContainer().getTagValue("Foo"));
	}
	
	@Test
	public void testTagsAtLinkLevel() {
		DslModelErrors errors = new DslModelErrors();
		TagsConverter tagsConverter = new TagsConverter(errors);
		
		// Origin LINK (field)
		DomainField field = new DomainField(12, "country", new DomainEntityType("Country", DomainCardinality.ONE) );
		field.addTag(new DomainTag("Foo", "abc"));
		assertEquals(1, field.getTags().size() );
		// Destination ATTRIBUTE  
		DslModelEntity dslEntity = new DslModelEntity("MyEntity");
		DslModelLink dslLink = new DslModelLink("country");
		
		// Conversion 		
		tagsConverter.applyTagsToLink(dslEntity, dslLink, field);
		// Result 		
		assertFalse( dslLink.getTagContainer().isEmpty());
		assertTrue( dslLink.getTagContainer().containsTag("Foo") );
		assertEquals("abc", dslLink.getTagContainer().getTagValue("Foo"));
	}
	
}
