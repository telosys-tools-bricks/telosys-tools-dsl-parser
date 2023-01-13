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
package org.telosys.tools.dsl.model.writer;

/**
 * Exception thrown when an error occurs while writing a model file
 * 
 * @author Laurent Guerin
 *
 */
public class ModelWriterException extends RuntimeException { 

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor with message
	 * @param msg
	 */
	public ModelWriterException(String msg) {
		super(msg);
	}

	/**
	 * Constructor with message and cause
	 * @param msg
	 * @param exception
	 */
	public ModelWriterException(String msg, Exception exception) {
		super(msg, exception);
	}

}
