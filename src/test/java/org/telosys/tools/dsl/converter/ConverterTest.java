package org.telosys.tools.dsl.converter;

import java.math.BigDecimal;
import java.util.Properties;

import org.junit.Test;
import org.telosys.tools.dsl.AnnotationName;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralType;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.generic.model.Attribute;
import org.telosys.tools.generic.model.Entity;
import org.telosys.tools.generic.model.Link;
import org.telosys.tools.generic.model.Model;
import org.telosys.tools.generic.model.enums.Cardinality;
import org.telosys.tools.generic.model.enums.ModelType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ConverterTest {

	private final static String     MODEL_FILE_NAME = "test.model" ;
	private final static Properties MODEL_PROPERTIES = null ;
	
	private Converter converter = new Converter();

	@Test
	public void testEmptyModel() {
		DomainModel domainModel = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
		Model model = converter.convertToGenericModel(domainModel);
		
		assertEquals("test", model.getName());
		assertTrue(model.getEntities().isEmpty());		
		assertEquals(ModelType.DOMAIN_SPECIFIC_LANGUAGE, model.getType() );
	}
	
	private Model buildModelWithTwoEmptyEntities() {
		DomainModel domainModel = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
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
		DomainModel domainModel = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
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

		domainEntity_1.addField( new DomainField("myBoolean", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN)) );
		
		domainEntity_1.addField( new DomainField("myDecimal", DomainNeutralTypes.getType(DomainNeutralTypes.DECIMAL)) );
		domainEntity_1.addField( new DomainField("myInteger", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER)) );
		
		domainEntity_1.addField( new DomainField("myString", DomainNeutralTypes.getType(DomainNeutralTypes.STRING)) );
		
		domainEntity_1.addField( new DomainField("myDate", DomainNeutralTypes.getType(DomainNeutralTypes.DATE)) );
		domainEntity_1.addField( new DomainField("myTime", DomainNeutralTypes.getType(DomainNeutralTypes.TIME))  );
		domainEntity_1.addField( new DomainField("myTimestamp", DomainNeutralTypes.getType(DomainNeutralTypes.TIMESTAMP)) );

		domainEntity_1.addField( new DomainField("myBinary", DomainNeutralTypes.getType(DomainNeutralTypes.BINARY_BLOB)) );
		
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
		assertEquals(8, entity_1.getAttributes().size());
		
//		// Check simple type
//		assertEquals("Boolean",    getAttributeByName(entity_1, "myBoolean").getSimpleType());
//		assertEquals("BigDecimal", getAttributeByName(entity_1, "myDecimal").getSimpleType());
//		assertEquals("Integer",    getAttributeByName(entity_1, "myInteger").getSimpleType());
//		assertEquals("String",     getAttributeByName(entity_1, "myString").getSimpleType());
//		assertEquals("Date",       getAttributeByName(entity_1, "myDate").getSimpleType());
//		assertEquals("Date",       getAttributeByName(entity_1, "myTime").getSimpleType());
//		assertEquals("Date",       getAttributeByName(entity_1, "myTimestamp").getSimpleType());
//		assertEquals("byte[]",     getAttributeByName(entity_1, "myBinary").getSimpleType());

//		// Check full type
//		assertEquals("java.lang.Boolean",    getAttributeByName(entity_1, "myBoolean").getFullType());
//		assertEquals("java.math.BigDecimal", getAttributeByName(entity_1, "myDecimal").getFullType());
//		assertEquals("java.lang.Integer",    getAttributeByName(entity_1, "myInteger").getFullType());
//		assertEquals("java.lang.String",     getAttributeByName(entity_1, "myString").getFullType());
//		assertEquals("java.util.Date",       getAttributeByName(entity_1, "myDate").getFullType());
//		assertEquals("java.util.Date",       getAttributeByName(entity_1, "myTime").getFullType());
//		assertEquals("java.util.Date",       getAttributeByName(entity_1, "myTimestamp").getFullType());
//		assertEquals("byte[]",               getAttributeByName(entity_1, "myBinary").getFullType());

		// entity 2
//		Entity entity_2 = getEntityByClassName(model, "domainEntity_2");
		Entity entity_2 = model.getEntityByClassName("domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getClassName());
		
		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
	}

	@Test
	public void testAttributeWithAnnotations() { // throws AnnotationOrTagError {
		DomainModel domainModel = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);
		DomainEntity domainEntity_1 = new DomainEntity("domainEntity_1");

		domainModel.addEntity(domainEntity_1);
		DomainField domainEntityField_1_1 = new DomainField("field_1_1", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_2 = new DomainField("field_1_2", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_3 = new DomainField("field_1_3", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_4 = new DomainField("field_1_4", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_5 = new DomainField("field_1_5", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_6 = new DomainField("field_1_6", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_7 = new DomainField("field_1_7", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
		DomainField domainEntityField_1_8 = new DomainField("field_1_8", DomainNeutralTypes.getType(DomainNeutralTypes.BOOLEAN));
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
		domainEntityField_1_1.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		domainEntityField_1_2.addAnnotation(new DomainAnnotation(AnnotationName.NOT_NULL));
//		domainEntityField_1_3.addAnnotation(new DomainEntityFieldAnnotation("@Min", "1"));
//		domainEntityField_1_4.addAnnotation(new DomainEntityFieldAnnotation("@Max", "2"));
		domainEntityField_1_3.addAnnotation(new DomainAnnotation(AnnotationName.MIN, new BigDecimal(1) ));
		domainEntityField_1_4.addAnnotation(new DomainAnnotation(AnnotationName.MAX, new BigDecimal(25) ));
//		domainEntityField_1_5.addAnnotation(new DomainEntityFieldAnnotation("@SizeMin", "3"));
//		domainEntityField_1_6.addAnnotation(new DomainEntityFieldAnnotation("@SizeMax", "4"));
		domainEntityField_1_5.addAnnotation(new DomainAnnotation(AnnotationName.SIZE_MIN, new Integer(3)  ));
		domainEntityField_1_6.addAnnotation(new DomainAnnotation(AnnotationName.SIZE_MAX, new Integer(4)  ));
//		domainEntityField_1_7.addAnnotation(new DomainEntityFieldAnnotation("@Past"));
//		domainEntityField_1_8.addAnnotation(new DomainEntityFieldAnnotation("@Future"));
		domainEntityField_1_7.addAnnotation(new DomainAnnotation(AnnotationName.PAST));
		domainEntityField_1_8.addAnnotation(new DomainAnnotation(AnnotationName.FUTURE));

		DomainEntity domainEntity_2 = new DomainEntity("domainEntity_2");
		domainModel.addEntity(domainEntity_2);

		Model model = converter.convertToGenericModel(domainModel);

		assertEquals(2, model.getEntities().size());

		// entity 1
		//Entity entity_1 = getEntityByClassName(model, "domainEntity_1");
		Entity entity_1 = model.getEntityByClassName("domainEntity_1");
		assertEquals("domainEntity_1", entity_1.getClassName());

		// attributes of entity 1
		assertEquals(8, entity_1.getAttributes().size());

		Attribute attribute_1_1 = getAttributeByName(entity_1, "field_1_1");
		Attribute attribute_1_2 = getAttributeByName(entity_1, "field_1_2");
		Attribute attribute_1_3 = getAttributeByName(entity_1, "field_1_3");
		Attribute attribute_1_4 = getAttributeByName(entity_1, "field_1_4");
		Attribute attribute_1_5 = getAttributeByName(entity_1, "field_1_5");
		Attribute attribute_1_6 = getAttributeByName(entity_1, "field_1_6");
		Attribute attribute_1_7 = getAttributeByName(entity_1, "field_1_7");
		Attribute attribute_1_8 = getAttributeByName(entity_1, "field_1_8");
		
		// boolean @Id 
		assertTrue(attribute_1_1.isKeyElement());
		
		// boolean @NotNull => primitive type
		assertTrue(attribute_1_2.isNotNull());
		
		assertEquals(new BigDecimal( 1), attribute_1_3.getMinValue());
		
		assertEquals(new BigDecimal(25), attribute_1_4.getMaxValue());
		
		assertEquals(Integer.valueOf(3), attribute_1_5.getMinLength());
		
		assertEquals(Integer.valueOf(4), attribute_1_6.getMaxLength());
		assertTrue(attribute_1_7.isDatePast());
		assertTrue(attribute_1_8.isDateFuture());

		// entity 2
		Entity entity_2 = model.getEntityByClassName("domainEntity_2");
		assertEquals("domainEntity_2", entity_2.getClassName());

		// attributes of entity 2
		assertTrue(entity_2.getAttributes().isEmpty());
	}

	private DomainModel buildFullModel() { // throws AnnotationOrTagError {
		DomainModel domainModel = new DomainModel(MODEL_FILE_NAME, MODEL_PROPERTIES);

		DomainEntity carEntity = new DomainEntity("Car");
		DomainEntity driverEntity = new DomainEntity("Driver");
		DomainEntity groupEntity = new DomainEntity("Group");
		
		//--- "Driver" entity
		DomainField driverCode = new DomainField("code", new DomainNeutralType("long"));
		driverCode.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		driverCode.addAnnotation(new DomainAnnotation(AnnotationName.SIZE_MAX, new Integer(20) ));
		//driverCode.setAnnotationList(annotationList);
		driverEntity.addField(driverCode);
		
		driverEntity.addField( new DomainField("firstName", new DomainNeutralType("string") ) );
		driverEntity.addField( new DomainField("lastName",  new DomainNeutralType("string") ) );
		driverEntity.addField( new DomainField(0, "car",  new DomainEntityType("Car") , 1 ) );

		//--- "Car" entity referencing "Driver" entity
		DomainField carId = new DomainField("id",     new DomainNeutralType("short") );
		carId.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		carEntity.addField(carId);
		
		carEntity.addField(new DomainField("name",   new DomainNeutralType("string")) );
		carEntity.addField(new DomainField("driver", new DomainEntityType("Driver")) ); // Reference to "Driver"
		
		//--- "Group" entity referencing N "Driver" entity
		DomainField groupId = new DomainField("id",     new DomainNeutralType("int"));
		groupId.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		groupEntity.addField(groupId);
		
		groupEntity.addField(new DomainField("name",   new DomainNeutralType("string")) );
		groupEntity.addField(new DomainField(0, "drivers", new DomainEntityType("Driver"), -1) ); // Reference to "Driver"
		
		//--- Add entities to model
		domainModel.addEntity(carEntity);
		domainModel.addEntity(driverEntity);
		domainModel.addEntity(groupEntity);
		
		//--- Check original model 
		// Number of entities
		assertEquals(3, domainModel.getEntities().size() );
		// Number of fields
		assertEquals(4, driverEntity.getFields().size() );
		assertEquals(3, carEntity.getFields().size() );
		assertEquals(3, groupEntity.getFields().size() );
		
		return domainModel ;
	}
	
	@Test
	public void testFullModel() { // throws AnnotationOrTagError {
		DomainModel domainModel = buildFullModel() ;
		Model model = converter.convertToGenericModel(domainModel);
		
		//--- "Car" entity
		checkCarEntity((DslModelEntity) model.getEntityByClassName("Car"));
		
		//--- "Driver" entity
//		GenericEntity driverEntity = (GenericEntity) model.getEntityByClassName("Driver");
//		//assertNull( entity_2.getDatabaseTable());
//		assertEquals("Driver", driverEntity.getDatabaseTable() );
		checkDriverEntity((DslModelEntity) model.getEntityByClassName("Driver"));
		
		//--- "Group" entity
		checkGroupEntity((DslModelEntity) model.getEntityByClassName("Group"));

//		GenericEntity carEntity = (GenericEntity) model.getEntityByClassName("Car");
//		assertEquals("Car", carEntity.getClassName());
//		for ( Attribute attribute : carEntity.getAttributes() ) {
//			System.out.println(" . " + attribute.getName() );
//		}
//		assertEquals(3, carEntity.getAttributes().size() );
//		assertNotNull(carEntity.getAttributeByName("id"));
//		assertNotNull(carEntity.getAttributeByName("name"));
//		assertNotNull(carEntity.getAttributeByName("driver"));
//
//		//--- "Car" - "name" attribute
//		Attribute car_nameAttribute = carEntity.getAttributeByName("name");
//		assertNotNull(car_nameAttribute);
//		assertEquals("string", car_nameAttribute.getNeutralType() );
//		assertFalse(car_nameAttribute.isFK()); 
//		assertFalse(car_nameAttribute.isFKSimple()); 
//		assertFalse(car_nameAttribute.isFKComposite());
//		
//		//--- "Car" - "driver" attribute ( "pseudo FK" )
//		Attribute car_driverAttribute = carEntity.getAttributeByName("driver");
//		assertNotNull(car_driverAttribute);
//		assertEquals("long", car_driverAttribute.getNeutralType() );
//		//assertEquals("driverCode", driverAttribute.getLabel() ); // "driver" --> "driverCode" TODO ?
//		assertEquals(Integer.valueOf(20), car_driverAttribute.getMaxLength() );
		
//		//Attribute driverFK = carEntity.getAttributeByName("driver"); // Reference to "Driver"
//		//assertNotNull(driverFK);
//		assertEquals("long", car_driverAttribute.getNeutralType() ); // same as original attribute
//		assertEquals(car_driverAttribute.getName(), car_driverAttribute.getLabel() ); 
//		assertEquals(Integer.valueOf(20), car_driverAttribute.getMaxLength() ); // same as original attribute
//		
//		assertTrue(car_driverAttribute.isFK()); // is "FK"
//		assertTrue(car_driverAttribute.isFKSimple()); // is "Simple FK" (is the only attribute in the FK)
//		assertFalse(car_driverAttribute.isFKComposite());
//		
//		//--- "Car" links
//		assertEquals(1, carEntity.getLinks().size());
//		assertEquals("Driver", carEntity.getLinks().get(0).getTargetEntityClassName());

		//--- Get by class name
		assertNotNull(model.getEntityByClassName("Car"));
		assertNotNull(model.getEntityByClassName("Driver")); 
		
		//--- Get by table name ( table name = class name )
		assertNotNull(model.getEntityByTableName("Car"));
		assertNotNull(model.getEntityByTableName("Driver"));
		
	}
	
	/**
	 * Check "Car" entity
	 * @param carEntity
	 */
	private void checkCarEntity(DslModelEntity carEntity) {

		System.out.println("check 'Car' entity...");
		
		assertEquals("Car", carEntity.getClassName());
		assertEquals("Car", carEntity.getDatabaseTable() );
		
		//--- "Car" attributes
		for ( Attribute attribute : carEntity.getAttributes() ) {
			System.out.println(" . " + attribute.getName() );
		}
		assertEquals(2, carEntity.getAttributes().size() );
		assertNotNull(carEntity.getAttributeByName("id"));
		assertNotNull(carEntity.getAttributeByName("name"));
		//assertNotNull(carEntity.getAttributeByName("driver")); // LINK

		//--- "Car" - "name" attribute
		Attribute car_nameAttribute = carEntity.getAttributeByName("name");
		assertNotNull(car_nameAttribute);
		assertEquals("string", car_nameAttribute.getNeutralType() );
		assertFalse(car_nameAttribute.isFK()); 
		assertFalse(car_nameAttribute.isFKSimple()); 
		assertFalse(car_nameAttribute.isFKComposite());
		
//		//--- "Car" - "driver" attribute ( "pseudo FK" )
//		Attribute car_driverAttribute = carEntity.getAttributeByName("driver");
//		assertNotNull(car_driverAttribute);
//		assertEquals("long", car_driverAttribute.getNeutralType() );
//		//assertEquals("driverCode", driverAttribute.getLabel() ); // "driver" --> "driverCode" TODO ?
//		assertEquals(Integer.valueOf(20), car_driverAttribute.getMaxLength() );
//		assertEquals("long", car_driverAttribute.getNeutralType() ); // same as original attribute
//		assertEquals(car_driverAttribute.getName(), car_driverAttribute.getLabel() ); 
//		assertEquals(Integer.valueOf(20), car_driverAttribute.getMaxLength() ); // same as original attribute
//		
//		assertTrue(car_driverAttribute.isFK()); // is "FK"
//		assertTrue(car_driverAttribute.isFKSimple()); // is "Simple FK" (is the only attribute in the FK)
//		assertFalse(car_driverAttribute.isFKComposite());
		
		//--- "Car" links
		assertEquals(1, carEntity.getLinks().size()); 
		
		Link driverLink = carEntity.getLinks().get(0);
		assertEquals(Cardinality.MANY_TO_ONE, driverLink.getCardinality() );
		assertEquals("Driver", driverLink.getTargetEntityClassName());
		
	}
	
	/**
	 * Check "Driver" entity
	 * @param driverEntity
	 */
	private void checkDriverEntity(DslModelEntity driverEntity) {
		System.out.println("check 'Driver' entity...");
		assertEquals("Driver", driverEntity.getClassName() );
		assertEquals("Driver", driverEntity.getDatabaseTable() );
		//--- Attributes
		for ( Attribute attribute : driverEntity.getAttributes() ) {
			System.out.println(" . " + attribute.getName() );
		}

		//--- Links
		assertEquals(1, driverEntity.getLinks().size());
		Link car = driverEntity.getLinks().get(0);
		assertEquals(Cardinality.MANY_TO_ONE, car.getCardinality() );
		assertEquals("Car", car.getTargetEntityClassName());
	}
	
	/**
	 * Check "Group" entity
	 * @param groupEntity
	 */
	private void checkGroupEntity(DslModelEntity groupEntity) {
		System.out.println("check 'Group' entity...");
		assertEquals("Group", groupEntity.getClassName() );
		assertEquals("Group", groupEntity.getDatabaseTable() );
		
		//--- Attributes
		for ( Attribute attribute : groupEntity.getAttributes() ) {
			System.out.println(" . " + attribute.getName() );
		}

		assertEquals(2, groupEntity.getAttributes().size() ); // "drivers" is not a "pseudo FK attribute" => not in attributes list
		assertNotNull(groupEntity.getAttributeByName("id"));
		assertNotNull(groupEntity.getAttributeByName("name"));
		assertNull(groupEntity.getAttributeByName("drivers")); // not an attribute (cardinality is 0..N)

		//--- "id" attribute
		Attribute id = groupEntity.getAttributeByName("id");
		assertNotNull(id);
		assertEquals("int", id.getNeutralType() );
		assertTrue(id.isKeyElement()); 
		assertFalse(id.isFK()); 
		assertFalse(id.isFKSimple()); 
		assertFalse(id.isFKComposite());
		
		//--- "name" attribute
		Attribute name = groupEntity.getAttributeByName("name");
		assertNotNull(name);
		assertEquals("string", name.getNeutralType() );
		assertFalse(name.isKeyElement()); 
		assertFalse(name.isFK()); 
		assertFalse(name.isFKSimple()); 
		assertFalse(name.isFKComposite());
		
		//--- Links
		assertEquals(1, groupEntity.getLinks().size());
		Link drivers = groupEntity.getLinks().get(0);
		assertEquals(Cardinality.ONE_TO_MANY, drivers.getCardinality() );
		assertEquals("Driver", drivers.getTargetEntityClassName());
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
