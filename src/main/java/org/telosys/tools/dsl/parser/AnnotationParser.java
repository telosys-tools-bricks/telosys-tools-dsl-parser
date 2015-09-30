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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.telosys.tools.dsl.KeyWords;
import org.telosys.tools.dsl.parser.model.DomainEntityFieldAnnotation;

/**
 * @author Jonathan Goncalves, Mathieu Herbert, Thomas Legendre, Laurent Guerin
 * @version 1.0
 */
public class AnnotationParser extends AbstractParser  {
//    private Logger logger;

    public AnnotationParser() {
    	super(LoggerFactory.getLogger(EntityParser.class));

//        this.logger = LoggerFactory.getLogger(AnnotationParser.class);
    }

    /**
     * Parse field annotations located between brackets : '{ @xxx, @xxx }'
     * @param entityNameFromFileName
     * @param fieldInfo
     * @return
     */
    List<DomainEntityFieldAnnotation> parseAnnotations(String entityNameFromFileName, String fieldInfo) {

        // get index of first and last open brackets
        int bodyStart = fieldInfo.indexOf('{');
        int bodyEnd = fieldInfo.lastIndexOf('}');

        List<DomainEntityFieldAnnotation> list = new ArrayList<DomainEntityFieldAnnotation>();

        // no annotation found
        if ((bodyEnd < 0 && bodyStart >= 0) || (bodyEnd >= 0 && bodyStart < 0)) {
//            String errorMessage = "There is a problem with the bracket. There's one missing in the field : "+fieldInfo;
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "Invalid bracket usage : " + fieldInfo);
        }

        if (bodyEnd < 0 && bodyStart < 0) {
            if(fieldInfo.indexOf("]") > 0 && fieldInfo.indexOf("]")!= fieldInfo.length()-1 ) {
                if(!fieldInfo.substring(fieldInfo.indexOf("]")+1).trim().equals("")){
//                    String errorMessage = "There is a problem with the semilicon : "+fieldInfo;
//                    this.logger.error(errorMessage);
//                    throw new EntityParserException(errorMessage);
                    throwParsingError(entityNameFromFileName, "There is a problem with the semilicon : "+ fieldInfo); 
                }
            }
            return list;
        } else {
            
            if(!fieldInfo.substring(bodyEnd+1).trim().equals("") &&  !fieldInfo.substring(bodyEnd+1).trim().equals(";")) {
//                String errorMessage = "There is a problem with the semilicon : "+fieldInfo;
//                this.logger.error(errorMessage);
//                throw new EntityParserException(errorMessage);
                throwParsingError(entityNameFromFileName, "There is a problem with the semilicon : "+ fieldInfo); 
            }
        }
        bodyStart++;
        fieldInfo = fieldInfo.substring(bodyStart, bodyEnd).trim();

        // list of annotation found
        String[] annotationList = fieldInfo.split(",");
        // at least 1 annotation is required, if there are brackets
        if (annotationList.length < 1) {
//            String errorMessage = "There is no annotation in the given information";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "No annotation between brackets "+ fieldInfo); 
        }

        // extract annotations
        for (String annotationString : annotationList) {
            DomainEntityFieldAnnotation annotation = this.parseSingleAnnotation(entityNameFromFileName, annotationString.trim());
            list.add(annotation);
        }

        return list;
    }

    /**
     * @param entityNameFromFileName
     * @param annotationString
     * @return
     */
    private DomainEntityFieldAnnotation parseSingleAnnotation(String entityNameFromFileName, String annotationString) {
        // start with a @
        if (annotationString.charAt(0) != '@') {
//            String errorMessage = "An annotation must start with a '@' ";
//            this.logger.error(errorMessage);
//            throw new EntityParserException(errorMessage);
            throwParsingError(entityNameFromFileName, "An annotation must start with '@' "); 
        }

        // find the name of the annotation
        int end = annotationString.length();

        // check if annotation has a parameter
        boolean containsParam = false;
        String param = "";
        if (annotationString.contains("(")) {
            end = annotationString.indexOf('(');
            int endLess = end + 1 ;
            int endMore = annotationString.length() - 1 ;
            param = annotationString.substring(endLess, endMore).trim();
            if (param.equals("")) {
//                String errorMessage = "A parameter is required for this annotation : " + annotationString;
//                this.logger.error(errorMessage);
//                throw new EntityParserException(errorMessage);
                throwParsingError(entityNameFromFileName, "Parameter required for annotation : " + annotationString); 
            }
            containsParam = true;
        }


        String givenAnnotation = annotationString.substring(1, end).trim();

        // check annotation exist
//        String annotationAllowed = TelosysDSLProperties.getProperties().getProperty("annotations");
//        String[] listAllowed = annotationAllowed.split(",");
        List<String> listAllowed = KeyWords.getAnnotations();

        // find annotation
        for (String allowed : listAllowed) {
            if (allowed.contains(givenAnnotation)) {
                if (allowed.contains("#") && !containsParam) {
//                    String errorMessage = "A parameter is required for this annotation : " + givenAnnotation;
//                    this.logger.error(errorMessage);
//                    throw new EntityParserException(errorMessage);
                    throwParsingError(entityNameFromFileName, "Parameter required for annotation : " + givenAnnotation); 
                }
                if (!allowed.contains("#") && containsParam) {
//                    String errorMessage = "There is a not required parameter for this annotation : " + annotationString;
//                    this.logger.error(errorMessage);
//                    throw new EntityParserException(errorMessage);
                    throwParsingError(entityNameFromFileName, "Parameter not supported for annotation : " + givenAnnotation); 
                }

                if (containsParam) {
                    return new DomainEntityFieldAnnotation(givenAnnotation, param);
                } else {
                    return new DomainEntityFieldAnnotation(givenAnnotation);
                }
            }
        }

//        String errorMessage = "No annotation has been configured yet ";
//        this.logger.error(errorMessage);
//        throw new EntityParserException(errorMessage);

        throwParsingError(entityNameFromFileName, "Parameter not supported for annotation : " + givenAnnotation); 
        return null ; // never reached
    }

}