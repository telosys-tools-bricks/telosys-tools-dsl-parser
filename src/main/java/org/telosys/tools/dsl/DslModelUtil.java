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
package org.telosys.tools.dsl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.commons.DirUtil;
import org.telosys.tools.commons.FileUtil;
import org.telosys.tools.commons.StrUtil;
import org.telosys.tools.dsl.commons.ModelInfo;
import org.telosys.tools.dsl.model.writer.ModelInfoFileWriter;

public class DslModelUtil {
	
	private static final int FULLPATH_NAME = 1 ;
	private static final int SIMPLE_NAME   = 2 ;
	private static final int ENTITY_NAME   = 3 ;
	
    private static final String DOT_ENTITY      = ".entity" ;
    private static final String MODEL_FILE_NAME = ModelInfo.FILE_NAME; // model.yaml

    private DslModelUtil() {
	}

	//-------------------------------------------------------------------------------------------------
    // Names from file name
    //-------------------------------------------------------------------------------------------------
    /**
     * @param modelName
     * @return
     */
        public static String getModelFileName() {
    	return MODEL_FILE_NAME ; // v 3.4.0
    }
    
    public static String getModelNameFromFolderName(String folderName) { // v 3.4.0
    	File folder = new File(folderName);
    	return folder.getName();
    }
    
    /**
     * Returns the entity name for the given entity file name
     * @param entityFile an entity file ( e.g. 'aaa/bbb/Car.entity' ) 
     * @return the entity name ( e.g. 'Car' for 'aaa/bbb/Car.entity' )
     */
    public static String getEntityName(File entityFile) {
    	return getFileNameWithoutExtension(entityFile, DOT_ENTITY) ;
    }

    private static String getFileNameWithoutExtension(File file, String extension) {
    	return removeExtension(file.getName(), extension);
    }
    
    private static String removeExtension(String fileName, String extension) {
    	int i = fileName.lastIndexOf(extension);
    	if ( i > 0 ) {
    		return fileName.substring(0, i);
    	}
    	else {
            String textError = "Invalid file name '" + fileName + "' (doesn't end with '" + extension + "')";
    		throw new RuntimeException(textError);
    	}
    }

    /**
     * Returns a list of entity absolute file names ( eg "/full/path/Student.entity" )<br>
     * List of all the files located in the model folder and ending with ".entity" <br>
     * 
     * @param modelFolder
     * @return
     */
    public static List<String> getEntityFullFileNames(File modelFolder) {
    	return getEntitiesFromModelFolder(modelFolder, FULLPATH_NAME); // v 3.4.0
    }
    
    /**
     * Returns a list of entity short file names ( eg "Student.entity" ) <br>
     * List of all the files located in the model folder and ending with ".entity" <br>
     * @param modelFile
     * @return
     */
    public static List<String> getEntityShortFileNames(File modelFolder) {  // v 3.4.0
    	return getEntitiesFromModelFolder(modelFolder, SIMPLE_NAME);
    }
    
    /**
     * Returns a list of entity names ( eg "Student" ) <br>
     * List of all the files located in the model folder and ending with ".entity" <br>
     * @param modelFolder
     * @return
     */
    public static List<String> getEntityNames(File modelFolder) {  // v 3.4.0
    	return getEntitiesFromModelFolder(modelFolder, ENTITY_NAME);
    }
    
    private static List<String> getEntitiesFromModelFolder(File modelFolder, int expectedName) {  // v 3.4.0
    	if ( ! modelFolder.exists() ) {
            String textError = "Model folder '"+ modelFolder.getAbsolutePath() + "' not found";
            throw new RuntimeException(textError);
    	}
    	if ( ! modelFolder.isDirectory() ) {
            String textError = "Model folder '"+ modelFolder.getAbsolutePath() + "' not a directory";
            throw new RuntimeException(textError);
    	}
    	
        List<String> entities = new LinkedList<>();
        String[] allFiles = modelFolder.list();
        for (String fileName : allFiles) {
        	if ( fileName.endsWith(DslModelUtil.DOT_ENTITY)) {
        		switch(expectedName) {
        		case FULLPATH_NAME :
        			entities.add(FileUtil.buildFilePath(modelFolder.getAbsolutePath(), fileName));
        			break;
        		case SIMPLE_NAME :
        			entities.add(fileName) ;
        			break;
        		case ENTITY_NAME :
        			entities.add(StrUtil.removeEnd(fileName, DslModelUtil.DOT_ENTITY)) ;
        			break;
        		}
        	}
        }
        return entities;
    }
    
    /**
     * Returns the entity file in the given model folder for the given entity name 
     * @param modelFolder
     * @param entityName
     * @return
     */
    public static File getEntityFile(File modelFolder, String entityName) {
    	if ( modelFolder.exists() && modelFolder.isDirectory() ) {
    		String entityFileName = entityName + DOT_ENTITY ;
    		String absoluteFileName = FileUtil.buildFilePath(modelFolder.getAbsolutePath(), entityFileName);
    		return new File(absoluteFileName);
    	}
    	else {
            throw new RuntimeException("Invalid model folder '" + modelFolder.getName() + "'");
    	}
    }
    
    //-----------------------------------------------------------------------------------------------------------
    // MODEL MANAGEMENT
    //-----------------------------------------------------------------------------------------------------------
    /**
     * Returns true if the given file is a valid "model file" : <br>
     * - the file name ends with ".model" <br>
     * - the file folder (parent) is the models folder ( eg "TelosysTools" ) <br>
     * - the file folder (parent) exists (if checkParentFolderExistence is true)
     * @param file the file to be checked
     * @param checkParentFolderExistence 
     * @return
     */
    public static boolean isValidModelFile(File file) {
    	return MODEL_FILE_NAME.equals(file.getName());
    }
    
    /**
     * Creates a new DSL model in the given folder <br>
     * Creates the 'model-name' directory with a void model information file
     * @param modelFolder
     */
    public static void createNewModel(File modelFolder) {
		if ( modelFolder.exists() ) {
			throw new RuntimeException("Model folder '"+modelFolder.getName()+"' already exists");
		}
		
		//--- Folder creation
		DirUtil.createDirectory(modelFolder);
		
		//--- Create model info file in the model directory (model.yaml)
		ModelInfoFileWriter modelInfoFilewriter = new ModelInfoFileWriter(modelFolder.getAbsolutePath());
		ModelInfo modelInfo = new ModelInfo();
		modelInfo.setTitle("My model title");
		modelInfo.setVersion("1.0.0");
		modelInfoFilewriter.writeModelInfoFile(modelInfo);
    }
    
    /**
     * Delete the DSL model identified by the given model folder
     * @param modelFolder
     */
    public static void deleteModel(File modelFolder) {
		if ( modelFolder.exists() ) {
			DirUtil.deleteDirectory(modelFolder);
		}
    }

    /**
     * Returns all the models (directories) located in the given folder
     * @param folder
     * @return
     */
    public static List<File> getModelsInFolder(File folder) {
		List<File> list = new LinkedList<>();
		if ( folder.exists() && folder.isDirectory() ) {
			for ( File file : folder.listFiles() ) {
				if ( file.isDirectory() ) {
					list.add(file);
				}
			}
		}
		return list ;
    }
    
    //-----------------------------------------------------------------------------------------------------------
    // ENTITY MANAGEMENT
    //-----------------------------------------------------------------------------------------------------------
    /**
     * Creates a new entity file 
     * (the first caracter of the entity name is converted tu uppper-case if necessary)
     * @param modelFolder the model folder where to create the entity
     * @param entityName the entity name ( e.g. 'Book' or 'Car' )
     * @return the entity file 
     */
    public static File createNewEntity(File modelFolder, String entityName) {
    	String entityNameInModel = StrUtil.capitalize(entityName);
    	StringBuilder sb = new StringBuilder();
    	sb.append("// Entity "); sb.append(entityNameInModel); sb.append("\n");
    	sb.append("\n");
    	sb.append(entityNameInModel); sb.append(" {\n");
    	sb.append("  myfield : string ; // field example \n" );
    	sb.append("}\n");
    	String content = sb.toString() ;
    	
    	File entityFile = getEntityFile(modelFolder, entityNameInModel);
    	if ( ! entityFile.exists() ) {
        	try {
				FileUtil.copy(content, entityFile, true);
	        	return entityFile ;
			} catch (Exception e) {
				throw new RuntimeException("Cannot create new entity '" + entityNameInModel + "'", e);
			}
    	}
    	else {
    		throw new RuntimeException("File '" + entityFile.getName() + "' already exists");
    	}
    }

    /**
     * Deletes an entity in the given model folder
     * @param modelFolder
     * @param entityName name of the entity to be deleted
     * @return
     */
    public static boolean deleteEntity(File modelFolder, String entityName) {
		File entityFile = getEntityFile(modelFolder, entityName);
		if ( entityFile.exists() ) {
			return entityFile.delete() ;
		}
		else {
			return false ;
		}
    }

    public static File renameEntity(File currentEntityFile, String newEntityName) {
    	String newEntityFileName = FileUtil.buildFilePath(
    			currentEntityFile.getParentFile().getAbsolutePath(), newEntityName + DOT_ENTITY);
    	File newEntityFile = new File(newEntityFileName) ;
    	boolean renamed = currentEntityFile.renameTo(newEntityFile);
    	if ( ! renamed ) {
    		throw new RuntimeException("Cannot rename " + currentEntityFile.getName() + " to " + newEntityFile.getName());
    	}
    	return newEntityFile;
    }
    
    public static File getModelFileFromEntityFile(File entityFile ) {
       	if ( isValidEntityFile(entityFile) ) {
    		String modelFolderPath = entityFile.getParentFile().getAbsolutePath();
    		return new File(FileUtil.buildFilePath(modelFolderPath, MODEL_FILE_NAME));
    	}
    	else {
    		return null ;
    	}
    }
    
    public static File getModelFileFromModelFolder(File modelFolder ) {
    	return new File(FileUtil.buildFilePath(modelFolder.getAbsolutePath(), MODEL_FILE_NAME));
    }
    
    public static boolean isValidEntityFile(File file) {
    	// check file name ends with ".entity"
		return file.getName().endsWith(DOT_ENTITY);
    }

}
