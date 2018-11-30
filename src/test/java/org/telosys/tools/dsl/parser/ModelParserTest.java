package org.telosys.tools.dsl.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.model.DomainTypeNature;

public class ModelParserTest {

	private final void compareModels(DomainModel model1, DomainModel model2) {
		assertEquals ( model1.getNumberOfEntities(), model2.getNumberOfEntities() ) ;
		for ( DomainEntity e1 : model1.getEntities() ) {
			DomainEntity e2 = model2.getEntity(e1.getName());
			assertNotNull(e2);
//			System.out.println("Compare entities : ");
//			System.out.println(" " + e1.toString());
//			System.out.println(" " + e2.toString());
			compareEntities(e1, e2);
		}
	}
	private final void compareEntities(DomainEntity e1, DomainEntity e2) {
		assertEquals ( e1.getNumberOfFields(), e2.getNumberOfFields()) ;
		for ( DomainEntityField field1 : e1.getFields() ) {
			DomainEntityField field2 = e2.getField(field1.getName());
			assertNotNull(field2);
			System.out.println("Compare fields : ");
			System.out.println(" " + field1.toString());
			System.out.println(" " + field2.toString());
			assertEquals(field1.toString(), field2.toString());
		}
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParseModel_OneEntity() throws Exception {
		File modelFile = new File("src/test/resources/model_test/valid/OneEntity.model");
		Parser parser = new Parser();
		DomainModel model = parser.parse(modelFile);
		
		assertEquals("OneEntity", model.getName());
		assertEquals("1.0", model.getVersion() );

		
		DomainModel modelToCompare = new DomainModel("OneEntity");
		DomainEntity employee = new DomainEntity("Employee");
		DomainEntityField id = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		id.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		employee.addField(id);
		employee.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
		employee.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
		modelToCompare.addEntity(employee);
		
		//assertEquals(modelToCompare,model);
		compareModels(model, modelToCompare) ;

	}
	
	@Test
	public void testParseModel_TwoEntities() throws Exception {
		File modelFile = new File("src/test/resources/model_test/valid/TwoEntities.model");
		Parser parser = new Parser();
		DomainModel model = parser.parse(modelFile);
		
		DomainModel modelToCompare = new DomainModel("TwoEntities");
		
		assertEquals("ModelWithTwoEntities", model.getName());
		assertEquals("2.1", model.getVersion() );

//		DomainEntity country = new DomainEntity("Country");
//		DomainEntityField idCountry = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//		idCountry.addAnnotation(new DomainEntityFieldAnnotation("Id"));		
//		country.addField(idCountry);
//		country.addField(new DomainEntityField("name", DomainNeutralTypes.getType("string")));
		DomainEntity country = buildCountryEntity();
		
		modelToCompare.addEntity(country);
		
		DomainEntity employee = new DomainEntity("Employee");
		DomainEntityField id = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		id.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		employee.addField(id);
		employee.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
		employee.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
		DomainEntityField countryField = new DomainEntityField("country", country);
		countryField.addAnnotation(new DomainEntityFieldAnnotation("Max", new BigDecimal("3")));
		employee.addField(countryField);
		modelToCompare.addEntity(employee);
		
		//assertEquals(modelToCompare,model);
		compareModels(model, modelToCompare) ;
	}

    @Test
    public void testParseModel_TwoEntitiesWithEmbedded() throws Exception {
        File modelFile = new File("src/test/resources/model_test/valid/TwoEntitiesWithEmbedded.model");
        Parser parser = new Parser();
        DomainModel model = parser.parse(modelFile);
        
        DomainModel modelToCompare = new DomainModel("TwoEntitiesWithEmbedded");
        
//        DomainEntity country = new DomainEntity("Country");
//        DomainEntityField idCountry = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//        idCountry.addAnnotation(new DomainEntityFieldAnnotation("Id"));
//        country.addField(idCountry);
//        country.addField(new DomainEntityField("name", DomainNeutralTypes.getType("string")));
        DomainEntity country = buildCountryEntity();
        
        modelToCompare.addEntity(country);

        DomainEntity employee = new DomainEntity("Employee");
        DomainEntityField id = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
        id.addAnnotation(new DomainEntityFieldAnnotation("Id"));
        employee.addField(id);
        employee.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
        employee.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
        DomainEntityField countryField = new DomainEntityField("country", country);
        countryField.addAnnotation(new DomainEntityFieldAnnotation("Max", new BigDecimal("3")));
        countryField.addAnnotation(new DomainEntityFieldAnnotation("Embedded"));
        employee.addField(countryField);
        modelToCompare.addEntity(employee);
        
//        assertEquals(modelToCompare,model);
		compareModels(model, modelToCompare) ;
        assertEquals(DomainTypeNature.ENTITY, model.getEntity("Employee").getField("country").getType().getNature());
    }

//    @Test
//	public void testParseModelWithAnEnum() throws Exception {
//		File folder = new File("src/test/resources/model_test/valid/model_withAnEnum");
//		DomainModelParser parser = new DomainModelParser();
//		DomainModel model = parser.parse(folder);
//		DomainModel modelToCompare = new DomainModel("ModelWithAnEnum");
//		DomainEnumeration<String> enumeration = new DomainEnumerationForString("Pays");
//		enumeration.addItem(new DomainEnumerationItem("FR", "France"));
//		enumeration.addItem(new DomainEnumerationItem("EN", "Angleterre"));
//		enumeration.addItem(new DomainEnumerationItem("ES", "Espagne"));
//		modelToCompare.addEnumeration(enumeration);
//		assertEquals(modelToCompare,model);
//	}
	
	
//	@Test
//	public void testParseModelWithAnEnumAndAnEntity() throws Exception {
//		File folder = new File("src/test/resources/model_test/valid/model_withAnEnumAndAnEntity/");
//		DomainModelParser parser = new DomainModelParser();
//		DomainModel model = parser.parse(folder);
//		DomainModel modelToCompare = new DomainModel("ModelWithAnEnumAndAnEntity");
//		DomainEnumeration<String> enumeration = new DomainEnumerationForString("Pays");
//		enumeration.addItem(new DomainEnumerationItem("FR", "France"));
//		enumeration.addItem(new DomainEnumerationItem("EN", "Angleterre"));
//		enumeration.addItem(new DomainEnumerationItem("ES", "Espagne"));
//		
//		DomainEntity employee = new DomainEntity("Employee");
//		DomainEntityField id = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//		id.addAnnotation(new DomainEntityFieldAnnotation("Id"));
//		employee.addField(id);
//		employee.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
//		employee.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
//		DomainEntityField countryField = new DomainEntityField("country", enumeration );
//		employee.addField(countryField);
//		modelToCompare.addEntity(employee);
//		modelToCompare.addEnumeration(enumeration);
//		assertEquals(modelToCompare,model);
//	}
	
	@Test
	public void testParseModel_TwoEntitiesWithSpaces() throws Exception {
		File modelFile = new File("src/test/resources/model_test/valid/TwoEntitiesWithSpaces.model");
		Parser parser = new Parser();
		DomainModel model = parser.parse(modelFile);
		
		assertEquals("TwoEntitiesWithSpaces", model.getName());
		assertEquals("1.0", model.getVersion() );
		
		DomainModel modelToCompare = new DomainModel("TwoEntitiesWithSpaces");
		
//		DomainEnumeration<String> enumeration = new DomainEnumerationForString("Pays");
//		enumeration.addItem(new DomainEnumerationItem("FR", "Fra     nce"));
//		enumeration.addItem(new DomainEnumerationItem("EN", "Angleterre"));
//		enumeration.addItem(new DomainEnumerationItem("ES", "Espagne    "));
		DomainEntity country = buildCountryEntity();
		
		DomainEntity employee = new DomainEntity("Employee");
		DomainEntityField id = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		id.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		employee.addField(id);
		employee.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
		employee.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
		
//		DomainEntityField countryField = new DomainEntityField("country", enumeration );
		DomainEntityField countryField = new DomainEntityField("country", country );
		employee.addField(countryField);
		modelToCompare.addEntity(employee);
//		modelToCompare.addEnumeration(enumeration);
		modelToCompare.addEntity(country);
		
//		assertEquals(modelToCompare,model);
		compareModels(model, modelToCompare);
	}
//	@Test
//	public void testParseModelWithTwoEnumAndTwoEntity() throws Exception {
//		File folder = new File("src/test/resources/model_test/valid/model_withTwoEnumAndTwoEntity/");
//		DomainModelParser parser = new DomainModelParser();
//		DomainModel model = parser.parse(folder);
//		DomainModel modelToCompare = new DomainModel("ModelWithTwoEnumAndTwoEntity");
//		
//		DomainEnumeration<String> country = new DomainEnumerationForString("Country");
//		country.addItem(new DomainEnumerationItem("FR", "France"));
//		country.addItem(new DomainEnumerationItem("EN", "Angleterre"));
//		country.addItem(new DomainEnumerationItem("ES", "Espagne"));
//
//		DomainEnumeration<String> gender = new DomainEnumerationForString("Gender");
//		gender.addItem(new DomainEnumerationItem("H", "Homme"));
//		gender.addItem(new DomainEnumerationItem("F", "Femme"));
//		
//		DomainEntity employee = new DomainEntity("Employee");
//		DomainEntityField employeeId = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//		employeeId.addAnnotation(new DomainEntityFieldAnnotation("Id"));
//		employeeId.addAnnotation(new DomainEntityFieldAnnotation("NotNull"));
//		employee.addField(employeeId);
//		DomainEntityField employeePoste = new DomainEntityField("poste", DomainNeutralTypes.getType("string"), -1);
//		employee.addField(employeePoste);
//		employeePoste.addAnnotation(new DomainEntityFieldAnnotation("NotNull"));
//		
//		DomainEntity person = new DomainEntity("Person");
//		DomainEntityField personId = new DomainEntityField("id", DomainNeutralTypes.getType("integer"));
//		personId.addAnnotation(new DomainEntityFieldAnnotation("Id"));
//		person.addField(personId);
//		person.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
//		person.addField(new DomainEntityField("lastName", DomainNeutralTypes.getType("string")));
//		person.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
//		DomainEntityField countryField = new DomainEntityField("country", country, 999 );
//		person.addField(countryField);
//		DomainEntityField genderField = new DomainEntityField("gender", gender );
//		person.addField(genderField);
//		
//		modelToCompare.addEntity(employee);
//		modelToCompare.addEntity(person);
//		modelToCompare.addEnumeration(country);
//		modelToCompare.addEnumeration(gender);
//		assertEquals(modelToCompare,model);
//	}

	@Test
	public void testParseModel_FourEntitiesDirty() throws Exception {
		File modelFile = new File("src/test/resources/model_test/valid/FourEntitiesDirty.model");
		Parser parser = new Parser();
		DomainModel model = parser.parse(modelFile);
		
		assertEquals("FourEntitiesDirty", model.getName());
		assertEquals("", model.getVersion() );

		DomainModel modelToCompare = new DomainModel("FourEntitiesDirty");
		
//		DomainEnumeration<String> country = new DomainEnumerationForString("Country");
//		country.addItem(new DomainEnumerationItem("FR", "France"));
//		country.addItem(new DomainEnumerationItem("EN", "Angleterre"));
//		country.addItem(new DomainEnumerationItem("ES", "Espagne"));
//
//		DomainEnumeration<String> gender = new DomainEnumerationForString("Gender");
//		gender.addItem(new DomainEnumerationItem("H", "Homme"));
//		gender.addItem(new DomainEnumerationItem("F", "Femme"));
//		
		DomainEntity country = buildCountryEntity();
		DomainEntity gender = buildGenderEntity();

		DomainEntity employee = new DomainEntity("Employee");
		DomainEntityField employeeId = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		employeeId.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		employeeId.addAnnotation(new DomainEntityFieldAnnotation("NotNull"));
		employee.addField(employeeId);
		DomainEntityField employeePoste = new DomainEntityField("poste", DomainNeutralTypes.getType("string"), -1);
		employee.addField(employeePoste);
		employeePoste.addAnnotation(new DomainEntityFieldAnnotation("NotNull"));
		
		DomainEntity person = new DomainEntity("Person");
		DomainEntityField personId = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		personId.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		person.addField(personId);
		person.addField(new DomainEntityField("firstName", DomainNeutralTypes.getType("string")));
		person.addField(new DomainEntityField("lastName", DomainNeutralTypes.getType("string")));
		person.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
		DomainEntityField countryField = new DomainEntityField("country", country, 998 );
		person.addField(countryField);
		DomainEntityField genderField = new DomainEntityField("gender", gender );
		person.addField(genderField);
		
		modelToCompare.addEntity(employee);
		modelToCompare.addEntity(person);
//		modelToCompare.addEnumeration(country);
		modelToCompare.addEntity(country);
//		modelToCompare.addEnumeration(gender);
		modelToCompare.addEntity(gender);
		
//		assertEquals(modelToCompare,model);
		compareModels(model, modelToCompare);
	}
	
	
	@Test
    public void testParseModel_FourEntities() throws Exception {
        File modelFile = new File("src/test/resources/model_test/valid/FourEntities.model");
        Parser parser = new Parser();
        DomainModel model = parser.parse(modelFile);
        
		assertEquals("FourEntities", model.getName());
		assertEquals("", model.getVersion() );
		assertEquals(0, parser.getErrors().size() );
		
        DomainModel modelToCompare = new DomainModel("FourEntities");
        
//        DomainEnumeration<String> country = new DomainEnumerationForString("Country");
//        country.addItem(new DomainEnumerationItem("FR", "  France"));
//        country.addItem(new DomainEnumerationItem("EN", "Angleterre  "));
//        country.addItem(new DomainEnumerationItem("ES", "Esp  agne"));
//
//        DomainEnumeration<String> gender = new DomainEnumerationForString("Gender");
//        gender.addItem(new DomainEnumerationItem("H", " Homme"));
//        gender.addItem(new DomainEnumerationItem("F", "Fem me "));

		DomainEntity country = buildCountryEntity();
		DomainEntity gender = buildGenderEntity();
        
        DomainEntity employee = new DomainEntity("Employee");
        DomainEntityField employeeId = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
        employeeId.addAnnotation(new DomainEntityFieldAnnotation("Id"));
        employeeId.addAnnotation(new DomainEntityFieldAnnotation("NotNull"));
        employee.addField(employeeId);
        DomainEntityField employeePoste = new DomainEntityField("poste", DomainNeutralTypes.getType("string"), 4);
        employee.addField(employeePoste);
        employeePoste.addAnnotation(new DomainEntityFieldAnnotation("NotNull"));
        
        DomainEntity person = new DomainEntity("Person");
        DomainEntityField personId = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
        personId.addAnnotation(new DomainEntityFieldAnnotation("Id"));
        person.addField(personId);
        DomainEntityField firstName =new DomainEntityField("firstName", DomainNeutralTypes.getType("string"));
        firstName.addAnnotation(new DomainEntityFieldAnnotation("SizeMax", new BigDecimal("3") ));
        person.addField(firstName);
        person.addField(new DomainEntityField("lastName", DomainNeutralTypes.getType("string")));
        person.addField(new DomainEntityField("birthDate", DomainNeutralTypes.getType("date")));
        DomainEntityField countryField = new DomainEntityField("country", country, 999 );
        person.addField(countryField);
        DomainEntityField genderField = new DomainEntityField("gender", gender );
        person.addField(genderField);
        
        modelToCompare.addEntity(employee);
        modelToCompare.addEntity(person);
//        modelToCompare.addEnumeration(country);
//        modelToCompare.addEnumeration(gender);
        modelToCompare.addEntity(country);
        modelToCompare.addEntity(gender);

//        assertEquals(modelToCompare,model);
		compareModels(model, modelToCompare) ;
    }
	
	@Test
    public void testParseModel_FourEntities_Invalid() throws Exception {
        File modelFile = new File("src/test/resources/model_test/invalid/FourEntities.model");
        Parser parser = new Parser();
        DomainModel model = null ;
        Exception exception = null ;
		try {
			model = parser.parse(modelFile);
		} catch (DslParserException e) {
			exception = e ;
			e.printStackTrace();
		}
        
		assertEquals(null, model);
		assertNotNull(exception );
		assertEquals(2, parser.getErrors().size() );
		System.out.println("Entities with errors : ");
		System.out.println(" Country : " + parser.getErrors().get("Country.entity") );
		System.out.println(" Gender  : " + parser.getErrors().get("Gender.entity") );
		assertNotNull(parser.getErrors().get("Country.entity"));
		assertNotNull(parser.getErrors().get("Gender.entity"));
    }
	
	private DomainEntity buildCountryEntity() {
		DomainEntity country = new DomainEntity("Country");
		
		// id : integer {@Id}
		DomainEntityField idCountry = new DomainEntityField("id", DomainNeutralTypes.getType(DomainNeutralTypes.INTEGER));
		idCountry.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		country.addField(idCountry);
		
		// name : string
		country.addField(new DomainEntityField("name", DomainNeutralTypes.getType("string")));
		
		return country ;
	}

	private DomainEntity buildGenderEntity() {
		DomainEntity country = new DomainEntity("Gender");
		
		// id : string {@Id}
		DomainEntityField idCountry = new DomainEntityField("id", DomainNeutralTypes.getType("string"));
		idCountry.addAnnotation(new DomainEntityFieldAnnotation("Id"));
		country.addField(idCountry);
		
		// name : string
		country.addField(new DomainEntityField("name", DomainNeutralTypes.getType("string")));
		
		return country ;
	}
	
	@Test
    public void testParseModel_AttributesOrder() throws Exception {
        File modelFile = new File("src/test/resources/model_test/valid/FourEntities.model");
        Parser parser = new Parser();
        DomainModel model = parser.parse(modelFile);
        
		assertEquals("FourEntities", model.getName());
		assertEquals("", model.getVersion() );
		assertEquals(0, parser.getErrors().size() );
		
		DomainEntity entity = model.getEntity("Person");
		assertNotNull(entity);
		System.out.println("Entity '" + entity.getName()+"' ready.");
		List<DomainEntityField> fields = entity.getFields();
		System.out.println(fields.size() + " field(s) : ");
		for ( DomainEntityField field : fields ) {
			System.out.println(" . " + field.getName() + " ( type = " + field.getTypeName() + " )");
		}
		assertEquals(6, fields.size());
		int i = 0 ;
		assertEquals("id",        fields.get(i++).getName() );
		assertEquals("firstName", fields.get(i++).getName() );
		assertEquals("lastName",  fields.get(i++).getName() );
		assertEquals("birthDate", fields.get(i++).getName() );
		assertEquals("country",   fields.get(i++).getName() );
		assertEquals("gender",    fields.get(i++).getName() );
		
	}
}
