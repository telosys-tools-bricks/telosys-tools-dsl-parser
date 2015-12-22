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
package org.telosys.tools.dsl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.FileUtil;

public class DslModelUtil {
	
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
    		throw new RuntimeException(textError);
    	}
    }

    public static File getModelFolder(File modelFile) {
    	String modelName = getModelName(modelFile) ;
    	String modelFolderAbsolutePath = modelFile.getParentFile().getAbsolutePath() 
    			+ "/" + modelName + MODEL_FOLDER_SUFFIX ;
    	return new File(modelFolderAbsolutePath);
    }
    
    /**
     * Returns a list of entities absolute file names <br>
     * List of all the files located in the model folder and ending with ".entity" <br>
     * 
     * @param modelFile
     * @return
     */
    public static List<String> getEntitiesAbsoluteFileNames(File modelFile) {

    	File modelFolder = getModelFolder(modelFile);
    	if ( modelFolder.exists() ) {
            List<String> entities = new LinkedList<String>();

            String[] allFiles = modelFolder.list();
            for (String fileName : allFiles) {
            	if ( fileName.endsWith(DslModelUtil.DOT_ENTITY)) {
            		entities.add( modelFolder.getAbsolutePath() + "/" + fileName ) ;
            	}
            }
            return entities;
    	}
    	else {
            String textError = "Model folder '"+ modelFolder.getAbsolutePath() + "' not found";
            throw new RuntimeException(textError);
    	}
    }
    
//    /**
//     * Loads the model file properties ( model information ) 
//     * @param modelFile
//     * @return
//     */
//    public static Properties loadModelProperties(File modelFile) {
//        Properties props = new Properties();
//        FileInputStream fis = null;
//
//        try {
//            fis = new FileInputStream(modelFile);
//            props.load(fis);
//        } catch (IOException ioe) {
//            String textError = "Cannot load properties from file "+ modelFile;
//            //logger.error(textError);
//            throw new RuntimeException(textError + " (IOException : " + ioe.getMessage() + ")");
//        } finally {
//
//            try {
//                if (fis != null) {
//                    fis.close();
//                }
//            } catch (IOException e) {
//                // NOTHING TO DO
//            }
//        }
//        return props;
//    }

    /**
     * Builds the file for the given entity name and the model file it belongs to
     * @param modelFile
     * @param entityName the entity name without the file extension ( e.g. 'Book' or 'Car' ) 
     * @return
     */
    public static File buildEntityFile(File modelFile, String entityName) {
    	File modelFolder = getModelFolder(modelFile);
    	if ( modelFolder.exists() && modelFolder.isDirectory() ) {
    		String absoluteFileName = modelFolder.getAbsolutePath() + "/" + entityName + DOT_ENTITY ;
    		return new File(absoluteFileName);
    	}
    	else {
            throw new RuntimeException("Invalid model folder '" + modelFolder.getName() + "'");
    	}
    }
    
    /**
     * Creates a new entity file 
     * @param modelFile the model file defining the model for the entity to be created
     * @param entityName the entity name ( e.g. 'Book' or 'Car' )
     * @return the entity file 
     */
    public static File createNewEntity(File modelFile, String entityName) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("// Entity "); sb.append(entityName); sb.append("\n");
    	sb.append("\n");
    	sb.append(entityName); sb.append(" {\n");
    	sb.append("  myfield : string ; // field example \n" );
    	sb.append("}\n");
    	String content = sb.toString() ;
    	
    	File entityFile = buildEntityFile(modelFile, entityName);
    	if ( ! entityFile.exists() ) {
        	try {
				FileUtil.copy(content, entityFile, true);
	        	return entityFile ;
			} catch (Exception e) {
				throw new RuntimeException("Cannot create new entity '" + entityName + "'", e);
			}
    	}
    	else {
    		throw new RuntimeException("File '" + entityFile.getName() + "' already exists");
    	}
    }

    public static void renameEntity(File currentEntityFile, String newEntityName) {
    	String newEntityFileName = currentEntityFile.getParentFile().getAbsolutePath() + "/" + newEntityName + DOT_ENTITY ;
    	File newEntityFile = new File(newEntityFileName) ;
    	boolean renamed = currentEntityFile.renameTo( newEntityFile );
    	if ( ! renamed ) {
    		throw new RuntimeException("Cannot rename " + currentEntityFile.getName() + " to " + newEntityFile.getName());
    	}
    }
}
