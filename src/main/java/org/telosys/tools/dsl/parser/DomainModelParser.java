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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DomainModelParser {

    private static final String DOT_MODEL = ".model";
    private static final String DOT_ENTITY = ".entity";
    private static final String DOT_ENUM = ".enum";
    private Logger logger = LoggerFactory.getLogger(DomainModelParser.class);

    /**
     * Parse the given model
     *
     * @param file the ".model" file or a directory containing a ".model" file
     * @return
     */
    public final DomainModel parse(File file) {

        if (!file.exists()) {
            String textError = "Cannot parse model : file '" + file.toString() + "' doesn't exist";
            logger.error(textError);
            throw new EntityParserException(textError);
        }
        if (file.isFile()) {
            if (file.getName().endsWith(DOT_MODEL)) {
                return parseModelFile(file);
            } else {
                String textError = "Cannot parse model : file '" + file.toString() + "' is not a model";
                logger.error(textError);
                throw new EntityParserException(textError);
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(DOT_MODEL)) {
                    return parseModelFile(f);
                }
            }
            String textError = "Cannot parse model : no model file in '" + file.toString() + "'";
            logger.error(textError);
            throw new EntityParserException(textError);
        } else {
            String textError = "Cannot parse model : '" + file.toString() + "' is not a file or directory";
            logger.error(textError);
            throw new EntityParserException(textError);
        }
    }

    private final DomainModel parseModelFile(File file) {

        Properties p = loadProperties(file);
        String modelName = p.getProperty("name");
        if (modelName == null || modelName.trim().length() == 0) {
            // use the file name as default name
            String fileName = file.getName();
            int i = fileName.indexOf(DOT_MODEL);
            modelName = fileName.substring(0, i);
        }

        File folder = file.getParentFile();
        Map<String, List<String>> files = getMapFiles(folder);
        
        DomainModel model = new DomainModel(modelName);
        
//        // ENUMERATIONS ( .enum files )
//        List<String> enumerations = files.get(DOT_ENUM);
//        EnumerationParser enumParser = new EnumerationParser();
//        for (String enumeration : enumerations) {
//            model.addEnumeration(enumParser.parse(new File(enumeration)));
//        }

        // ENTITIES ( .entity files )
        List<String> entities = files.get(DOT_ENTITY);
        for (String entity : entities) {
            File entityFile = new File(entity);
            model.addEntity(new DomainEntity(entityFile.getName().substring(0,entityFile.getName().lastIndexOf('.'))));
        }

        EntityParser entityParser = new EntityParser(model);
        for (String entity : entities) {
            model.putEntity(entityParser.parse(entity));
        }
        return model;
    }

    private Properties loadProperties(File propFile) {
        Properties props = new Properties();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(propFile);
            props.load(fis);
        } catch (IOException ioe) {
            String textError = "Cannot load properties from file "+ propFile;
            logger.error(textError);
            throw new EntityParserException(textError + "\n Documentation : " +ioe);
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                // NOTHING TO DO
            }
        }
        return props;
    }

    /**
     * Get all files name and their associate class from a folder
     *
     * @param folder
     * @return
     */
    private Map<String, List<String>> getMapFiles(File folder) {
        Map<String, List<String>> files = new HashMap<String, List<String>>();
        files.put(DOT_ENTITY, new ArrayList<String>());
        files.put(DOT_ENUM, new ArrayList<String>());

        String[] allFiles = folder.list();
        for (String fileName : allFiles) {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            if (files.containsKey(extension)) {
                List<String> current = files.get(extension);
                current.add(folder.getAbsolutePath() + "/" + fileName);
                files.put(extension, current);
            }
        }
        return files;
    }

}
