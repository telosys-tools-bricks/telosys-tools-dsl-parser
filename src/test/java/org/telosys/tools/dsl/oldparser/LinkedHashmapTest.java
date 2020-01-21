package org.telosys.tools.dsl.oldparser;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class LinkedHashmapTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test1() throws Exception {
		HashMap<String, String> map = new LinkedHashMap<String, String>() ;
		
		for ( int i = 0 ; i < 10 ; i++ ) {
			map.put(""+i, "Value" + i);
		}
		
		System.out.println("Map : " + map );
		Set<String> keys = map.keySet();
		System.out.println("Keys : " + keys );
	}
	
	@Test
	public void test2() throws Exception {
		HashMap<String, String> map = new LinkedHashMap<String, String>() ;
		
		for ( int i = 20 ; i > 10 ; i-- ) {
			map.put(""+i, "Value" + i);
		}
		System.out.println("Map : " + map );

		Set<String> keys = map.keySet();
		System.out.println("Keys : " + keys );
		
		Collection<String> values = map.values() ;
		System.out.println("Values : " + values );
		
	}
	
}
