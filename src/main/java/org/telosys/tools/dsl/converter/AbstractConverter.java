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
package org.telosys.tools.dsl.converter;

import org.telosys.tools.commons.logger.ConsoleLogger;

public abstract class AbstractConverter {
	
	private static final ConsoleLogger logger = new ConsoleLogger();
	
	protected void log(String msg) {
		if (ConverterLogStatus.LOG) {
			logger.log(this, msg);
		}
	}

	protected void check(boolean expr, String errorMessage) {
		if (!expr) {
			throw new IllegalStateException(errorMessage);
		}
	}
	
	/**
	 * Throws an exception if the given value is null
	 * @param value
	 * @return
	 */
	protected String notNull(String value) {
		if (value == null) {
			throw new IllegalStateException("Unexpected null value");
		}
		return value;
	}

	/**
	 * Returns a void string if the given value is null
	 * 
	 * @param value
	 * @return
	 */
	protected String voidIfNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}
}
