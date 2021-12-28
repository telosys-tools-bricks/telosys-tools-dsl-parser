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
package org.telosys.tools.dsl.parser2;

/**
 * 
 * @author Laurent GUERIN
 *
 */
class State {
	
	private final int lineNumber;
	
	private char    previousChar = 0;
	private boolean inAnnotation = false ;
	private boolean inTag = false ;
	private boolean inAnnotationOrTagParam = false ;
	private boolean inDoubleQuote = false ;
	private boolean inSingleQuote = false ;
	
	public State(int lineNumber) {
		super();
		this.lineNumber = lineNumber ;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void reset() {
		inAnnotation = false ;
		inTag = false ;
		inAnnotationOrTagParam = false ;
		inDoubleQuote = false ;
		inSingleQuote = false ;
		// NB : do not reset 'previousChar'
	}
	
	public boolean inAnnotation() {
		return inAnnotation;
	}
	public void setInAnnotation() {
		this.inAnnotation = true;
	}
	
	public boolean inTag() {
		return inTag;
	}
	public void setInTag() {
		this.inTag = true;
	}
	
	public boolean inAnnotationOrTag() {
		return inAnnotation || inTag ;
	}
	public void setInAnnotationOrTagParam() {
		inAnnotationOrTagParam = true;
	}
	public boolean inAnnotationOrTagParam() {
		return inAnnotationOrTagParam ;
	}
	
	public char previousChar() {
		return previousChar;
	}
	public void setPreviousChar(char c) {
		this.previousChar = c;
	}
	
	public void toggleDoubleQuote() {
		inDoubleQuote = ! inDoubleQuote ;
	}
	public boolean inDoubleQuote() {
		return inDoubleQuote ;
	}
	
	public void toggleSingleQuote() {
		inSingleQuote = ! inSingleQuote ;
	}
	public boolean inSingleQuote() {
		return inSingleQuote ;
	}
	
	public boolean inQuote() {
		return inDoubleQuote || inSingleQuote ;
	}

	@Override
	public String toString() {
		return "State : " 
				+ "previousChar = '" + previousChar + "'"
				+ ", inAnnotation = " + inAnnotation 
				+ ", inTag = " + inTag 
				+ ", inAnnotationOrTagParam = " + inAnnotationOrTagParam 
				+ ", inSingleQuote = " + inSingleQuote 
				+ ", inDoubleQuote = " + inDoubleQuote
				;
	}

}
