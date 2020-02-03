package org.telosys.tools.dsl.parser;

public class ParserLogger {

	private ParserLogger() {
	}

	public static final void log(String msg) {
    	System.out.println(msg);		
	}
	
	public static final void print(String msg) {
    	System.out.print(msg);		
	}
	
}
