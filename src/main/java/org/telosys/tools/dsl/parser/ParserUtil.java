/**
 *  Copyright (C) 2008-2015  Telosys project org. ( http://www.telosys.org/ )
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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.telosys.tools.dsl.EntityParserException;

public class ParserUtil {
	
    private final static String COMMENT_REGEXP = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)" ;
	
    /**
     * Removes the comments from the given text ( single-line and multi-line comments )
     * @param initialText
     * @return
     */
    private static String removeComments (String initialText) {
    	return initialText.replaceAll(COMMENT_REGEXP,"");
    }
    
    /**
     * Flattens the given text : <br>
     * - removes all EOL characters (CR-LF) <br>
     * - removes whitespaces and TABS at the beginning/end of each line (trim) <br>
     * 
     * @param initialText
     * @return
     */
    private static String flattenText (String initialText) {
        StringTokenizer tokenizer = new StringTokenizer(initialText, "\r\n");
        StringBuilder stringBuilder = new StringBuilder();
        while (tokenizer.hasMoreElements()) {
            String line = tokenizer.nextElement().toString();

// Already done before
//            //--- Remove the right part of each single-line comment ( "//" )
//            if (line.contains(KeyWords.getSingleLineComment())) {
//                line = line.substring(0, line.indexOf(KeyWords.getSingleLineComment() ) );
//            }

            //--- Remove every "whitespace" (including TAB) at the beginning and at the end
            if (line.length() > 0) {
                stringBuilder.append(line.trim());
            }
            
            // At this step "whitespace" and TAB can be still present in the middle of the string
        }
        return stringBuilder.toString();
    }

    public static String preprocessText (String initialText) {
        return flattenText( removeComments(initialText) );
    }

    //-------------------------------------------------------------------------------------------------
    // Names from file name
    //-------------------------------------------------------------------------------------------------
    private static final String DOT_MODEL           = ".model"  ;
    private static final String DOT_ENTITY          = ".entity" ;
    private static final String MODEL_FOLDER_SUFFIX = "_model"  ;

    /**
     * Returns the model name for the given file name
     * @param file eg 'aaa/bbb/foo.model' 
     * @return 'foo' for 'aaa/bbb/foo.model' 
     */
    public static String getModelName(File file) {
    	return getFileNameWithoutExtension(file, DOT_MODEL) ;
    }
    
    /**
     * Returns the entity name for the given file name
     * @param file an entity file, e.g. 'aaa/bbb/Car.entity' 
     * @return 'Car' for 'aaa/bbb/Car.entity' 
     */
    public static String getEntityName(File file) {
    	return getFileNameWithoutExtension(file, DOT_ENTITY) ;
    }

    private static String getFileNameWithoutExtension(File file, String extension) {
    	String fileName = file.getName();
    	int i = fileName.lastIndexOf(extension);
    	if ( i > 0 ) {
    		return fileName.substring(0, i);
    	}
    	else {
            String textError = "Invalid file name '" + fileName + "' (doesn't end with '" + extension + "')";
    		throw new EntityParserException(textError);
    	}
    }
    
    public static void checkModelFile(File file) {
        if ( ! file.exists() ) {
            String textError = "File '" + file.toString() + "' not found";
            //logger.error(textError);
            throw new EntityParserException(textError);
        }
        if ( ! file.isFile() ) {
            String textError = "'" + file.toString() + "' is not a file";
            //logger.error(textError);
            throw new EntityParserException(textError);
        }
        if ( ! file.getName().endsWith(DOT_MODEL)) {
            String textError = "File '" + file.toString() + "' doesn't end with '" + DOT_MODEL + "'";
            //logger.error(textError);
            throw new EntityParserException(textError);
        }
    }
    
    /**
     * Returns a list of entities absolute file names <br>
     * List of all the files located in the model folder and ending with ".entity" <br>
     * 
     * @param modelFile
     * @return
     */
    public static List<String> getEntitiesAbsoluteFileNames(File modelFile) {

    	String modelName = getModelName(modelFile) ;

    	String modelFolderAbsolutePath = modelFile.getParentFile().getAbsolutePath() 
    			+ "/" + modelName + MODEL_FOLDER_SUFFIX ;
    	File folder = new File(modelFolderAbsolutePath);
    	if ( folder.exists() ) {
            List<String> entities = new LinkedList<String>();

            String[] allFiles = folder.list();
            for (String fileName : allFiles) {
            	if ( fileName.endsWith(ParserUtil.DOT_ENTITY)) {
            		entities.add( folder.getAbsolutePath() + "/" + fileName ) ;
            	}
            }
            return entities;
    	}
    	else {
            String textError = "Model folder '"+ modelFolderAbsolutePath + "' not found";
//            logger.error(textError);
            throw new EntityParserException(textError);
    	}
    }
    
}
