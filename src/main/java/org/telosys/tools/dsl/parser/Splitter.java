package org.telosys.tools.dsl.parser;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.dsl.DslParserException;

public class Splitter {

	List<String> elements = new LinkedList<>();
	
	private StringBuilder currentElement ;
	boolean inElement = false ;
	boolean inSingleQuote = false ;
	boolean inDoubleQuote = false ;
	boolean inParentheses = false ;
	
	public Splitter() {
		super();
	}
	
	private void log(String message) {
		System.out.println("LOG:" + message);
	}
	private void logChar(char c) {
		System.out.print( "[" + c + "]");
	}
	private void log(char c) {
		System.out.print(c);
	}
	
	private boolean inParentheses() {
		return inElement && inParentheses ;
	}
	private boolean inQuote() {
		return inElement && inParentheses && ( inSingleQuote || inDoubleQuote ) ;
	}
	
	public List<String> split(String s) {
		
		for (char c : s.toCharArray() ) {
			logChar(c);
			if ( c == '@' ) {
				if ( inParentheses() ) {
					if ( inQuote() ) {
						keepChar(c);
					}
					else {
						throw new DslParserException("Unexpected '" + c + "' in parentheses");
					}
				}
				else {
					newCurrentElement();
					keepChar(c);
				}
			}
			else if ( c == '(' ) {
				if ( ! inQuote() ) {
					if ( ! inParentheses() ) {
						inParentheses = true ;
					}
					else {
						throw new DslParserException("Unexpected opening parenthesis");
					}
				}
				keepChar(c);
			}
			else if ( c == ')' ) {
				if ( ! inQuote() ) {
					if ( inParentheses() ) {
						inParentheses = false ;
						inElement = false; // it's the end of current element
					}
					else {
						throw new DslParserException("Unexpected closing parenthesis");
					}
				}
				keepChar(c);
			}
			else if ( c == '\'' ) {
				if ( inParentheses() ) {
					if ( ! inDoubleQuote ) {
						inSingleQuote = ! inSingleQuote ; // toggle quote flag
					}
					keepChar(c);
				}
				else {
					throw new DslParserException("Unexpected single quote");
				}
				
			}
			else if ( c == '\"' ) {
				if ( inParentheses() ) {
					if ( ! inSingleQuote ) {
						inDoubleQuote = ! inDoubleQuote ; // toggle quote flag
					}
					keepChar(c);
				}
				else {
					throw new DslParserException("Unexpected double quote");
				}
			}
			else if ( c == ',' ) {
				if ( inQuote() ) {
					keepChar(c);
				}
				// else ignore this char
			}
			else {
				keepChar(c);
//				if ( inElement ) {
//					keepChar(c);
//				}
//				else {
//					// 
//				}
			}
		}
		// end 
		keepCurrentElement();
		return elements;
	}
	
	private void newCurrentElement() {
		keepCurrentElement();
		currentElement = new StringBuilder();
		// reset all flags
		inElement = true ;
		inSingleQuote = false ;
		inDoubleQuote = false ;
		inParentheses = false ;
	}
	
	private void keepCurrentElement() {
		if ( currentElement != null ) {
			if ( inQuote() ) {
				throw new DslParserException("Unexpected end of element (quote not closed)");
			}
			if ( inParentheses() ) {
				throw new DslParserException("Unexpected end of element (parenthesis not closed)");
			}
			String element = currentElement.toString().trim();
			elements.add(element);
		}
	}
	
	private void keepChar(char c) {
		log( '+' );
		currentElement.append(c);
	}

}
