package org.telosys.tools.dsl.parser;

class Field {

//	private int position ;
	private boolean inAnnotations;
	private boolean finished;
	private final StringBuilder nameAndTypeBuilder ;
	private final StringBuilder annotationsBuilder ;
	private final int lineNumber; 
	
	private String nameAndTypePart = "" ;
	private String annotationsPart = "" ;
	
	private void log(String msg) {
		System.out.println("\n=== Field log : " + msg + " " + this.toString() + "\n");
		System.out.flush();
	}

	protected Field(int lineNumber) {
		super();
		System.out.println("\n=== Field log : new Field()" );
		this.nameAndTypeBuilder = new StringBuilder();
		this.annotationsBuilder = new StringBuilder();
//		this.position = Const.IN_FIELDS; 
		this.inAnnotations = false ;
		this.finished = false ;
		this.lineNumber = lineNumber;
	}

	protected Field(int lineNumber, String nameAndTypePart, String annotationsPart) {
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
//			if ( position == Const.IN_FIELDS ) {
//				nameAndTypeBuilder.append(c);
//			}
//			else if ( position == Const.IN_ANNOTATIONS ) {
//				annotationsBuilder.append(c);
//			}
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
		log("finished()");
	}
	
	protected void setInAnnotations(boolean flag) {
		this.inAnnotations = flag;
	}
//	protected void setPosition(int position) {
//		this.position = position ;
//		log("setPosition(" + position + ")");
//	}
//	public int getPosition() {
//		return position;
//	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getNameAndTypePart() {
		return nameAndTypePart;
	}
//	protected void setFieldPart(String fieldPart) {
//		this.nameAndTypeBuilder = fieldPart;
//	}

	public String getAnnotationsPart() {
		return annotationsPart;
	}
//	protected void setAnnotationsPart(String annotationsPart) {
//		this.annotationsPart = annotationsPart;
//	}

	@Override
	public String toString() {
		return "Field : line " + lineNumber + " (finished=" + finished + ")"
				+ " [" + nameAndTypeBuilder.toString() + "] [" + annotationsBuilder.toString() + "]"
				+ " -> [" + nameAndTypePart + "] [" + annotationsPart + "]"
				;
	}

}
