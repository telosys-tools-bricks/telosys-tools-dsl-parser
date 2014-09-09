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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.EntityParserException;
import org.telosys.tools.dsl.parser.model.DomainEnumerationItem;
import org.telosys.tools.dsl.parser.model.DomainEnumerationType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @param <T>
 */
public class FieldEnumParser {
    private Logger logger = LoggerFactory.getLogger(FieldEnumParser.class);

    DomainEnumerationItem parseField(String fieldInfo, BigInteger value) {
        if (!isItemWithoutValue(fieldInfo)) {
            throw new EntityParserException("this item has to be without value : " + fieldInfo);
        }

        checkName(fieldInfo);
        return new DomainEnumerationItem(fieldInfo, value.add(new BigInteger("1")));
    }

    /**
     * @param fieldInfo
     * @return
     */
    DomainEnumerationItem parseField(String fieldInfo, DomainEnumerationType type) {
        if (!fieldInfo.contains("=")) {
            String textError = "The fields does not contains value " + fieldInfo;
            logger.error(textError);
            throw new EntityParserException(textError);
        }
        int startDescription = fieldInfo.indexOf('=');
        String name = fieldInfo.substring(0, startDescription).trim();
        checkName(name);

        int end = fieldInfo.length();

        String value = fieldInfo.substring(++startDescription, end).trim();
        if (value.length() == 0) {
            String textError = "The value of the field is missing";
            logger.error(textError);
            throw new EntityParserException(textError);
        }

        DomainEnumerationItem field = null;
        if (type == DomainEnumerationType.INTEGER) {
            field = new DomainEnumerationItem(name, (BigInteger) getValue(value, DomainEnumerationType.INTEGER));
        } else if (type == DomainEnumerationType.DECIMAL) {
            field = new DomainEnumerationItem(name, (BigDecimal) getValue(value, DomainEnumerationType.DECIMAL));
        } else if (type == DomainEnumerationType.STRING) {
            field = new DomainEnumerationItem(name, (String) getValue(value, DomainEnumerationType.STRING));
        }
        return field;
    }

    public Object getValue(String value, DomainEnumerationType type) {
        switch (type) {
            case INTEGER:
                try {
                    return new BigInteger(value);

                } catch (NumberFormatException e) {
                    String textError = "the value : " + value + " must be a Big Integer";
                    logger.error(textError);
                    throw new EntityParserException(textError);
                }

            case DECIMAL:
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    String textError = "the value : " + value + " must be a Big Decimal";
                    logger.error(textError);
                    throw new EntityParserException(textError);
                }

            case STRING:

                if (value.charAt(0) == '"' && value.charAt(1) != '"'
                        && value.charAt(value.length() - 1) == '"') {
                    return value.substring(1, value.length() - 1);
                } else {
                    String textError = "the value : " + value + " must be a String";
                    logger.error(textError);
                    throw new EntityParserException(textError);
                }

        }
        // unreachable code
        return null;
    }

    private void checkName(String name) {
        if (!name.matches("^[A-Z]*$")) {
            String textError = "The name of the fields must contains only uppercase "
                    + name;
            logger.error(textError);
            throw new EntityParserException(
                    textError);
        }
        if (name.length() == 0) {
            String textError = "The name of the field is missing";
            logger.error(textError);
            throw new EntityParserException(textError);
        }
    }

    public boolean isItemWithoutValue(String item) {
        return !item.contains("=");
    }
}
