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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.telosys.tools.commons.FileUtil;

/**
 * Abstract class with commons methods to write a model file in a directory
 * 
 * @author Laurent Guerin
 *
 */
public abstract class AbstractWriter {

	private final String directory;
	
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private PrintWriter  printWriter;
	
	/**
	 * Constructor
	 * @param directory
	 */
	protected AbstractWriter(String directory ) {
		super();
		this.directory = directory ;
	}

	/**
	 * Open the output file in the predefined directory <br>
	 * The charset for the output file is UTF-8
	 * 
	 * @param fileName
	 */
	protected void openFile(String fileName) {
		String fullFileName = FileUtil.buildFilePath(directory, fileName); 
		try {
			this.outputStream = new FileOutputStream(fullFileName);
		} catch (FileNotFoundException e) {
			throw new ModelWriterException("FileNotFoundException : Cannot write file " + fullFileName, e); 
		}
		// Use OutputStreamWriter to force charset to UTF-8
		this.outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
		this.printWriter = new PrintWriter(outputStreamWriter);
	}
	
	/**
	 * Print the given line in the output file
	 * @param line
	 */
	protected void printLine(String line) {
		printWriter.println(line);
		printWriter.flush();
	}
	
	/**
	 * Print the given lines in the output file
	 * @param lines
	 */
	protected void printLines(List<String> lines) {
		for (String line : lines ) {
			printLine(line);
		}
	}
	
	/**
	 * Close the output file
	 */
	protected void closeFile() {
		printWriter.close();
		try {
			this.outputStreamWriter.close();
			this.outputStream.close();
		} catch (IOException e) {
			throw new ModelWriterException("IOException : Cannot close OutputStream", e );
		}
	}

}
