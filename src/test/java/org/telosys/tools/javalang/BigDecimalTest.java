package org.telosys.tools.javalang;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BigDecimalTest {
    
    @Test
    public void test1()  {
    	BigDecimal x = new BigDecimal("123");
    	assertEquals("123", x.toString() );
    	assertEquals( 3, x.precision());
    	assertEquals( 0, x.scale());
    	assertEquals( 123, x.intValueExact()); 

    	// Left part 
    	assertEquals( 123, x.intValue());
    	// Right part 
    	assertEquals( BigInteger.valueOf(0), x.remainder(BigDecimal.ONE).movePointRight(x.scale()).abs().toBigInteger());
    }

    @Test
    public void test2()  {
    	BigDecimal x = new BigDecimal("123.89");
    	assertEquals("123.89", x.toString() );
    	assertEquals( 5, x.precision());
    	assertEquals( 2, x.scale());
    	
    	// Left part 
    	assertEquals( 123, x.intValue());

    	//assertEquals( 123, x.intValueExact()); // Error "Rounding necessary"
    	//System.out.println( x.remainder(BigDecimal.ONE) ); // 0.89
    	// Right part 
    	assertEquals( BigInteger.valueOf(89), x.remainder(BigDecimal.ONE).movePointRight(x.scale()).abs().toBigInteger());
    }

    @Test
    public void test3()  {
    	BigDecimal x = new BigDecimal("0");
    	assertEquals("0", x.toString() );
    	// Left part 
    	assertEquals( 0, x.intValue());
    	// Right part 
    	assertEquals( BigInteger.valueOf(0), x.remainder(BigDecimal.ONE).movePointRight(x.scale()).abs().toBigInteger());
    }

    @Test
    public void test4()  {
    	BigDecimal x = new BigDecimal("0.99");
    	assertEquals("0.99", x.toString() );
    	// Left part 
    	assertEquals( 0, x.intValue());
    	// Right part 
    	assertEquals( BigInteger.valueOf(99), x.remainder(BigDecimal.ONE).movePointRight(x.scale()).abs().toBigInteger());
    }
}
