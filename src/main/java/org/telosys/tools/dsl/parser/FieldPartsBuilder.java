/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
	
	protected FieldPartsBuilder(int lineNumber) {
		super();
		ParserLogger.log("\n=== Field log : new Field()" );
		this.nameAndTypeBuilder = new StringBuilder();
		this.annotationsBuilder = new StringBuilder();
		this.inAnnotations = false ;
		this.finished = false ;
		this.lineNumber = lineNumber;
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
