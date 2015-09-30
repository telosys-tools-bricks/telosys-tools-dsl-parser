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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre, Laurent Guerin
 * 
 * @version 1.0
 */
public class EntityParser extends AbstractParser {

//    /**
//     * Content of the File
//     */
//    private String formattedContent;

//	private String entityFileName = "" ;
	
    /**
     * Flatten Content of the File
     */
    //private String flattenContent;

    /**
     * fieldParser used to parse fields
     */
    private FieldParser fieldParser;

//    private Logger logger;

    public EntityParser(DomainModel model) {
    	super(LoggerFactory.getLogger(EntityParser.class));
//        this.formattedContent = "";
//        this.flattenContent = "";
        this.fieldParser = new FieldParser(model);
//        this.logger = LoggerFactory.getLogger(EntityParser.class);
    }

//    public EntityParser(String formattedContent, DomainModel model) {
//        this.formattedContent = formattedContent;
//        this.flattenContent = "";
//        this.fieldParser = new FieldParser(model);
//        this.logger = LoggerFactory.getLogger(EntityParser.class);
//    }
//
    /**
     * @param fileName
     */
    public DomainEntity parse(String fileName) {
        File entityFile = new File(fileName);
        logInfo("--- fileName = " + fileName);
//        this.entityFileName = entityFile.getName();
//        logger.info("--- this.entityFileName = " + this.entityFileName);
        return this.parse(entityFile);
    }

    /**
     * @param file
     */
    protected DomainEntity parse(File file) {
    	InputStream is ;
        try {
//            if (!file.exists()) {
//                throw new FileNotFoundException();
//            }
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new EntityParserException( "File Not found : "+ file.getAbsolutePath() );
        }
        String entityNameFromFileName = ParserUtil.getEntityName(file);
        return this.parse(is, entityNameFromFileName);
    }

    /**
     * @param is
     */
    private DomainEntity parse(InputStream is, String entityNameFromFileName) {
//        File file = new File(path);

//        formattedContent = StringUtils.readStream(is);
        String originalContent = StringUtils.readStream(is);
//        flattenContent = computeFlattenContent();
        String flattenContent = ParserUtil.preprocessText(originalContent);
//        int indexPoint = file.getName().lastIndexOf('.');
//        if (indexPoint >= 0) {
//            return parseFlattenContent(file.getName().substring(0, indexPoint));
//        } else {
//            throw new EntityParserException(this.entityFileName + " : The filename has no extension");
//        }
        
        return parseFlattenContent(flattenContent, entityNameFromFileName) ;
    }

/***
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
***/

    /**
     * @param flattenContent the file content after flatten
     * @param entityNameFromFileName
     * @return
     */
    protected DomainEntity parseFlattenContent(String flattenContent, String entityNameFromFileName) {
    	logInfo("Parsing entity " + entityNameFromFileName);

        // get index of first and last open brackets
        int bodyStart = flattenContent.indexOf('{');
        int bodyEnd = flattenContent.lastIndexOf('}');

        checkStructure(entityNameFromFileName, bodyStart, bodyEnd);

        // body required
        if (bodyEnd - bodyStart == 1) {
//            String errorMessage = entityNameFromFileName + " : A field is required";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "An entity must contains at least one field");
        }

        String entityNameInFile = flattenContent.substring(0, bodyStart).trim();

        // the filename must be equal to entity name
        if (!entityNameInFile.equals(entityNameFromFileName)) {
//            String errorMessage = entityNameFromFileName + " : The name of the file does not match with the entity name '" + entityNameInFile +"' ";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "Entity name '" + entityNameInFile +"' doesn't match with file name ");
        }

        // the first later of an entity must be upper case
        if (!Character.isUpperCase(flattenContent.charAt(0))) {
//            String errorMessage = entityNameFromFileName + " : The name of the entity must start with an upper case";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "Entity name must start with an upper case");
        }

        // only simple chars are allowed
        if (!entityNameInFile.matches("^[A-Z][\\w]*$")) {
//            String errorMessage = entityNameFromFileName + " : The name must not contains special char " + entityNameInFile;
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "Entity name '" + entityNameInFile +"' must not contains special char ");
        }

        // create object
        DomainEntity domainEntity = new DomainEntity(entityNameInFile);

        // find all fields
        String body = flattenContent.substring(bodyStart + 1, bodyEnd).trim();
        if (body.lastIndexOf(';') != body.length() - 1) {
//            String errorMessage = entityNameFromFileName + " : A semilicon is missing";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "Semicolon is missing");
        }

        String[] fieldList = body.split(";");

        // extract fields
        for (String field : fieldList) {
            DomainEntityField f = fieldParser.parseField(entityNameFromFileName, field.trim());
            domainEntity.addField(f);
        }
        verifyEntityStructure(domainEntity, entityNameFromFileName);
        return domainEntity;
    }

    /**
     * Check if the main structure of the file correspond to the specifications
     *
     * @param bodyStart first bracket index
     * @param bodyEnd   last bracket index
     */
    private void checkStructure(String entityNameFromFileName, int bodyStart, int bodyEnd) {
        // name required before body
        if (bodyStart < 0) {
//            String errorMessage = entityNameFromFileName + " : There's something wrong at the beginning of the body";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "There's something wrong at the beginning of the body");
        }

        // end of body required
        if (bodyEnd < 1) {
//            String errorMessage = entityNameFromFileName + " : There's something wrong at the end of the body";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "There's something wrong at the end of the body");
        }
    }

    /**
     * Verify the sructure of an entity
     * @param entity
     * @param entityNameFromFileName
     * @throws EntityParserException
     */
    private void verifyEntityStructure(DomainEntity entity, String entityNameFromFileName) throws EntityParserException {
        DomainEntityField fieldWithId = null;
        for (DomainEntityField tmp : entity.getFields()) {
            if (tmp.getAnnotationNames().contains("Id")) {
                if (fieldWithId != null) {
                    throw new EntityParserException(entityNameFromFileName + " : The Id is defined more than once"
                    			+ " (entity " + entity.getName() + ")" );
                }
                if (tmp.getCardinality() != 1) {
                    throw new EntityParserException(entityNameFromFileName + " : The Id can't be an array"
                    			+ " (entity " + entity.getName() + ")" );
                }
                if (tmp.isNeutralType()) {
                    if (tmp.getTypeName().equals(DomainNeutralTypes.BINARY_BLOB) || tmp.getTypeName().equals(DomainNeutralTypes.LONGTEXT_CLOB)) {
                        throw new EntityParserException(entityNameFromFileName + " : The Id can't be a binary (BLOB) ou a longtext (CLOB)"
                    			+ " (entity " + entity.getName() + ")" );
                    }
                }
                fieldWithId = tmp;
            }
        }
    }

//    public String getFormattedContent() {
//        return formattedContent;
//    }

//    protected String getFlattenContent() {
//        return flattenContent;
//    }
//
//    /**
//     * For unit tests only
//     * @param flattenContent
//     */
//    protected void setFlattenContent(String flattenContent) {
//        this.flattenContent = flattenContent;
//    }
}
