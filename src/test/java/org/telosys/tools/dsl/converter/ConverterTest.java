package org.telosys.tools.dsl.converter;

import java.math.BigDecimal;

import org.junit.Test;
import org.telosys.tools.dsl.DslModelError;
import org.telosys.tools.dsl.DslModelErrors;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.model.DslModelEntity;
import org.telosys.tools.dsl.parser.annotation.AnnotationName;
import org.telosys.tools.dsl.parser.model.DomainAnnotation;
import org.telosys.tools.dsl.parser.model.DomainCardinality;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityType;
import org.telosys.tools.dsl.parser.model.DomainField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainType;
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

	private static final String     MODEL_NAME = "test" ;
//	private static final Properties MODEL_PROPERTIES = null ;
	
	private static final String     ENTITY1 = "Entity1" ;
	private static final String     ENTITY2 = "Entity2" ;

	private void print(String s) {
		System.out.println(s);
	}
	private ModelConverter getModelConverter() {
		DslModelErrors errors = new DslModelErrors();
		return new ModelConverter(errors);
	}
	
	private DomainField buildField(String fieldName, String fieldType, DomainCardinality cardinality) {
		int lineNumber = 1 ;
		DomainType neutralType = DomainNeutralTypes.getType(fieldType);
		if ( neutralType != null) {
			// Field with basic type ( string, int, etc )
			return new DomainField(lineNumber, fieldName, neutralType );
		}
		else {
			// Field with entity type ( Car, Teacher, etc )
			DomainType entityType = new DomainEntityType(fieldType, cardinality) ;
			return new DomainField(lineNumber, fieldName, entityType );
		}
	}
	private DomainField buildField(String fieldName, String fieldType) {
		return buildField(fieldName, fieldType, DomainCardinality.ONE);
	}	
	private DomainField buildFieldMany(String fieldName, String fieldType) {
		return buildField(fieldName, fieldType, DomainCardinality.MANY);
	}	

	@Test
	public void testEmptyModel() {
		DomainModel domainModel = new DomainModel(MODEL_NAME, new ModelInfo());
		ModelConverter converter = getModelConverter();
		Model model = converter.convertModel(domainModel);
		
		assertEquals("test", model.getName());
		assertTrue(model.getEntities().isEmpty());		
		assertEquals(ModelType.DOMAIN_SPECIFIC_LANGUAGE, model.getType() );
	}
	
	private Model buildModelWithTwoEmptyEntities() {
		DomainModel domainModel = new DomainModel(MODEL_NAME, new ModelInfo());
		domainModel.addEntity(new DomainEntity(ENTITY1));
		domainModel.addEntity(new DomainEntity(ENTITY2));		
		//--- Convert 
		ModelConverter converter = getModelConverter();
		return converter.convertModel(domainModel);	
	}
	
	@Test
	public void testEmptyEntity() {
		Model model = buildModelWithTwoEmptyEntities();

		assertEquals(2, model.getEntities().size());
		
		// entity 1
		Entity entity1 = model.getEntityByClassName(ENTITY1);
		assertEquals(ENTITY1, entity1.getClassName());
		
		// attributes of entity 1
		assertTrue(entity1.getAttributes().isEmpty());
		
		// entity 2
		Entity entity2 = model.getEntityByClassName(ENTITY2);
		assertEquals(ENTITY2, entity2.getClassName());
		
		// attributes of entity 2
		assertTrue(entity2.getAttributes().isEmpty());
		
		Entity e = model.getEntityByClassName(ENTITY1);
		assertEquals("", e.getDatabaseCatalog());
		assertEquals("", e.getDatabaseSchema());
		assertEquals("TABLE", e.getDatabaseType());
		assertEquals(0, e.getForeignKeys().size());

// removed in v 3.4.0 (no default table name)
//		Entity e2 = model.getEntityByTableName(ENTITY1);
//		assertEquals(ENTITY1, e2.getClassName());
	}
	
	@Test
	public void testAttributeWithNeutralTypes() throws DslModelError {
		// Given
		DomainModel domainModel = new DomainModel(MODEL_NAME, new ModelInfo());
		DomainEntity domainEntity1 = new DomainEntity(ENTITY1);

		domainModel.addEntity(domainEntity1);
		domainEntity1.addField( buildField("myBoolean", DomainNeutralTypes.BOOLEAN) );
		
		domainEntity1.addField( buildField("myDecimal", DomainNeutralTypes.DECIMAL) );
		domainEntity1.addField( buildField("myInteger", DomainNeutralTypes.INTEGER) );
		
		domainEntity1.addField( buildField("myString", DomainNeutralTypes.STRING) );
		
		domainEntity1.addField( buildField("myDate", DomainNeutralTypes.DATE) );
		domainEntity1.addField( buildField("myTime", DomainNeutralTypes.TIME) );
		domainEntity1.addField( buildField("myTimestamp", DomainNeutralTypes.TIMESTAMP));

		domainEntity1.addField( buildField("myBinary", DomainNeutralTypes.BINARY_BLOB) );
		
		DomainEntity domainEntity2 = new DomainEntity(ENTITY2);
		domainModel.addEntity(domainEntity2);
		
		// When
		ModelConverter converter = getModelConverter();
		Model model = converter.convertModel(domainModel);
		
		// Then
		assertEquals(2, model.getEntities().size());
		
		// entity 1
		Entity entity1 = model.getEntityByClassName(ENTITY1);
		assertEquals(ENTITY1, entity1.getClassName());
		
		// attributes of entity 1
		assertEquals(8, entity1.getAttributes().size());
		
		// entity 2
		Entity entity2 = model.getEntityByClassName(ENTITY2);
		assertEquals(ENTITY2, entity2.getClassName());
		
		// attributes of entity 2
		assertTrue(entity2.getAttributes().isEmpty());
	}

	@Test
	public void testAttributeWithAnnotations() throws DslModelError {
		DomainModel domainModel = new DomainModel(MODEL_NAME, new ModelInfo());
		DomainEntity domainEntity1 = new DomainEntity(ENTITY1);

		domainModel.addEntity(domainEntity1);
		DomainField domainField1A = buildField("field_1_1", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1B = buildField("field_1_2", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1C = buildField("field_1_3", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1D = buildField("field_1_4", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1E = buildField("field_1_5", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1F = buildField("field_1_6", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1G = buildField("field_1_7", DomainNeutralTypes.BOOLEAN);
		DomainField domainField1H = buildField("field_1_8", DomainNeutralTypes.BOOLEAN);
		domainEntity1.addField(domainField1A);
		domainEntity1.addField(domainField1B);
		domainEntity1.addField(domainField1C);
		domainEntity1.addField(domainField1D);
		domainEntity1.addField(domainField1E);
		domainEntity1.addField(domainField1F);
		domainEntity1.addField(domainField1G);
		domainEntity1.addField(domainField1H);
		domainField1A.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		domainField1B.addAnnotation(new DomainAnnotation(AnnotationName.NOT_NULL));
		domainField1C.addAnnotation(new DomainAnnotation(AnnotationName.MIN, new BigDecimal(1) ));
		domainField1D.addAnnotation(new DomainAnnotation(AnnotationName.MAX, new BigDecimal(25) ));
		domainField1E.addAnnotation(new DomainAnnotation(AnnotationName.SIZE_MIN, Integer.valueOf(3)  ));
		domainField1F.addAnnotation(new DomainAnnotation(AnnotationName.SIZE_MAX, Integer.valueOf(4)  ));
		domainField1G.addAnnotation(new DomainAnnotation(AnnotationName.PAST));
		domainField1H.addAnnotation(new DomainAnnotation(AnnotationName.FUTURE));

		DomainEntity domainEntity2 = new DomainEntity(ENTITY2);
		domainModel.addEntity(domainEntity2);

		ModelConverter converter = getModelConverter();
		Model model = converter.convertModel(domainModel);

		assertEquals(2, model.getEntities().size());

		// entity 1
		Entity entity1 = model.getEntityByClassName(ENTITY1);
		assertEquals(ENTITY1, entity1.getClassName());

		// attributes of entity 1
		assertEquals(8, entity1.getAttributes().size());

		Attribute attribute1A = getAttributeByName(entity1, "field_1_1");
		Attribute attribute1B = getAttributeByName(entity1, "field_1_2");
		Attribute attribute1C = getAttributeByName(entity1, "field_1_3");
		Attribute attribute1D = getAttributeByName(entity1, "field_1_4");
		Attribute attribute1E = getAttributeByName(entity1, "field_1_5");
		Attribute attribute1F = getAttributeByName(entity1, "field_1_6");
		Attribute attribute1G = getAttributeByName(entity1, "field_1_7");
		Attribute attribute1H = getAttributeByName(entity1, "field_1_8");
		
		// boolean @Id 
		assertTrue(attribute1A.isKeyElement());
		
		// boolean @NotNull => primitive type
		assertTrue(attribute1B.isNotNull());
		
		assertEquals(new BigDecimal( 1), attribute1C.getMinValue());
		
		assertEquals(new BigDecimal(25), attribute1D.getMaxValue());
		
		assertEquals(Integer.valueOf(3), attribute1E.getMinLength());
		
		assertEquals(Integer.valueOf(4), attribute1F.getMaxLength());
		assertTrue(attribute1G.isDatePast());
		assertTrue(attribute1H.isDateFuture());

		// entity 2
		Entity entity2 = model.getEntityByClassName(ENTITY2);
		assertEquals(ENTITY2, entity2.getClassName());

		// attributes of entity 2
		assertTrue(entity2.getAttributes().isEmpty());
	}
	
//	private DomainType getType(String typeName) {
//		DomainType type = DomainNeutralTypes.getType(typeName);
//		if ( type != null ) {
//			return type;
//		}
//		else {
//			throw new IllegalStateException("No type " + typeName);
//		}
//	}

	private DomainModel buildFullModel() throws DslModelError {
		DomainModel domainModel = new DomainModel(MODEL_NAME, new ModelInfo());

		DomainEntity carEntity = new DomainEntity("Car");
		DomainEntity driverEntity = new DomainEntity("Driver");
		DomainEntity groupEntity = new DomainEntity("Group");
		
		//--- "Driver" entity
//		DomainField driverCode = new DomainField("code", getType("long"));
		DomainField driverCode = buildField("code", DomainNeutralTypes.LONG);
		driverCode.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		driverCode.addAnnotation(new DomainAnnotation(AnnotationName.SIZE_MAX, Integer.valueOf(20) ));
		driverEntity.addField(driverCode);
		
//		driverEntity.addField( new DomainField("firstName", getType("string") ) );
		driverEntity.addField( buildField("firstName", DomainNeutralTypes.STRING) );
		
//		driverEntity.addField( new DomainField("lastName",  getType("string") ) );
		driverEntity.addField( buildField("lastName", DomainNeutralTypes.STRING ) );

//		driverEntity.addField( new DomainField(0, "car",  new DomainEntityType("Car") , 1 ) );
		driverEntity.addField( buildField("car", "Car" ) );

		//--- "Car" entity referencing "Driver" entity
//		DomainField carId = new DomainField("id",     getType("short") );
		DomainField carId = buildField("id", DomainNeutralTypes.SHORT) ;
		carId.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		carEntity.addField(carId);
		
//		carEntity.addField(new DomainField("name",   getType("string")) );
		carEntity.addField(buildField("name",  DomainNeutralTypes.STRING) );

//		carEntity.addField(new DomainField("driver", new DomainEntityType("Driver")) ); // Reference to "Driver"
		carEntity.addField(buildField("driver", "Driver") ); // Reference to "Driver"
		
		//--- "Group" entity referencing N "Driver" entity
//		DomainField groupId = new DomainField("id",     getType("int"));
		DomainField groupId = buildField("id",  DomainNeutralTypes.INTEGER);
		groupId.addAnnotation(new DomainAnnotation(AnnotationName.ID));
		groupEntity.addField(groupId);
		
//		groupEntity.addField(new DomainField("name",   getType("string")) );
		groupEntity.addField(buildField("name", DomainNeutralTypes.STRING) );
//		groupEntity.addField(new DomainField(0, "drivers", new DomainEntityType("Driver"), -1) ); // Reference to "Driver"
		groupEntity.addField(buildFieldMany("drivers", "Driver") ); // Reference to "Driver"
		
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
	public void testFullModel() throws DslModelError {
		DomainModel domainModel = buildFullModel() ;
		ModelConverter converter = getModelConverter();
		Model model = converter.convertModel(domainModel);
		
		//--- "Car" entity
		checkCarEntity((DslModelEntity) model.getEntityByClassName("Car"));
		
		//--- "Driver" entity
		checkDriverEntity((DslModelEntity) model.getEntityByClassName("Driver"));
		
		//--- "Group" entity
		checkGroupEntity((DslModelEntity) model.getEntityByClassName("Group"));

		//--- Get by class name
		assertNotNull(model.getEntityByClassName("Car"));
		assertNotNull(model.getEntityByClassName("Driver")); 
		
		//--- Get by table name ( table name = class name )
// removed in v 3.4.0 (no default table nale)
//		assertNotNull(model.getEntityByTableName("Car"));
//		assertNotNull(model.getEntityByTableName("Driver"));
	}
	
	/**
	 * Check "Car" entity
	 * @param carEntity
	 */
	private void checkCarEntity(DslModelEntity carEntity) {

		print("check 'Car' entity...");
		
		assertEquals("Car", carEntity.getClassName());
//		assertEquals("Car", carEntity.getDatabaseTable() );
		assertEquals("", carEntity.getDatabaseTable() ); // v 3.4.0
		
		//--- "Car" attributes
		for ( Attribute attribute : carEntity.getAttributes() ) {
			print(" . " + attribute.getName() );
		}
		assertEquals(2, carEntity.getAttributes().size() );
		assertNotNull(carEntity.getAttributeByName("id"));
		assertNotNull(carEntity.getAttributeByName("name"));
		//assertNotNull(carEntity.getAttributeByName("driver")); // LINK

		//--- "Car" - "name" attribute
		Attribute attribute = carEntity.getAttributeByName("name");
		assertNotNull(attribute);
		assertEquals("string", attribute.getNeutralType() );
		assertFalse(attribute.isFK()); 
		assertFalse(attribute.isFKSimple()); 
		assertFalse(attribute.isFKComposite());
		
		//--- "Car" links
		assertEquals(1, carEntity.getLinks().size()); 
		
		Link driverLink = carEntity.getLinks().get(0);
		assertEquals(Cardinality.MANY_TO_ONE, driverLink.getCardinality() );
		assertEquals("Driver", driverLink.getReferencedEntityName());
	}
	
	/**
	 * Check "Driver" entity
	 * @param driverEntity
	 */
	private void checkDriverEntity(DslModelEntity driverEntity) {
		print("check 'Driver' entity...");
		assertEquals("Driver", driverEntity.getClassName() );
//		assertEquals("Driver", driverEntity.getDatabaseTable() );
		assertEquals("", driverEntity.getDatabaseTable() ); // V 3.4.0
		//--- Attributes
		for ( Attribute attribute : driverEntity.getAttributes() ) {
			print(" . " + attribute.getName() );
		}
		//--- Links
		assertEquals(1, driverEntity.getLinks().size());
		Link car = driverEntity.getLinks().get(0);
		assertEquals(Cardinality.MANY_TO_ONE, car.getCardinality() );
		assertEquals("Car", car.getReferencedEntityName());
	}
	
	/**
	 * Check "Group" entity
	 * @param groupEntity
	 */
	private void checkGroupEntity(DslModelEntity groupEntity) {
		print("check 'Group' entity...");
		assertEquals("Group", groupEntity.getClassName() );
//		assertEquals("Group", groupEntity.getDatabaseTable() );
		assertEquals("", groupEntity.getDatabaseTable() ); // v 3.4.0
		
		//--- Attributes
		for ( Attribute attribute : groupEntity.getAttributes() ) {
			print(" . " + attribute.getName() );
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
		assertEquals("Driver", drivers.getReferencedEntityName());
	}
	
	private Attribute getAttributeByName(Entity entity, String name) {
		for(Attribute attribute : entity.getAttributes()) {
			if(name.equals(attribute.getName())) {
				return attribute;
			}
		}
		throw new IllegalStateException("Attribute '" + name + "' not found");
	}
}
