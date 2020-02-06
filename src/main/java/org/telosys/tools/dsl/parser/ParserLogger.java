package org.telosys.tools.dsl.parser;

public class ParserLogger {

	private static final boolean LOG = false;

	private ParserLogger() {
	}

	public static final void log(String msg) {
		if (LOG) {
			System.out.println(msg);
		}
	}

	public static final void print(String msg) {
		if (LOG) {
			System.out.print(msg);
		}
	}

}
