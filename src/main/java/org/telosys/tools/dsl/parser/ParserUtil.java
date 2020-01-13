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
import java.util.StringTokenizer;

import org.telosys.tools.dsl.DslParserException;

public class ParserUtil {
	
    private static final String COMMENT_REGEXP = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)" ;
	
    /**
     * Private constructor
     */
    private ParserUtil() { 
    }
    
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

    protected static String preprocessText (String initialText) {
        return flattenText( removeComments(initialText) );
    }

    //-------------------------------------------------------------------------------------------------
    // Names from file name
    //-------------------------------------------------------------------------------------------------
    private static final String DOT_MODEL           = ".model"  ;

    protected static void checkModelFile(File file) {
        if ( ! file.exists() ) {
            String textError = "File '" + file.toString() + "' not found";
            throw new DslParserException(textError);
        }
        if ( ! file.isFile() ) {
            String textError = "'" + file.toString() + "' is not a file";
            throw new DslParserException(textError);
        }
        if ( ! file.getName().endsWith(DOT_MODEL)) {
            String textError = "File '" + file.toString() + "' doesn't end with '" + DOT_MODEL + "'";
            throw new DslParserException(textError);
        }
    }

    /**
     * Remove quote char at the first and last position if any 
     * @param s
     * @param quoteChar
     * @return
     */
    protected static String unquote(String s, char quoteChar ) {
    	if ( s == null ) {
    		return s ;
    	}
    	if ( s.length() == 0  ) {
    		return s ;
    	}
    	int last = s.length()-1;
    	if ( s.charAt(0) == quoteChar && s.charAt(last) == quoteChar ) {
    		return s.substring(1, last);
    	}
    	else {
    		return s ;
    	}
    }
}
