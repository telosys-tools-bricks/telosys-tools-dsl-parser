package org.telosys.tools.dsl.parser;

/**
 * This object contains a 'field' after the first step of parsing. <br>
 * It contains the 2 parts of the field : <br>
 * . name and type<br>
 * . annotations ans tags <br>
 * These two parts are raw (not yet parsed) <br>
 * 
 * @author Laurent GUERIN
 *
 */
class FieldParts {

	private final int lineNumber;
	private final String nameAndTypePart ;
	private final String annotationsPart ;
	
	protected FieldParts(int lineNumber, String nameAndTypePart, String annotationsPart) {
		super();
		this.lineNumber = lineNumber ;
		this.nameAndTypePart = nameAndTypePart;
		this.annotationsPart = annotationsPart;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getNameAndTypePart() {
		return nameAndTypePart;
	}

	public String getAnnotationsPart() {
		return annotationsPart;
	}

	protected boolean isVoid() {
		return nameAndTypePart.length() == 0 && annotationsPart.length() == 0 ;
	}
	
	@Override
	public String toString() {
		return "Field : line " + lineNumber 
				+ " [" + nameAndTypePart + "] [" + annotationsPart + "]"
				;
	}

}
