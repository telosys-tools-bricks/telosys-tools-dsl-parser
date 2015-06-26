/**
 * Copyright (C) 2008-2014  Telosys project org. ( http://www.telosys.org/ )
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.utils.StringUtils;

/**
 * First entry point for the telosys entity parser
 *
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre
 * @version 1.0
 */
public class EntityParser {

    /**
     * Content of the File
     */
    private String formattedContent;

    /**
     * Flatten Content of the File
     */
    private String flattenContent;

    /**
     * fieldParser used to parse fields
     */
    private FieldParser fieldParser;

    private Logger logger;

    public EntityParser(DomainModel model) {
        this.formattedContent = "";
        this.flattenContent = "";
        this.fieldParser = new FieldParser(model);
        this.logger = LoggerFactory.getLogger(EntityParser.class);
    }

    public EntityParser(String formattedContent, DomainModel model) {
        this.formattedContent = formattedContent;
        this.flattenContent = "";
        this.fieldParser = new FieldParser(model);
        this.logger = LoggerFactory.getLogger(EntityParser.class);
    }

    /**
     * @param fileName
     */
    public DomainEntity parse(String fileName) {
        return this.parse(new File(fileName));
    }

    /**
     * @param file
     */
    public DomainEntity parse(File file) {
        try {
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            InputStream io = new FileInputStream(file);
            return this.parse(io, file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new EntityParserException("File Not found : "+ file.getAbsolutePath() + "\n Documentation : " + e);
        }

    }

    /**
     * @param is
     */
    public DomainEntity parse(InputStream is, String path) {
        File file = new File(path);

        formattedContent = StringUtils.readStream(is);
        flattenContent = computeFlattenContent();
        int indexPoint = file.getName().lastIndexOf('.');
        if (indexPoint >= 0) {
            return parseFlattenContent(file.getName().substring(0, indexPoint));
        } else {
            throw new EntityParserException("The filename has no extension");
        }
    }

//    public String computeFlattenContent() {
    private String computeFlattenContent() {
//        StringTokenizer content = new StringTokenizer(formattedContent, "\r\n");
//        StringBuilder stringBuilder = new StringBuilder();
//        while (content.hasMoreElements()) {
//            String line = content.nextElement().toString();
//
////            if (line.contains(TelosysDSLProperties.getProperties().getProperty("start_comment"))) {
////                line = line.substring(0,
////                        line.indexOf(TelosysDSLProperties.getProperties().getProperty("start_comment")));
////            }
//            if (line.contains(KeyWords.getSingleLineComment())) {
//                line = line.substring(0, line.indexOf(KeyWords.getSingleLineComment() ) );
//            }
//
//            if (line.length() > 0) {
//                stringBuilder.append(line.trim());
//            }
//        }
//        return stringBuilder.toString();
    	return ParserUtil.preprocessText(formattedContent);
    }


    /**
     * @param filename The filename to check the content
     * @return An entity wich contain the name of the entity, and all its fields
     */
    public DomainEntity parseFlattenContent(String filename) {
        this.logger.info("Parsing of the file " + filename);

        // get index of first and last open brackets
        int bodyStart = flattenContent.indexOf('{');
        int bodyEnd = flattenContent.lastIndexOf('}');

        checkStructure(bodyStart, bodyEnd);

        // body required
        if (bodyEnd - bodyStart == 1) {
            String errorMessage = "A field is required";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        String entityName = flattenContent.substring(0, bodyStart).trim();

        // the filename must be equal to entity name
        if (!entityName.equals(filename)) {
            String errorMessage = "The name of the file does not match with the entity name '" + filename +"' ";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        // the first later of an entity must be upper case
        if (!Character.isUpperCase(flattenContent.charAt(0))) {
            String errorMessage = "The name of the entity must start with an upper case";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        // only simple chars are allowed
        if (!entityName.matches("^[A-Z][\\w]*$")) {
            String errorMessage = "The name must not contains special char " + entityName;
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        // create object
        DomainEntity table = new DomainEntity(entityName);

        // find all fields
        String body = flattenContent.substring(bodyStart + 1, bodyEnd).trim();
        if (body.lastIndexOf(';') != body.length() - 1) {
            String errorMessage = "A semilicon is missing";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        String[] fieldList = body.split(";");

        // extract fields
        for (String field : fieldList) {
            DomainEntityField f = fieldParser.parseField(filename, field.trim());
            table.addField(f);
        }
        verifyEntityStructure(table);
        return table;
    }

    /**
     * Check if the main structure of the file correspond to the specifications
     *
     * @param bodyStart first bracket index
     * @param bodyEnd   last bracket index
     */
    private void checkStructure(int bodyStart, int bodyEnd) {
        // name required before body
        if (bodyStart < 0) {
            String errorMessage = "There's something wrong with the beginning of the body";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }

        // end of body required
        if (bodyEnd < 1) {
            String errorMessage = "There's something wrong with the end of the body";
            this.logger.error(errorMessage);
            throw new EntityParserException(errorMessage);
        }
    }

    /**
     * Verify the sructure of an entity
     *
     * @param entity
     * @throws EntityParserException
     */
    public void verifyEntityStructure(DomainEntity entity) throws EntityParserException {
        DomainEntityField fieldWithId = null;
        for (DomainEntityField tmp : entity.getFields()) {
            if (tmp.getAnnotationNames().contains("Id")) {
                if (fieldWithId != null) {
                    throw new EntityParserException("The Id is defined more than once in the entity " + entity.getName());
                }
                if (tmp.getCardinality() != 1) {
                    throw new EntityParserException("The Id can't be in an array in the entity " + entity.getName());
                }
                if (tmp.isNeutralType()) {
                    if (tmp.getTypeName().equals(DomainNeutralTypes.BLOB) || tmp.getTypeName().equals(DomainNeutralTypes.CLOB)) {
                        throw new EntityParserException("The Id can't be a blob ou a clob in the entity " + entity.getName());
                    }
                }
                fieldWithId = tmp;
            }
        }
    }

//    public String getFormattedContent() {
//        return formattedContent;
//    }

    public String getFlattenContent() {
        return flattenContent;
    }

    public void setFlattenContent(String flattenContent) {
        this.flattenContent = flattenContent;
    }
}
