package org.telosys.tools.dsl.parser.annotation;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class AnnotationsTest {
	
	@Test
	public void test1() {

		assertEquals(58, AnnotationDefinitions.getAll().size());

		assertNull(AnnotationDefinitions.get("Abcdef"));
		
		AnnotationDefinition ad ;
		
		ad = AnnotationDefinitions.get("Id");
		assertNotNull(ad);
		assertEquals("Id", ad.getName());
		assertEquals(AnnotationParamType.NONE, ad.getParamType());
		
		ad = AnnotationDefinitions.get("Size");
		assertNotNull(ad);
		assertEquals("Size", ad.getName());
		assertEquals(AnnotationParamType.SIZE, ad.getParamType());
		
		ad = AnnotationDefinitions.get("SizeMin");
		assertNotNull(ad);
		assertEquals("SizeMin", ad.getName());
		assertEquals(AnnotationParamType.INTEGER, ad.getParamType());
		
		ad = AnnotationDefinitions.get("DbName");
		assertNotNull(ad);
		assertEquals("DbName", ad.getName());
		assertEquals(AnnotationParamType.STRING, ad.getParamType());
		
		ad = AnnotationDefinitions.get("Max");
		assertNotNull(ad);
		assertEquals("Max", ad.getName());
		assertEquals(AnnotationParamType.DECIMAL, ad.getParamType());
		
		ad = AnnotationDefinitions.get("Insertable");
		assertNotNull(ad);
		assertEquals("Insertable", ad.getName());
		assertEquals(AnnotationParamType.BOOLEAN, ad.getParamType());
		
		ad = AnnotationDefinitions.get("Updatable");
		assertNotNull(ad);
		assertEquals("Updatable", ad.getName());
		assertEquals(AnnotationParamType.BOOLEAN, ad.getParamType());
		
	}

	@Test
	public void testGetAnnotations1() {
		List<String> list = AnnotationDefinitions.getAllAnnotationsWithPrefix();
		print(list);
		assertTrue(list.contains("@DbComment"));
		assertTrue(list.contains("@Label"));
	}

	@Test
	public void testGetAnnotations2() {
		List<String> list = AnnotationDefinitions.getAllAnnotationsWithPrefixAndParentheses();
		print(list);
		assertTrue(list.contains("@DbComment()"));
		assertTrue(list.contains("@Label()"));
	}
	private void print(List<String> list) {		
//		System.out.println("Annotations :");
//		for(String s : list) {
//			System.out.println(" . " + s);
//		}
	}
}
