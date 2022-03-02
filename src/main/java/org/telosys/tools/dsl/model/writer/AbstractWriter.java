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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.telosys.tools.commons.FileUtil;

public abstract class AbstractWriter {

	private final String directory;
	private OutputStream outputStream;
	private PrintWriter  printWriter;
	
	/**
	 * Constructor
	 * @param directory
	 */
	protected AbstractWriter(String directory ) {
		super();
		this.directory = directory ;
	}

	protected void openFile(String fileName) {
		String fullFileName = FileUtil.buildFilePath(directory, fileName); 
		try {
			this.outputStream = new FileOutputStream(fullFileName);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("FileNotFoundException : Cannot write file " + fullFileName ); 
		}
		this.printWriter = new PrintWriter(outputStream);
	}
	
	protected void printLine(String line) {
		printWriter.println(line);
		printWriter.flush();
	}
	protected void printLines(List<String> lines) {
		for (String line : lines ) {
			printLine(line);
		}
	}
	
	protected void closeFile() {
		printWriter.close();
		try {
			this.outputStream.close();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot close OutputStream" );
		}
	}
}
