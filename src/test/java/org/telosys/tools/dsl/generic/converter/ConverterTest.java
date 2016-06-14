package org.telosys.tools.dsl.generic.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.telosys.tools.dsl.AnnotationName;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.runners.MockitoJUnitRunner;
import org.telosys.tools.dsl.generic.model.GenericEntity;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.ModelType;

//@RunWith(MockitoJUnitRunner.class)
public class ConverterTest {

//	@InjectMocks
//	Converter converter;
	Converter converter = new Converter();

	@Test
	public void testEmptyModel() {
		// Given
		DomainModel domainModel = new DomainModel("test-model");
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals("", model.getName());
		assertTrue(model.getEntities().isEmpty());
		
		assertEquals(ModelType.DOMAIN_SPECIFIC_LANGUAGE, model.getType() );
	}
	
	private Model buildModelWithTwoEmptyEntities() {
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");
		domainModel.addEntity(domainEntity_1);
		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);		
		//--- Convert 
		return converter.convertToGenericModel(domainModel);	
	}
	
	@Test
	public void testEmptyEntity() {
		Model model = buildModelWithTwoEmptyEntities();

		assertEquals(2, model.getEntities().size());
		
		// entity 1
		//Entity entity_1 = getEntityByClassName(model, "domainEntity_1");
		Entity entity_1 = model.getEntityByClassName("domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getClassName());
		
		// attributes of entity 1
		assertTrue(entity_1.getAttributes().isEmpty());
		
		// entity 2
		//Entity entity_2 = getEntityByClassName(model, "domainEntity_2");
		Entity entity_2 = model.getEntityByClassName("domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getClassName());
		
		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
		
		Entity e = model.getEntityByClassName("domainEntity_1");
		assertEquals("", e.getDatabaseCatalog());
		assertEquals("", e.getDatabaseSchema());
		assertEquals("TABLE", e.getDatabaseType());
		assertEquals(0, e.getDatabaseForeignKeys().size());

		Entity e2 = model.getEntityByTableName("domainEntity_1");
		assertEquals("domainEntity_1", e2.getClassName());
	}
	
	@Test
	public void testAttributeWithNeutralTypes() {
		// Given
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");

		domainModel.addEntity(domainEntity_1);
//		DomainEntityField domainEntityField_1_2 = new DomainEntityField("field_1_2", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
//		DomainEntityField domainEntityField_1_4 = new DomainEntityField("field_1_4", DomainNeutralTypes.getType(DomainNeutralTypes.DATE));
//		DomainEntityField domainEntityField_1_5 = new DomainEntityField("field_1_5", DomainNeutralTypes.getType(DomainNeutralTypes.DECIMAL));
//		DomainEntityField domainEntityField_1_6 = new DomainEntityField("field_1_6", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
//		DomainEntityField domainEntityField_1_7 = new DomainEntityField("field_1_7", DomainNeutralTypes.getType(DomainNeutralTypes.STRING));
//		DomainEntityField domainEntityField_1_8 = new DomainEntityField("field_1_8", DomainNeutralTypes.getType(DomainNeutralTypes.TIME));
//		DomainEntityField domainEntityField_1_9 = new DomainEntityField("field_1_9", DomainNeutralTypes.getType(DomainNeutralTypes.TIMESTAMP));
//		domainEntity_1.addField(domainEntityField_1_2);
//		domainEntity_1.addField(domainEntityField_1_4);
//		domainEntity_1.addField(domainEntityField_1_5);
//		domainEntity_1.addField(domainEntityField_1_6);
//		domainEntity_1.addField(domainEntityField_1_7);
//		domainEntity_1.addField(domainEntityField_1_8);
//		domainEntity_1.addField(domainEntityField_1_9);

		domainEntity_1.addField( new DomainEntityField("myBoolean", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN)) );
		
		domainEntity_1.addField( new DomainEntityField("myDecimal", DomainNeutralTypes.getType(DomainNeutralTypes.DECIMAL)) );
		domainEntity_1.addField( new DomainEntityField("myInteger", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER)) );
		
		domainEntity_1.addField( new DomainEntityField("myString", DomainNeutralTypes.getType(DomainNeutralTypes.STRING)) );
		domainEntity_1.addField( new DomainEntityField("myLongText", DomainNeutralTypes.getType(DomainNeutralTypes.LONGTEXT_CLOB)) );
		
		domainEntity_1.addField( new DomainEntityField("myDate", DomainNeutralTypes.getType(DomainNeutralTypes.DATE)) );
		domainEntity_1.addField( new DomainEntityField("myTime", DomainNeutralTypes.getType(DomainNeutralTypes.TIME))  );
		domainEntity_1.addField( new DomainEntityField("myTimestamp", DomainNeutralTypes.getType(DomainNeutralTypes.TIMESTAMP)) );

		domainEntity_1.addField( new DomainEntityField("myBinary", DomainNeutralTypes.getType(DomainNeutralTypes.BINARY_BLOB)) );
		
		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);
		
		// When
		Model model = converter.convertToGenericModel(domainModel);
		
		// Then
		assertEquals(2, model.getEntities().size());
		
		// entity 1
//		Entity entity_1 = getEntityByClassName(model, "domainEntity_1");
		Entity entity_1 = model.getEntityByClassName("domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getClassName());
		
		// attributes of entity 1
		assertEquals(9, entity_1.getAttributes().size());
		
		assertEquals("Boolean",    getAttributeByName(entity_1, "myBoolean").getSimpleType());
		assertEquals("BigDecimal", getAttributeByName(entity_1, "myDecimal").getSimpleType());
		assertEquals("Integer",    getAttributeByName(entity_1, "myInteger").getSimpleType());
		assertEquals("String", getAttributeByName(entity_1, "myString").getSimpleType());
		assertEquals("String", getAttributeByName(entity_1, "myLongText").getSimpleType());
		assertEquals("Date",       getAttributeByName(entity_1, "myDate").getSimpleType());
		assertEquals("Date", getAttributeByName(entity_1, "myTime").getSimpleType());
		assertEquals("Date", getAttributeByName(entity_1, "myTimestamp").getSimpleType());
		assertEquals("byte[]", getAttributeByName(entity_1, "myBinary").getSimpleType());

		assertEquals("java.lang.Boolean", getAttributeByName(entity_1, "myBoolean").getFullType());
		assertEquals("java.util.Date", getAttributeByName(entity_1, "myDate").getFullType());
		assertEquals("java.math.BigDecimal", getAttributeByName(entity_1, "myDecimal").getFullType());
		assertEquals("java.lang.Integer", getAttributeByName(entity_1, "myInteger").getFullType());
		assertEquals("java.lang.String", getAttributeByName(entity_1, "myString").getFullType());
		assertEquals("java.util.Date", getAttributeByName(entity_1, "myTime").getFullType());
		assertEquals("java.util.Date", getAttributeByName(entity_1, "myTimestamp").getFullType());
		assertEquals("byte[]", getAttributeByName(entity_1, "myBinary").getFullType());

		// entity 2
//		Entity entity_2 = getEntityByClassName(model, "domainEntity_2");
		Entity entity_2 = model.getEntityByClassName("domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getClassName());
		
		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
	}

	@Test
	public void testAttributeWithAnnotations() {
		// Given
		DomainModel domainModel = new DomainModel("domainModel");
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");

		domainModel.addEntity(domainEntity_1);
		DomainEntityField domainEntityField_1_1 = new DomainEntityField("field_1_1", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_2 = new DomainEntityField("field_1_2", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_3 = new DomainEntityField("field_1_3", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_4 = new DomainEntityField("field_1_4", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_5 = new DomainEntityField("field_1_5", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_6 = new DomainEntityField("field_1_6", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_7 = new DomainEntityField("field_1_7", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainEntityField domainEntityField_1_8 = new DomainEntityField("field_1_8", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		domainEntity_1.addField(domainEntityField_1_1);
		domainEntity_1.addField(domainEntityField_1_2);
		domainEntity_1.addField(domainEntityField_1_3);
		domainEntity_1.addField(domainEntityField_1_4);
		domainEntity_1.addField(domainEntityField_1_5);
		domainEntity_1.addField(domainEntityField_1_6);
		domainEntity_1.addField(domainEntityField_1_7);
		domainEntity_1.addField(domainEntityField_1_8);
//		domainEntityField_1_1.addAnnotation(new DomainEntityFieldAnnotation("@Id"));
//		domainEntityField_1_2.addAnnotation(new DomainEntityFieldAnnotation("@NotNull"));
		domainEntityField_1_1.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.ID));
		domainEntityField_1_2.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.NOT_NULL));
//		domainEntityField_1_3.addAnnotation(new DomainEntityFieldAnnotation("@Min", "1"));
//		domainEntityField_1_4.addAnnotation(new DomainEntityFieldAnnotation("@Max", "2"));
		domainEntityField_1_3.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.MIN, new BigDecimal(1) ));
		domainEntityField_1_4.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.MAX, new BigDecimal(25) ));
//		domainEntityField_1_5.addAnnotation(new DomainEntityFieldAnnotation("@SizeMin", "3"));
//		domainEntityField_1_6.addAnnotation(new DomainEntityFieldAnnotation("@SizeMax", "4"));
		domainEntityField_1_5.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.SIZE_MIN, new Integer(3)  ));
		domainEntityField_1_6.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.SIZE_MAX, new Integer(4)  ));
//		domainEntityField_1_7.addAnnotation(new DomainEntityFieldAnnotation("@Past"));
//		domainEntityField_1_8.addAnnotation(new DomainEntityFieldAnnotation("@Future"));
		domainEntityField_1_7.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.PAST));
		domainEntityField_1_8.addAnnotation(new DomainEntityFieldAnnotation(AnnotationName.FUTURE));

		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);

		// When
		Model model = converter.convertToGenericModel(domainModel);

		// Then
		assertEquals(2, model.getEntities().size());

		// entity 1
		//Entity entity_1 = getEntityByClassName(model, "domainEntity_1");
		Entity entity_1 = model.getEntityByClassName("domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getClassName());

		// attributes of entity 1
		assertEquals(8, entity_1.getAttributes().size());

		assertEquals("java.lang.Boolean", getAttributeByName(entity_1, "field_1_2").getFullType());

		Attribute attribute_1_1 = getAttributeByName(entity_1, "field_1_1");
		Attribute attribute_1_2 = getAttributeByName(entity_1, "field_1_2");
		Attribute attribute_1_3 = getAttributeByName(entity_1, "field_1_3");
		Attribute attribute_1_4 = getAttributeByName(entity_1, "field_1_4");
		Attribute attribute_1_5 = getAttributeByName(entity_1, "field_1_5");
		Attribute attribute_1_6 = getAttributeByName(entity_1, "field_1_6");
		Attribute attribute_1_7 = getAttributeByName(entity_1, "field_1_7");
		Attribute attribute_1_8 = getAttributeByName(entity_1, "field_1_8");
		assertTrue(attribute_1_1.isKeyElement());
		assertTrue(attribute_1_2.isNotNull());
		assertEquals(new BigDecimal( 1), attribute_1_3.getMinValue());
		assertEquals(new BigDecimal(25), attribute_1_4.getMaxValue());
		assertEquals(Integer.valueOf(3), attribute_1_5.getMinLength());
		assertEquals(Integer.valueOf(4), attribute_1_6.getMaxLength());
		assertTrue(attribute_1_7.isDatePast());
		assertTrue(attribute_1_8.isDateFuture());

		// entity 2
//		Entity entity_2 = getEntityByClassName(model, "domainEntity_2");
		Entity entity_2 = model.getEntityByClassName("domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getClassName());

		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
	}

//	// @Test
//	public void testAttributeWithEnumeration() {
//		// Given
//		DomainModel domainModel = new DomainModel("domainModel");
//		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");
//
//		domainModel.addEntity(domainEntity_1);
//		DomainEntityField domainEntityField_1_1 = new DomainEntityField("field_1_1", new DomainEnumerationForString("enum_string"));
//		DomainEntityField domainEntityField_1_2 = new DomainEntityField("field_1_2", new DomainEnumerationForInteger("enum_integer"));
//		DomainEntityField domainEntityField_1_3 = new DomainEntityField("field_1_3", new DomainEnumerationForDecimal("enum_decimal"));
//		domainEntity_1.addField(domainEntityField_1_1);
//		domainEntity_1.addField(domainEntityField_1_2);
//		domainEntity_1.addField(domainEntityField_1_3);
//		
//		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
//		domainModel.addEntity(domainEntity_2);
//		
//		// When
//		Model model = converter.convertToGenericModel(domainModel);
//		
//		// Then
//		assertEquals("domainModel", model.getName());
//		assertEquals(2, model.getEntities().size());
//		
//		// entity 1
//		Entity entity_1 = getEntityByClassName(model, "domainEntity_1");
//		assertEquals("domainEntity_1", entity_1.getClassName());
//		
//		// TODO
//		fail("TODO : Enumerations");
//
//	}
	
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
		assertEquals(2, model.getEntities().size());
		
		// entity 1
//		GenericEntity entity_1 = (GenericEntity) getEntityByClassName(model, "domainEntity_1");
		GenericEntity entity_1 = (GenericEntity) model.getEntityByClassName("domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getClassName());

//		GenericEntity entity_2 = (GenericEntity) getEntityByClassName(model, "domainEntity_2");
		GenericEntity entity_2 = (GenericEntity) model.getEntityByClassName("domainEntity_2");
		//assertNull( entity_2.getDatabaseTable());
		assertEquals("domainEntity_2", entity_2.getDatabaseTable() );
		assertEquals("domainEntity_2", entity_1.getLinks().get(0).getTargetEntityClassName());
		
		//--- Get by class name
		assertNotNull(model.getEntityByClassName("domainEntity_1"));
		assertNotNull(model.getEntityByClassName("domainEntity_2"));
		
		//--- Get by table name ( table name = class name )
		assertNotNull(model.getEntityByTableName("domainEntity_1"));
		assertNotNull(model.getEntityByTableName("domainEntity_2"));
		
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
