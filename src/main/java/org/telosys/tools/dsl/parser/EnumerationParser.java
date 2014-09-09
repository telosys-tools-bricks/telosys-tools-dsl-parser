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
import java.math.BigInteger;

import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEnumeration;
import org.telosys.tools.dsl.parser.model.DomainEnumerationForDecimal;
import org.telosys.tools.dsl.parser.model.DomainEnumerationForInteger;
import org.telosys.tools.dsl.parser.model.DomainEnumerationForString;
import org.telosys.tools.dsl.parser.model.DomainEnumerationType;
import org.telosys.tools.dsl.parser.utils.StringUtils;

public class EnumerationParser {

    /**
     * Content of the File
     */
    private String formattedContent;

    /**
     * Flatten Content of the File
     */
    private String flattenContent;

    /**
     * Field Enummeration Parser
     */
    private FieldEnumParser fieldEnumParser;

    public EnumerationParser() {
        this.fieldEnumParser = new FieldEnumParser();
    }

    /**
     * @param fileName
     */
    public void parse(String fileName) {
        this.parse(new File(fileName));
    }

    /**
     * @param file
     */
    public DomainEnumeration<?> parse(File file) {
        try {
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            InputStream io = new FileInputStream(file);
            return this.parse(io, file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new EntityParserException("File Not found : " + file.getAbsolutePath() + "\n Documentation : " + e);
        }

    }

    /**
     * @param is
     */
    public DomainEnumeration<?> parse(InputStream is, String path) {
        File file = new File(path);

        formattedContent = StringUtils.readStream(is);
        flattenContent = computeFlattenContent();
        return parseFlattenContent(file.getName().substring(0, file.getName().lastIndexOf('.')));
    }

    public DomainEnumeration<?> parseFlattenContent(String filename) {
        // get index of first and last open brackets
        int bodyStart = flattenContent.indexOf('{');
        int bodyEnd = flattenContent.lastIndexOf('}');
        checkStructure(bodyStart, bodyEnd);

        // body required
        if (bodyEnd - bodyStart == 1) {
            throw new EntityParserException("A field is required");
        }
        String enumName = flattenContent.substring(0, bodyStart);
        DomainEnumerationType type = DomainEnumerationType.INTEGER;

        // read field infos
        String[] split = enumName.split(":");
        if (split.length == 2) {
            enumName = split[0].trim();
            String enumType = split[1].trim();

            // simple enum types
            if (enumType.equals("integer")) {
                type = DomainEnumerationType.INTEGER;
            } else if (enumType.equals("string")) {
                type = DomainEnumerationType.STRING;
            } else if (enumType.equals("decimal")) {
                type = DomainEnumerationType.DECIMAL;
            } else {
                throw new EntityParserException("The type of the Enum have to be int, string or decimal and nothing else");
            }
            // autoincrement enum type
        } else if (split.length == 1) { // If no type is defined it's an integer
            type = DomainEnumerationType.INTEGER;
        } else {
            throw new EntityParserException("There is something wrong with the head of the enum");
        }

        if (!enumName.equals(filename)) {
            throw new EntityParserException("The name of the file does not match with the enum name");
        } 
        // the first later of an entity must be upper case
        else if (!Character.isUpperCase(flattenContent.charAt(0))) {
            throw new EntityParserException("The name of the entity must start with an upper case");
        }
        // only simple chars are allowed
        else if (!enumName.matches("^[A-Z][\\w]*$")) {
            throw new EntityParserException("The name must not contains special char" + enumName);
        }
        // create object
        DomainEnumeration<?> enumeration = null;
        if (type == DomainEnumerationType.INTEGER) {
            enumeration = new DomainEnumerationForInteger(enumName);
        } else if (type == DomainEnumerationType.STRING) {
            enumeration = new DomainEnumerationForString(enumName);
        } else if (type == DomainEnumerationType.DECIMAL) {
            enumeration = new DomainEnumerationForDecimal(enumName);
        }

        // find all fields
        String body = flattenContent.substring(bodyStart + 1, bodyEnd).trim();

        String[] fieldEnumList = body.split(",");
        // at least 1 field is required
        if (fieldEnumList.length < 1) {
            throw new EntityParserException("This enum must contains at least one field");
        }
        // extract fields
        else if (type == DomainEnumerationType.INTEGER && fieldEnumParser.isItemWithoutValue(fieldEnumList[0])) {
            BigInteger previousValue = new BigInteger("0");
            for (String field : fieldEnumList) {
                enumeration.addItem(fieldEnumParser.parseField(field.trim(), previousValue));
                previousValue = previousValue.add(new BigInteger("1"));
            }
        } else {
            for (String field : fieldEnumList) {
                enumeration.addItem(fieldEnumParser.parseField(field.trim(), type));
            }
        }
        return enumeration;
    }
    
//    private <T> void populateEnumeration( final DomainEnumeration<T> enumeration, final int bodyStart, final int bodyEnd ) {
//
//        // find all fields
//        String body = flattenContent.substring(bodyStart + 1, bodyEnd).trim();
//
//        String[] fieldEnumList = body.split(",");
//        // at least 1 field is required
//        if (fieldEnumList.length < 1) {
//            throw new EntityParserException("This enum must contains at least one field");
//        }
//        
//        // extract fields
//        else if (type == TypeEnum.INTEGER && fieldEnumParser.isItemWithoutValue(fieldEnumList[0])) {
//            BigInteger previousValue = new BigInteger("0");
//            for (String field : fieldEnumList) {
//                enumeration.addItem(fieldEnumParser.parseField(field.trim(), previousValue));
//                previousValue = previousValue.add(new BigInteger("1"));
//            }
//        } else {
//            for (String field : fieldEnumList) {
//                enumeration.addItem(fieldEnumParser.parseField(field.trim(), type));
//            }
//        }
//    }
    
    /**
     * check the structure of the enumeration
     * @param bodyStart the first index
     * @param bodyEnd the last index
     */
    private void checkStructure(int bodyStart, int bodyEnd) {
        // name required before body
        if (bodyStart < 0) {
            throw new EntityParserException("There's something wrong with the beginning of the body");
        }
        // end of body required
        else if (bodyEnd < 1) {
            throw new EntityParserException("There's something wrong with the end of the body");
        }
    }
    /**
     * 
     * @return the flatten content without any comments
     */
    public String computeFlattenContent() {
//        StringTokenizer content = new StringTokenizer(formattedContent, "\r\n");
//        StringBuilder stringBuilder = new StringBuilder();
//        while (content.hasMoreElements()) {
//            String line = content.nextElement().toString();
//
//            if (line.contains(TelosysDSLProperties.getProperties().getProperty(
//                    "start_comment"))) {
//                line = line.substring(0, line.indexOf(TelosysDSLProperties.getProperties().getProperty("start_comment")));
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
     * 
     * @return the actual flatten content
     */
    public String getFlattenContent() {
        return flattenContent;
    }

    public void setFlattenContent(String flattenContent) {
        this.flattenContent = flattenContent;
    }
}
