package org.telosys.tools.split;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SplitTest {
    
    @Test
    public void test0()  {
    	String[] parts = "aaa".split("//");
        assertEquals(1, parts.length ) ;
		assertEquals("aaa", parts[0] ); 
    }

    @Test
    public void test1()  {
    	String[] parts = "aaa // bbb".split("//");
        assertEquals(2, parts.length ) ;
		assertEquals("aaa ", parts[0] ); 
		assertEquals(" bbb", parts[1] ); 
    }

    @Test
    public void test2()  {
    	String[] parts = "// bbb".split("//");
        assertEquals(2, parts.length ) ;
		assertEquals("", parts[0] ); 
		assertEquals(" bbb", parts[1] ); 
    }

    @Test
    public void test3()  {
    	String[] parts = "//".split("//");
        assertEquals(0, parts.length ) ;  // 0 parts !
    }

    @Test
    public void test4()  {
    	String[] parts = "aaa // bbb // ccc / ddd / eee ".split("//");
        assertEquals(3, parts.length ) ;
		assertEquals("aaa ", parts[0] ); 
		assertEquals(" bbb ", parts[1] ); 
		assertEquals(" ccc / ddd / eee ", parts[2] ); 
    }


}
