package org.telosys.tools.dsl.parser;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.reporting.EntityReport;

import static org.junit.Assert.assertEquals;

public class AccentuatedEntityParsingTest {

	@Test
	public void testParser() throws EntityParsingError {

		List<String> entitiesNames = new LinkedList<>();
		entitiesNames.add("Car");
		Parser parser = new Parser();
		DomainEntity entity = parser.parseEntity(new File("src/test/resources/entity_test_v_3_2/Employé.entity"),
				entitiesNames);

		EntityReport.print(entity);

		System.out.println("default charset : " + Charset.defaultCharset());

		//-- Entity name with accentuated character
		assertEquals("Employé", entity.getName()); // Accentuated entity name
		assertEquals("Employ\u00E9", entity.getName()); 

		// ERROR with MAVEN TEST :
		// assertEquals(toUTF8("Employé"), entity.getName()); // Accentuated
		// entity name
		// expected:<Employ[?]> but was:<Employ[é]>
		// expected:<Employ[?]> but was:<Employ[�]>

		assertEquals(4, entity.getNumberOfFields());

		//-- Field name with accentuated character
		String fieldName = entity.getFields().get(1).getName(); // 2nd field
		assertEquals("prénom", fieldName);
		assertEquals("pr\u00E9nom", fieldName);

		assertEquals("àéè", entity.getFields().get(3).getName());
	}

}
