package org.telosys.tools.dsl.generic.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEnumerationForDecimal;
import org.telosys.tools.dsl.parser.model.DomainEnumerationForInteger;
import org.telosys.tools.dsl.parser.model.DomainEnumerationForString;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;

@RunWith(MockitoJUnitRunner.class)
public class ConverterTest {

	@InjectMocks
	Converter converter;

	@Test
	public void testEmptyModel() {
		// Given
		DomainModel domainModel = new DomainModel(null);
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals("", model.getName());
		assertTrue(model.getEntities().isEmpty());
	}
	
	@Test
	public void testEmptyEntity() {
		// Given
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");
		domainModel.addEntity(domainEntity_1);
		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals("domainModel", model.getName());
		assertEquals(2, model.getEntities().size());
		
		// entity 1
		Entity entity_1 = getEntityByName(model, "domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getName());
		
		// attributes of entity 1
		assertTrue(entity_1.getAttributes().isEmpty());
		
		// entity 2
		Entity entity_2 = getEntityByName(model, "domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getName());
		
		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
	}
	
	@Test
	public void testAttributeWithNeutralTypes() {
		// Given
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");

		domainModel.addEntity(domainEntity_1);
		DomainEntityField domainEntityField_1_2 = new DomainEntityField("field_1_2", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_4 = new DomainEntityField("field_1_4", DomainNeutralTypes.getType(DomainNeutralTypes.DATE));
		DomainEntityField domainEntityField_1_5 = new DomainEntityField("field_1_5", DomainNeutralTypes.getType(DomainNeutralTypes.DECIMAL));
		DomainEntityField domainEntityField_1_6 = new DomainEntityField("field_1_6", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		DomainEntityField domainEntityField_1_7 = new DomainEntityField("field_1_7", DomainNeutralTypes.getType(DomainNeutralTypes.STRING));
		DomainEntityField domainEntityField_1_8 = new DomainEntityField("field_1_8", DomainNeutralTypes.getType(DomainNeutralTypes.TIME));
		DomainEntityField domainEntityField_1_9 = new DomainEntityField("field_1_9", DomainNeutralTypes.getType(DomainNeutralTypes.TIMESTAMP));
		domainEntity_1.addField(domainEntityField_1_2);
		domainEntity_1.addField(domainEntityField_1_4);
		domainEntity_1.addField(domainEntityField_1_5);
		domainEntity_1.addField(domainEntityField_1_6);
		domainEntity_1.addField(domainEntityField_1_7);
		domainEntity_1.addField(domainEntityField_1_8);
		domainEntity_1.addField(domainEntityField_1_9);
		
		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals("domainModel", model.getName());
		assertEquals(2, model.getEntities().size());
		
		// entity 1
		Entity entity_1 = getEntityByName(model, "domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getName());
		
		// attributes of entity 1
		assertEquals(7, entity_1.getAttributes().size());
		
		assertEquals("java.lang.Boolean", getAttributeByName(entity_1, "field_1_2").getType());
		assertEquals("java.util.Date", getAttributeByName(entity_1, "field_1_4").getType());
		assertEquals("java.math.BigDecimal", getAttributeByName(entity_1, "field_1_5").getType());
		assertEquals("java.lang.Integer", getAttributeByName(entity_1, "field_1_6").getType());
		assertEquals("java.lang.String", getAttributeByName(entity_1, "field_1_7").getType());
		assertEquals("java.util.Date", getAttributeByName(entity_1, "field_1_8").getType());
		assertEquals("java.util.Date", getAttributeByName(entity_1, "field_1_9").getType());
		
		// entity 2
		Entity entity_2 = getEntityByName(model, "domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getName());
		
		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
	}

	@Test
	public void testAttributeWithEnumeration() {
		// Given
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");

		domainModel.addEntity(domainEntity_1);
		DomainEntityField domainEntityField_1_1 = new DomainEntityField("field_1_1", new DomainEnumerationForString("enum_string"));
		DomainEntityField domainEntityField_1_2 = new DomainEntityField("field_1_2", new DomainEnumerationForInteger("enum_integer"));
		DomainEntityField domainEntityField_1_3 = new DomainEntityField("field_1_3", new DomainEnumerationForDecimal("enum_decimal"));
		domainEntity_1.addField(domainEntityField_1_1);
		domainEntity_1.addField(domainEntityField_1_2);
		domainEntity_1.addField(domainEntityField_1_3);
		
		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals("domainModel", model.getName());
		assertEquals(2, model.getEntities().size());
		
		// entity 1
		Entity entity_1 = getEntityByName(model, "domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getName());
		
		// TODO
		fail("TODO : Enumerations");

	}
	
	@Test
	public void testAttributeWithLink() {
		// Given
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");

		domainModel.addEntity(domainEntity_1);
		DomainEntityField domainEntityField_1_1 = new DomainEntityField("field_1_1", new DomainEntity("domainEntity_2"));
		domainEntity_1.addField(domainEntityField_1_1);
		
		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals("domainModel", model.getName());
		assertEquals(2, model.getEntities().size());
		
		// entity 1
		Entity entity_1 = getEntityByName(model, "domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getName());
		
		// TODO
		fail("TODO : Link between entities");
		
	}
	
	private Entity getEntityByName(Model model, String name) {
		for(Entity entity : model.getEntities()) {
			if(name.equals(entity.getName())) {
				return entity;
			}
		}
		return null;
	}

	private Attribute getAttributeByName(Entity entity, String name) {
		for(Attribute attribute : entity.getAttributes()) {
			if(name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}
	
}
