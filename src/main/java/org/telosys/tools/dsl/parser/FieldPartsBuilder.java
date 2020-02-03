package org.telosys.tools.dsl.parser;

/**
 * A work object used to build an instance of 'FieldParts'
 * 
 * @author Laurent GUERIN
 *
 */
class FieldPartsBuilder {

	private boolean inAnnotations;
	private boolean finished;
	private final StringBuilder nameAndTypeBuilder ;
	private final StringBuilder annotationsBuilder ;
	private final int lineNumber; 
	
	private String nameAndTypePart = "" ;
	private String annotationsPart = "" ;
	
//	private void log(String msg) {
//		System.out.println("\n=== Field log : " + msg + " " + this.toString() + "\n");
//		System.out.flush();
//	}

	protected FieldPartsBuilder(int lineNumber) {
		super();
		ParserLogger.log("\n=== Field log : new Field()" );
		this.nameAndTypeBuilder = new StringBuilder();
		this.annotationsBuilder = new StringBuilder();
		this.inAnnotations = false ;
		this.finished = false ;
		this.lineNumber = lineNumber;
	}

	protected FieldPartsBuilder(int lineNumber, String nameAndTypePart, String annotationsPart) {
		super();
		this.nameAndTypeBuilder = new StringBuilder();
		this.annotationsBuilder = new StringBuilder();
		this.finished = true ;
		this.lineNumber = lineNumber;
		this.nameAndTypePart = nameAndTypePart;
		this.annotationsPart = annotationsPart;
	}
	
	protected void append(char c) {
		if ( ! finished ) {
			if ( inAnnotations ) {
				annotationsBuilder.append(c);				
			}
			else {
				nameAndTypeBuilder.append(c);
			}
		}
	}
	
	protected boolean isVoid() {
		return nameAndTypeBuilder.length() == 0 && annotationsBuilder.length() == 0 ;
	}
	
	protected void finished() {
		finished = true ;
		nameAndTypePart = nameAndTypeBuilder.toString();
		annotationsPart = annotationsBuilder.toString();
		ParserLogger.log("\n=== Field log : finished() " + this.toString() + "\n");
	}
	
	protected void setInAnnotations(boolean flag) {
		this.inAnnotations = flag;
	}
	
//	public int getLineNumber() {
//		return lineNumber;
//	}
//	public String getNameAndTypePart() {
//		return nameAndTypePart;
//	}
//	public String getAnnotationsPart() {
//		return annotationsPart;
//	}

	public FieldParts getFieldParts() {
		return new FieldParts(lineNumber, nameAndTypePart, annotationsPart);
	}
	
	@Override
	public String toString() {
		return "Field : line " + lineNumber + " (finished=" + finished + ")"
				+ " [" + nameAndTypeBuilder.toString() + "] [" + annotationsBuilder.toString() + "]"
				+ " -> [" + nameAndTypePart + "] [" + annotationsPart + "]"
				;
	}

}
