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
package org.telosys.tools.dsl.parser.exceptions;

/**
 * Parsing Error main ancestor
 * 
 * @author Laurent GUERIN
 *
 */
public abstract class ParsingError extends Exception {

	private static final long serialVersionUID = 1L;
	
    // Entity name (if known)
	private final String entityName;

	// Entity line number (0 if unknown)
	private final int lineNumber;

    // Field name (if known)
	private final String fieldName;

    // Standard exception message
	private final String errorMessage ;

	/**
	 * MODEL level error 
	 * @param errorMessage
	 */
	protected ParsingError(String errorMessage ) {
        this(null, 0, null, errorMessage);
    }

	/**
	 * ENTITY level error without line number
	 * @param entityName
	 * @param errorMessage
	 */
	protected ParsingError(String entityName, String errorMessage ) {
        this(entityName, 0, null, errorMessage);
    }

	/**
	 * ENTITY level error with line number
	 * @param entityName
	 * @param lineNumber
	 * @param errorMessage
	 */
	protected ParsingError(String entityName, int lineNumber, String errorMessage ) {
        this(entityName, lineNumber, null, errorMessage);
    }

	/**
	 * FIELD level error without line number
	 * @param entityName
	 * @param fieldName
	 * @param errorMessage
	 */
	protected ParsingError(String entityName, String fieldName, String errorMessage ) {
        this(entityName, 0, fieldName, errorMessage);
    }

	/**
	 * FIELD level error with line number
	 * @param entityName
	 * @param lineNumber
	 * @param fieldName
	 * @param errorMessage
	 */
	protected ParsingError(String entityName, int lineNumber, String fieldName, String errorMessage ) {
        super();
        this.entityName = entityName ;
        this.lineNumber = lineNumber ;
        this.fieldName = fieldName ;
        this.errorMessage = errorMessage ;
    }

	//-------------------------------------------------------------------------------------------------------
	
	@Override
	public String getMessage() {
		return getReportMessage();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getEntityName() {
		return entityName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getReportMessage() {
		StringBuilder sb = new StringBuilder();
		if ( entityName != null || fieldName != null  ) {
			sb.append("[");
			if ( entityName != null ) {
				sb.append(entityName);
			}
			if ( entityName != null && fieldName != null  ) {
				sb.append(".");
			}
			if ( fieldName != null ) {
				sb.append(fieldName);
			}
			if ( lineNumber != 0 ) {
				sb.append(" (line ");
				sb.append(lineNumber);
				sb.append(")");
			}
			sb.append("] : ");
		}
		if ( errorMessage != null ) {
			sb.append(errorMessage);
		}
		else {
			sb.append("(no error message)");
		}
		return sb.toString();
	}
}
