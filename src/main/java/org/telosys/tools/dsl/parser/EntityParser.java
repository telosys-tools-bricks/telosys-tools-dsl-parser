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
package org.telosys.tools.dsl.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.telosys.tools.dsl.DslModelUtil;
import org.telosys.tools.dsl.DslParserException;
import org.telosys.tools.dsl.parser.model.DomainEntity;
import org.telosys.tools.dsl.parser.model.DomainEntityField;
import org.telosys.tools.dsl.parser.model.DomainModel;
import org.telosys.tools.dsl.parser.model.DomainNeutralTypes;
import org.telosys.tools.dsl.parser.utils.StringUtils;

/**
 * First entry point for the Telosys entity parser
 *
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre, Laurent Guerin
 * 
 * @version 1.0
 */
public class EntityParser extends AbstractParser {

    /**
     * fieldParser used to parse fields
     */
    private FieldParser fieldParser;

    public EntityParser(DomainModel model) {
    	//super(LoggerFactory.getLogger(EntityParser.class));
        this.fieldParser = new FieldParser(model);
    }

    /**
     * Parse the entity defined in the given file name
     * @param fileName
     */
    public DomainEntity parse(String fileName) {
        File entityFile = new File(fileName);
        return this.parse(entityFile);
    }

    /**
     * Parse the entity defined in the given File
     * @param file
     */
    protected DomainEntity parse(File file) {
    	InputStream is ;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new DslParserException( "File Not found : "+ file.getAbsolutePath() );
        }
        String entityNameFromFileName = DslModelUtil.getEntityName(file);
        return this.parse(is, entityNameFromFileName);
    }

    /**
     * Parse the entity defined in the given InputStream
     * @param is
     */
    private DomainEntity parse(InputStream is, String entityNameFromFileName) {

        String originalContent = StringUtils.readStream(is);
        String flattenContent = ParserUtil.preprocessText(originalContent);
        
        return parseFlattenContent(flattenContent, entityNameFromFileName) ;
    }

    /**
     * @param flattenContent the file content after flatten
     * @param entityNameFromFileName
     * @return
     */
    protected DomainEntity parseFlattenContent(String flattenContent, String entityNameFromFileName) {

        // get index of first and last open brackets
        int bodyStart = flattenContent.indexOf('{');
        int bodyEnd = flattenContent.lastIndexOf('}');

        checkStructure(entityNameFromFileName, bodyStart, bodyEnd);

        if (bodyEnd - bodyStart == 1) {
            throwParsingError(entityNameFromFileName, "An entity must contain at least one field");
        }

        String entityNameInFile = flattenContent.substring(0, bodyStart).trim();

        // the filename must be equal to entity name
        if (!entityNameInFile.equals(entityNameFromFileName)) {
            throwParsingError(entityNameFromFileName, "Entity name '" + entityNameInFile +"' doesn't match with file name ");
        }

        // the first later of an entity must be upper case
        if (!Character.isUpperCase(flattenContent.charAt(0))) {
            throwParsingError(entityNameFromFileName, "Entity name must start with an upper case");
        }

        // only simple chars are allowed
        if (!entityNameInFile.matches("^[A-Z][\\w]*$")) {
            throwParsingError(entityNameFromFileName, "Entity name '" + entityNameInFile +"' must not contains special char ");
        }

        // create object
        DomainEntity domainEntity = new DomainEntity(entityNameInFile);

        // find all fields
        String body = flattenContent.substring(bodyStart + 1, bodyEnd).trim();
        if (body.lastIndexOf(';') != body.length() - 1) {
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
            throwParsingError(entityNameFromFileName, "There's something wrong at the beginning of the body");
        }

        // end of body required
        if (bodyEnd < 1) {
            throwParsingError(entityNameFromFileName, "There's something wrong at the end of the body");
        }
    }

    /**
     * Verify the structure of an entity
     * @param entity
     * @param entityNameFromFileName
     * @throws DslParserException
     */
    private void verifyEntityStructure(DomainEntity entity, String entityNameFromFileName) throws DslParserException {
        DomainEntityField fieldWithId = null;
        for (DomainEntityField tmp : entity.getFields()) {
            if (tmp.getAnnotationNames().contains("Id")) {
                if (fieldWithId != null) {
                    throw new DslParserException(entityNameFromFileName + " : The Id is defined more than once"
                    			+ " (entity " + entity.getName() + ")" );
                }
                if (tmp.getCardinality() != 1) {
                    throw new DslParserException(entityNameFromFileName + " : The Id cannot be an array"
                    			+ " (entity " + entity.getName() + ")" );
                }
                if (tmp.isNeutralType()) {
                    if (tmp.getTypeName().equals(DomainNeutralTypes.BINARY_BLOB) ) {
                        throw new DslParserException(entityNameFromFileName + " : The Id cannot be a binary"
                    			+ " (entity " + entity.getName() + ")" );
                    }
                }
                fieldWithId = tmp;
            }
        }
    }

}
