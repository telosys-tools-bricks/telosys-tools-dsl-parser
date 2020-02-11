package org.telosys.tools.dsl.parsing.entitylevel;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.telosys.tools.dsl.parser.Parser;
import org.telosys.tools.dsl.parser.exceptions.EntityParsingError;
import org.telosys.tools.dsl.parser.model.DomainEntity;

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

		assertEquals("Employé", entity.getName()); // Accentuated entity name
		assertEquals(3, entity.getNumberOfFields());
		String fieldName = entity.getFields().get(1).getName(); // Accentuated field name
		
		// see : https://stackoverflow.com/questions/17354891/java-bytebuffer-to-string
		
		// To get the bytes from a String in a particular encoding, you can use a sibling getBytes() method:
		byte[] bytes = "prénom".getBytes( StandardCharsets.UTF_8 );
		System.out.println("Expected  : 'prénom' getBytes( StandardCharsets.UTF_8 ) length : " + bytes.length );
//		String expectedFieldName = new String(bytes, StandardCharsets.UTF_8 );
		String expectedFieldName = new String(bytes);
			
//		System.out.println("Expected  : Bytes length 'prénom' : " + "prénom".getBytes().length );
//		System.out.println("Real name : Bytes length '" + fieldName + "' : " + fieldName.getBytes().length );
//		System.out.println("expectedFieldName : Bytes length '" + expectedFieldName + "' : " + expectedFieldName.getBytes().length );

		System.out.println("Expected : '" + expectedFieldName + "' vs real '" + fieldName + "'" );
		assertEquals(expectedFieldName, fieldName); 
	}

	public void testCharsets() throws UnsupportedEncodingException, UnexpectedException {

		Map<String, Charset> map = Charset.availableCharsets();
		for (String key : map.keySet()) {
			System.out.println(" . " + key + " : " + map.get(key));
		}
		System.out.println(map.size() + " available Charsets");
		System.out.println("Default charset : " + Charset.defaultCharset());
		String s1 = "abcéèç";
		/*
		byte[] bytes = s1.getBytes(Charset.defaultCharset());
		String s2 = new String(bytes, "utf-8");
		if ( ! s1.equals(s2) ) {
			// False when test run by Maven : default charset = "windows-1252"
			throw new UnexpectedException("Strings are diferent, default charset = " + Charset.defaultCharset());
		}
		*/

		String s1UTF8 = toUTF8(s1);
		assertEquals(s1, s1UTF8);

	}

	// cf Detect charset : https://www.turro.org/publications/?item=114&page=0 
	
	private String toUTF8(String name) {
		try {
			byte[] bytes = name.getBytes(Charset.defaultCharset());
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return name; // no change !
		}
	}

}
