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

import org.telosys.tools.dsl.commons.ModelInfo;

public class ModelInfoFileWriter extends AbstractWriter {

	/**
	 * Constructor
	 * @param directory
	 */
	public ModelInfoFileWriter(String directory ) {
		super(directory);
	}

	/**
	 * Writes model info in file
	 * @param modelInfo
	 */
	public void writeModelInfoFile(ModelInfo modelInfo) {
		openFile(ModelInfo.FILE_NAME);
		printLine("# Telosys model info ");
		printLine(ModelInfo.TITLE + ": " + modelInfo.getTitle());
		printLine(ModelInfo.VERSION + ": " + modelInfo.getVersion());
		printLine(ModelInfo.DESCRIPTION + ": " + modelInfo.getDescription());
		closeFile();
	}
}
